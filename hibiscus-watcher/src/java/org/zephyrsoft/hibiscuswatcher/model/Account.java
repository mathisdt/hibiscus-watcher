package org.zephyrsoft.hibiscuswatcher.model;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * A bank account.
 * 
 * @author Mathis Dirksen-Thedens
 */
public class Account implements Comparable<Account> {
	
	private static final int FRACTION_DIGITS = 2;
	
	private String name = "";
	private BigDecimal balance = new BigDecimal(0);
	private String currency = "";
	private String balanceDate = "";
	
	public Account(String name) {
		setName(name);
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public BigDecimal getBalance() {
		return balance;
	}
	
	public String getFormattedBalance() {
		NumberFormat df = new DecimalFormat("0.00");
		return df.format(balance.setScale(FRACTION_DIGITS, BigDecimal.ROUND_HALF_UP)) + " " + currency;
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
	
}
