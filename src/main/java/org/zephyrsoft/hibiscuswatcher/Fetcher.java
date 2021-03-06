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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.lang3.StringUtils;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.zephyrsoft.hibiscuswatcher.model.Account;
import org.zephyrsoft.hibiscuswatcher.model.Posting;

/**
 * Fetch information from Hibiscus.
 */
public class Fetcher {

	private static final String REGEX_IBAN = "I\\s*B\\s*A\\s*N\\s*:\\s*([A-Z]\\s*[A-Z]\\s*\\d\\s*\\d\\s*\\d\\s*\\d\\s*\\d\\s*\\d\\s*\\d\\s*\\d\\s*\\d\\s*\\d\\s*\\d\\s*\\d\\s*\\d\\s*\\d\\s*\\d\\s*\\d\\s*\\d\\s*\\d\\s*\\d\\s*\\d\\s*)";
	private static final String REGEX_BIC = "B\\s*I\\s*C\\s*:\\s*(\\w\\s*\\w\\s*\\w\\s*\\w\\s*\\w\\s*\\w\\s*\\w\\s*\\w\\s*\\w?+\\s*+\\w?+\\s*+\\w?+\\s*+)";

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
				sc.init(null, new TrustManager[] { dummy }, new SecureRandom());
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

	private static BigDecimal parseBigDecimal(String in) {
		if (in.contains(",")) {
			// take care of the German number formatting of Hibiscus
			in = in.replaceAll("\\.", "").replaceAll(",", ".");
		}
		return new BigDecimal(in);
	}

	public List<Account> fetchAccountsWithBalances() {
		List<Account> ret = new ArrayList<>();

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
				String name = fetched.get("name");
				String iban = fetched.get("iban");
				Account account = new Account(name, iban);
				account.setID(fetched.get("id"));
				String balanceAsString = fetched.get("saldo");
				account.setBalance(parseBigDecimal(balanceAsString));
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

	public List<Account> fetchAccountsWithPostings(int daysToFetch, boolean noPositive, boolean noNegative) {
		List<Account> ret = fetchAccountsWithBalances();

		XmlRpcClient client = createXmlRpcClient();
		String methodName = "hibiscus.xmlrpc.umsatz.list";

		Map<String, String> params = new HashMap<>();
		GregorianCalendar beginOfPeriod = new GregorianCalendar();
		beginOfPeriod.add(Calendar.DAY_OF_MONTH, -1 * daysToFetch);
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		params.put("datum:min", sdf.format(beginOfPeriod.getTime()));

		Object result = null;
		try {
			result = client.execute(methodName, new Object[] { params });
		} catch (XmlRpcException e) {
			e.printStackTrace();
			Starter.die("xml-rpc error while executing service " + methodName);
		}

		Object[] array = (Object[]) result;
		if (array != null) {
			for (Object object : array) {
				@SuppressWarnings("unchecked")
				Map<String, String> fetched = (Map<String, String>) object;
				String amountAsString = fetched.get("betrag");
				BigDecimal amount = parseBigDecimal(amountAsString);
				if ((noPositive && amount.signum() == 1) || (noNegative && amount.signum() == -1)) {
					// do not include this posting
					// (although the effect of this posting will stay visible in the balance)
					continue;
				}
				Posting posting = new Posting();
				posting.setType(fetched.get("art"));
				posting.setPostingDate(fetched.get("datum"));
				posting.setCounterpartName(fetched.get("empfaenger_name"));
				String note = fetched.get("zweck");
				String counterpartAccountNumber = fetched.get("empfaenger_konto");
				String counterpartBankCode = fetched.get("empfaenger_blz");
				if (StringUtils.isAnyEmpty(counterpartAccountNumber, counterpartBankCode)) {
					// try to extract IBAN and BIC from note
					counterpartAccountNumber = find(REGEX_IBAN, note);
					counterpartBankCode = find(REGEX_BIC, note);
					// remove IBAN and BIC from note
					note = note.replaceAll(REGEX_IBAN, "");
					note = note.replaceAll(REGEX_BIC, "");
					note = note.replaceAll("\\s{2,}", " ");
				}
				posting.setNote(note);
				posting.setCounterpartAccountNumber(counterpartAccountNumber);
				posting.setCounterpartBankCode(counterpartBankCode);
				posting.setAmount(amount);
				String accountId = fetched.get("konto_id");
				Account relatedAccount = findAccount(ret, accountId);
				if (relatedAccount != null) {
					relatedAccount.addPosting(posting);
				} else {
					System.err.println("account with ID " + accountId + " not found");
				}
			}
		}

		return ret;
	}

	private String find(String regex, String haystack) {
		Matcher matcher = Pattern.compile(regex).matcher(haystack);
		if (!matcher.find(0)) {
			return "";
		} else {
			return matcher.group(1).replaceAll("\\s", "");
		}
	}

	private static Account findAccount(List<Account> accounts, String accountId) {
		for (Account account : accounts) {
			if (account.getID() != null && account.getID().equals(accountId)) {
				return account;
			}
		}
		return null;
	}

}
