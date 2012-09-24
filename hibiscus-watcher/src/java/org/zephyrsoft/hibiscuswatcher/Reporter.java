package org.zephyrsoft.hibiscuswatcher;

import java.math.BigDecimal;
import java.util.List;
import org.zephyrsoft.hibiscuswatcher.model.Account;

/**
 * Create a report.
 * 
 * @author Mathis Dirksen-Thedens
 */
public class Reporter {
	
	private static final int SPACE_BETWEEN_COLUMNS = 3;
	
	public static String generateBalancesReport(List<Account> accounts) {
		StringBuilder ret = new StringBuilder();
		
		int maxNameLength = 0;
		int maxFormattedBalanceLength = 0;
		BigDecimal sum = new BigDecimal(0);
		String currency = "";
		for (Account account : accounts) {
			String name = account.getName();
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
		
		Account sumAccount = new Account("");
		sumAccount.setBalance(sum);
		sumAccount.setCurrency(currency);
		accounts.add(sumAccount);
		
		for (Account account : accounts) {
			String name = account.getName();
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
			if (balanceDate != null && !balanceDate.isEmpty()) {
				ret.append("(");
				ret.append(balanceDate);
				ret.append(")");
			}
			ret.append("\n");
		}
		
		return ret.toString();
	}
}
