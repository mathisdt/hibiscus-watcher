package org.zephyrsoft.hibiscuswatcher;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.zephyrsoft.hibiscuswatcher.model.Account;
import org.zephyrsoft.hibiscuswatcher.model.Posting;

/**
 * Create reports.
 * 
 * @author Mathis Dirksen-Thedens
 */
public class Reporter {
	
	private static final int SPACE_BETWEEN_COLUMNS = 3;
	
	/**
	 * Create a report over all given accounts stating the name and the current balance for each one, plus one line at
	 * the bottom containing the sum.
	 */
	public static String generateBalancesReport(List<Account> accounts) {
		StringBuilder ret = new StringBuilder();
		
		int maxNameLength = 0;
		int maxFormattedBalanceLength = 0;
		BigDecimal sum = new BigDecimal(0);
		String currency = "";
		for (Account account : accounts) {
			String name = account.getDisplayName();
			if (name != null) {
				maxNameLength = Math.max(name.length(), maxNameLength);
			}
			String formattedBalance = account.getFormattedBalance();
			if (formattedBalance != null) {
				maxFormattedBalanceLength = Math.max(formattedBalance.length(), maxFormattedBalanceLength);
			}
			BigDecimal balance = account.getBalance();
			sum = sum.add(balance);
			currency = account.getCurrency();
		}
		
		Account sumAccount = new Account("", "");
		sumAccount.setBalance(sum);
		sumAccount.setCurrency(currency);
		accounts.add(sumAccount);
		
		for (Account account : accounts) {
			String name = account.getDisplayName();
			String formattedBalance = account.getFormattedBalance();
			String balanceDate = account.getBalanceDate();
			
			ret.append(name);
			for (int i = 0; i < maxNameLength - name.length() + SPACE_BETWEEN_COLUMNS + maxFormattedBalanceLength
				- formattedBalance.length(); i++) {
				ret.append(" ");
			}
			ret.append(formattedBalance);
			for (int i = 0; i < SPACE_BETWEEN_COLUMNS; i++) {
				ret.append(" ");
			}
			if (isNotEmpty(balanceDate)) {
				ret.append("(");
				ret.append(balanceDate);
				ret.append(")");
			}
			ret.append("\n");
		}
		
		return ret.toString();
	}
	
	/**
	 * Generate a report over all given accounts stating the name, the balance and all available postings.
	 */
	public static String generatePostingsReport(List<Account> accounts) {
		StringBuilder ret = new StringBuilder();
		
		boolean isFirst = true;
		for (Account account : accounts) {
			if (isFirst) {
				isFirst = false;
			} else {
				ret.append("- - - - - - - - - - - - - - - - - -\n\n");
			}
			String currency = account.getCurrency();
			ret.append(account.getDisplayName());
			ret.append("\nSaldo: ");
			ret.append(account.getFormattedBalance());
			String balanceDate = account.getBalanceDate();
			if (isNotEmpty(balanceDate)) {
				ret.append(" (");
				ret.append(balanceDate);
				ret.append(")");
			}
			ret.append("\n\n");
			
			for (Posting posting : account) {
				ret.append(posting.getCounterpart());
				ret.append("\n");
				
				String note = posting.getNote();
				if (isNotEmpty(note)) {
					ret.append(note);
					ret.append("\n");
				}
				ret.append(Account.getFormattedBalance(posting.getAmount(), currency));
				String type = posting.getType();
				String date = posting.getPostingDate();
				if (isNotEmpty(type) || isNotEmpty(date)) {
					ret.append(" (");
				}
				if (isNotEmpty(type)) {
					ret.append(type);
				}
				if (isNotEmpty(type) && isNotEmpty(date)) {
					ret.append(", ");
				}
				if (isNotEmpty(date)) {
					ret.append(formatDate(date));
				}
				if (isNotEmpty(type) || isNotEmpty(date)) {
					ret.append(")");
				}
				ret.append("\n\n");
			}
			
		}
		
		return ret.toString();
	}
	
	private static String formatDate(String in) {
		SimpleDateFormat sdfIn = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdfOut = new SimpleDateFormat("dd.MM.yyyy");
		try {
			Date out = sdfIn.parse(in);
			return sdfOut.format(out);
		} catch (ParseException e) {
			return in;
		}
	}
	
	private static boolean isNotEmpty(String in) {
		return in != null && !in.isEmpty();
	}
}
