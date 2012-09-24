package org.zephyrsoft.hibiscuswatcher;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
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
 * Fetch the information from Hibiscus.
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
		// Client-Config erzeugen
		XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
		try {
			config.setServerURL(new URL(url));
		} catch (MalformedURLException e) {
			e.printStackTrace();
			Starter.die(2, "could not parse URL");
		}
		config.setBasicUserName(username);
		config.setBasicPassword(password);
		
		if (url.startsWith("https"))
			disableCertCheck();
		
		XmlRpcClient client = new XmlRpcClient();
		client.setConfig(config);
		
		return client;
	}
	
	private static void disableCertCheck() {
		TrustManager dummy = new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}
			
			public void checkClientTrusted(X509Certificate[] certs, String authType) {
			}
			
			public void checkServerTrusted(X509Certificate[] certs, String authType) {
			}
		};
		
		SSLContext sc = null;
		try {
			sc = SSLContext.getInstance("SSL");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			Starter.die(3, "algorithm not found");
		}
		try {
			sc.init(null, new TrustManager[] {dummy}, new SecureRandom());
		} catch (KeyManagementException e) {
			e.printStackTrace();
			Starter.die(4, "key error");
		}
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		HostnameVerifier dummy2 = new HostnameVerifier() {
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
			Starter.die(5, "xml-rpc error while executing service " + methodName);
		}
		
		Object[] array = (Object[]) result;
		for (Object object : array) {
			@SuppressWarnings("unchecked")
			Map<String, String> fetched = (Map<String, String>) object;
			String name = fetched.get("bezeichnung");
			Account account = new Account(name);
			String balanceAsString = fetched.get("saldo");
			String balanceForParsing = balanceAsString;
			if (balanceForParsing.contains(",")) {
				// take care of German number formatting
				balanceForParsing = balanceForParsing.replaceAll("\\.", "").replaceAll(",", ".");
			}
			account.setBalance(new BigDecimal(balanceForParsing));
			String currency = fetched.get("waehrung");
			account.setCurrency(currency);
			String balanceDate = fetched.get("saldo_datum");
			account.setBalanceDate(balanceDate);
			ret.add(account);
		}
		
		Collections.sort(ret);
		
		return ret;
	}
	
}
