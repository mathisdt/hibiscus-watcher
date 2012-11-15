package org.zephyrsoft.hibiscuswatcher;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.zephyrsoft.hibiscuswatcher.model.Account;

/**
 * Fetch information from Hibiscus.
 * 
 * @author Mathis Dirksen-Thedens
 */
public class Fetcher {
	
	private final String url;
	private final String username;
	private final String password;
	
	public Fetcher(String url, String username, String password) {
		this.url = url;
		this.username = username;
		this.password = password;
	}
	
	private XmlRpcClient createXmlRpcClient() {
		// create client configuration
		XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
		try {
			config.setServerURL(new URL(url));
		} catch (MalformedURLException e) {
			e.printStackTrace();
			Starter.die("could not parse URL");
		}
		config.setBasicUserName(username);
		config.setBasicPassword(password);
		
		// ignore self-signed certificate errors
		if (url.startsWith("https")) {
			disableCertCheck();
		}
		
		XmlRpcClient client = new XmlRpcClient();
		client.setConfig(config);
		
		return client;
	}
	
	private static void disableCertCheck() {
		TrustManager dummy = new X509TrustManager() {
			@Override
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}
			
			@Override
			public void checkClientTrusted(X509Certificate[] certs, String authType) {
				// nothing to do
			}
			
			@Override
			public void checkServerTrusted(X509Certificate[] certs, String authType) {
				// nothing to do
			}
		};
		
		SSLContext sc = null;
		try {
			sc = SSLContext.getInstance("SSL");
			try {
				sc.init(null, new TrustManager[] {dummy}, new SecureRandom());
			} catch (KeyManagementException e) {
				e.printStackTrace();
				Starter.die("key error");
			}
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			Starter.die("algorithm not found");
		}
		HostnameVerifier dummy2 = new HostnameVerifier() {
			@Override
			public boolean verify(String host, SSLSession session) {
				return true;
			}
		};
		HttpsURLConnection.setDefaultHostnameVerifier(dummy2);
	}
	
	public List<Account> fetchAccountsWithBalances() {
		List<Account> ret = new ArrayList<Account>();
		
		XmlRpcClient client = createXmlRpcClient();
		String methodName = "hibiscus.xmlrpc.konto.find";
		Object result = null;
		try {
			result = client.execute(methodName, Collections.EMPTY_LIST);
		} catch (XmlRpcException e) {
			e.printStackTrace();
			Starter.die("xml-rpc error while executing service " + methodName);
		}
		
		Object[] array = (Object[]) result;
		if (array != null) {
			for (Object object : array) {
				@SuppressWarnings("unchecked")
				Map<String, String> fetched = (Map<String, String>) object;
				String name = fetched.get("bezeichnung");
				Account account = new Account(name);
				String balanceAsString = fetched.get("saldo");
				String balanceForParsing = balanceAsString;
				if (balanceForParsing.contains(",")) {
					// take care of the German number formatting of Hibiscus
					balanceForParsing = balanceForParsing.replaceAll("\\.", "").replaceAll(",", ".");
				}
				account.setBalance(new BigDecimal(balanceForParsing));
				String currency = fetched.get("waehrung");
				account.setCurrency(currency);
				String balanceDate = fetched.get("saldo_datum");
				account.setBalanceDate(balanceDate);
				ret.add(account);
			}
		}
		
		Collections.sort(ret);
		
		return ret;
	}
	
	public List<Account> fetchAccountsWithPostings() {
		List<Account> ret = fetchAccountsWithBalances();
		
		XmlRpcClient client = createXmlRpcClient();
		String methodName = "hibiscus.xmlrpc.umsatz.list";
		
		Map<String, String> params = new HashMap<String, String>();
		GregorianCalendar oneWeekAgo = new GregorianCalendar();
		oneWeekAgo.add(Calendar.DAY_OF_MONTH, -7);
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		params.put("datum:min", sdf.format(oneWeekAgo.getTime()));
		
		Object result = null;
		try {
			result = client.execute(methodName, new Object[] {params});
		} catch (XmlRpcException e) {
			e.printStackTrace();
			Starter.die("xml-rpc error while executing service " + methodName);
		}
		
		Object[] array = (Object[]) result;
		if (array != null) {
			for (Object object : array) {
				@SuppressWarnings("unchecked")
				Map<String, String> fetched = (Map<String, String>) object;
				// TODO
			}
		}
		
		return ret;
	}
	
}
