package org.zephyrsoft.hibiscuswatcher.model;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * A bank account.
 * 
 * @author Mathis Dirksen-Thedens
 */
public class Account implements Comparable<Account>, Iterable<Posting> {
	
	private static final int FRACTION_DIGITS = 2;
	
	private String name = "";
	private String id = "";
	private BigDecimal balance = new BigDecimal(0);
	private String currency = "";
	private String balanceDate = "";
	private List<Posting> postings = new LinkedList<Posting>();
	
	public Account(String name) {
		setName(name);
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
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
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
		return df.format(amount.setScale(FRACTION_DIGITS, BigDecimal.ROUND_HALF_UP)) + " " + currency;
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
		if (o == null || (getName() != null && o.getName() == null)) {
			return 1;
		} else if (getName() == null && o.getName() != null) {
			return -1;
		} else {
			return getName().compareTo(o.getName());
		}
	}
	
	@Override
	public Iterator<Posting> iterator() {
		return postings.iterator();
	}
	
}
