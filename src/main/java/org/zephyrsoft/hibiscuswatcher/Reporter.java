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
 */
public class Reporter {

	private static final int SPACE_BETWEEN_COLUMNS = 3;

	/**
	 * Create a report over all given accounts stating the name and the current balance for each one, plus one line at
	 * the bottom containing the sum.
	 */
	public static String generateBalancesReport(List<Account> accounts, boolean printSum) {
		StringBuilder ret = new StringBuilder();

		if (printSum) {
			BigDecimal sum = new BigDecimal(0);
			String currency = "";
			for (Account account : accounts) {
				BigDecimal balance = account.getBalance();
				sum = sum.add(balance);
				currency = account.getCurrency();
			}

			Account sumAccount = new Account("", "");
			sumAccount.setBalance(sum);
			sumAccount.setCurrency(currency);
			accounts.add(sumAccount);
		}

		int maxDisplayNameLength = accounts.stream()
			.mapToInt(account -> account.getDisplayName() == null ? 0 : account.getDisplayName().length())
			.max().getAsInt();
		int maxFormattedBalanceLength = accounts.stream()
			.mapToInt(account -> account.getFormattedBalance() == null ? 0 : account.getFormattedBalance().length())
			.max().getAsInt();

		for (Account account : accounts) {
			String name = account.getDisplayName();
			String formattedBalance = account.getFormattedBalance();
			String balanceDate = account.getBalanceDate();

			ret.append(name);
			for (int i = 0; i < maxDisplayNameLength - name.length() + SPACE_BETWEEN_COLUMNS
				+ maxFormattedBalanceLength
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
	 * Create output only if the given account's balance is less than the minimum.
	 */
	public static String generateLowReport(List<Account> accounts, List<String> targetAccounts, Double minimumBalance) {

		StringBuilder ret = new StringBuilder();

		int maxNameLength = 0;
		int maxFormattedBalanceLength = 0;
		for (Account account : accounts) {
			if (!isUnderMinimum(account, minimumBalance) || !targetAccounts.contains(account.getIban())) {
				continue;
			}

			String name = account.getDisplayName();
			if (name != null) {
				maxNameLength = Math.max(name.length(), maxNameLength);
			}
			String formattedBalance = account.getFormattedBalance();
			if (formattedBalance != null) {
				maxFormattedBalanceLength = Math.max(formattedBalance.length(), maxFormattedBalanceLength);
			}
		}

		for (Account account : accounts) {
			if (!isUnderMinimum(account, minimumBalance) || !targetAccounts.contains(account.getIban())) {
				continue;
			}

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

	private static boolean isUnderMinimum(Account account, Double minimumBalance) {
		return account.getBalance().doubleValue() <= minimumBalance.doubleValue();
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
				ret.append("===================================\n\n");
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
			ret.append("\n\n- - - - - - - - - - - - - - - - - -\n\n");

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

			if (account.getPostingCount() == 0) {
				ret.append("no changes\n\n");
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
