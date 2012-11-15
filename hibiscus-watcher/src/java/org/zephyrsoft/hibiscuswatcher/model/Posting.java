package org.zephyrsoft.hibiscuswatcher.model;

import java.math.BigDecimal;

/**
 * Posting on a bank account.
 * 
 * @author Mathis Dirksen-Thedens
 */
public class Posting {
	
	private BigDecimal amount;
	private String type;
	private String counterpartName;
	private String counterpartAccountNumber;
	private String counterpartBankCode;
	
	public BigDecimal getAmount() {
		return amount;
	}
	
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getCounterpartName() {
		return counterpartName;
	}
	
	public void setCounterpartName(String counterpartName) {
		this.counterpartName = counterpartName;
	}
	
	public String getCounterpartAccountNumber() {
		return counterpartAccountNumber;
	}
	
	public void setCounterpartAccountNumber(String counterpartAccountNumber) {
		this.counterpartAccountNumber = counterpartAccountNumber;
	}
	
	public String getCounterpartBankCode() {
		return counterpartBankCode;
	}
	
	public void setCounterpartBankCode(String counterpartBankCode) {
		this.counterpartBankCode = counterpartBankCode;
	}
	
}
