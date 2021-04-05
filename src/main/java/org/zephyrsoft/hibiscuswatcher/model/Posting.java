package org.zephyrsoft.hibiscuswatcher.model;

import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;

/**
 * Posting on a bank account.
 */
public class Posting {

	private BigDecimal amount;
	private String type;
	private String note;
	private String counterpartName;
	private String counterpartAccountNumber;
	private String counterpartBankCode;
	private String postingDate;

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

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
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

	public String getCounterpart() {
		if (StringUtils.isEmpty(counterpartName) && StringUtils.isEmpty(counterpartBankCode)
			&& StringUtils.isEmpty(counterpartAccountNumber)) {
			return "";
		}
		StringBuilder ret = new StringBuilder();
		if (StringUtils.isNotEmpty(counterpartName)) {
			ret.append(counterpartName);
			ret.append(" ");
		}
		if (StringUtils.isNotEmpty(counterpartAccountNumber)) {
			ret.append("(");
			ret.append(counterpartAccountNumber != null ? counterpartAccountNumber : "");
			if (StringUtils.isNotEmpty(counterpartAccountNumber) && StringUtils.isNotEmpty(counterpartBankCode)) {
				ret.append(" / ");
			}
			ret.append(counterpartBankCode != null ? counterpartBankCode : "");
			ret.append(")");
		}

		return ret.toString();
	}

	public String getPostingDate() {
		return postingDate;
	}

	public void setPostingDate(String postingDate) {
		this.postingDate = postingDate;
	}

}
