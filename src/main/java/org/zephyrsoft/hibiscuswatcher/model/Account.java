package org.zephyrsoft.hibiscuswatcher.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.ComparisonChain;

/**
 * A bank account.
 */
public class Account implements Comparable<Account>, Iterable<Posting> {

	private static final int FRACTION_DIGITS = 2;

	private String name = "";
	private String iban = "";
	private String id = "";
	private BigDecimal balance = new BigDecimal(0);
	private String currency = "";
	private String balanceDate = "";
	private List<Posting> postings = new ArrayList<>();

	public Account(String name, String iban) {
		setName(name);
		setIban(iban);
	}

	public boolean addPosting(Posting arg0) {
		return postings.add(arg0);
	}

	public void clearPostings() {
		postings.clear();
	}

	public Posting getPosting(int arg0) {
		return postings.get(arg0);
	}

	public boolean hasPostings() {
		return !postings.isEmpty();
	}

	public int postingsCount() {
		return postings.size();
	}

	public String getDisplayName() {
		return name + (nameAndIbanFilled() ? " / " : "") + iban;
	}

	private boolean nameAndIbanFilled() {
		return name != null && !name.isEmpty() && iban != null && !iban.isEmpty();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIban() {
		return iban;
	}

	public String getIbanWithoutChecksum() {
		return iban == null || iban.length() < 5 ? iban : iban.substring(0, 2) + iban.substring(4);
	}

	public void setIban(String iban) {
		this.iban = iban;
	}

	public String getID() {
		return id;
	}

	public void setID(String id) {
		this.id = id;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public String getFormattedBalance() {
		return getFormattedBalance(balance, currency);
	}

	public static String getFormattedBalance(BigDecimal amount, String currency) {
		NumberFormat df = new DecimalFormat("0.00");
		return df.format(amount.setScale(FRACTION_DIGITS, RoundingMode.HALF_UP)) + " " + currency;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getBalanceDate() {
		return balanceDate;
	}

	public void setBalanceDate(String balanceDate) {
		this.balanceDate = balanceDate;
	}

	@Override
	public int compareTo(Account o) {
		return ComparisonChain.start().compare(getName(), o.getName()).compare(getIbanWithoutChecksum(),
			o.getIbanWithoutChecksum()).result();
	}

	@Override
	public Iterator<Posting> iterator() {
		return postings.iterator();
	}

	public int getPostingCount() {
		return postings.size();
	}

}
