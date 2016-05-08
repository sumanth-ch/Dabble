/**
 * 
 */
package com.fourvector.apps.dabble.common.dto;

/**
 * @author asharma
 */
public class TransactionDTO extends BaseDTO {

	private Double	amount;
	private String	currency;
	private Double	fee;
	private Double	totalPayable;
	private String	quota;

	/**
	 * 
	 */
	public TransactionDTO() {
		super();
	}

	/**
	 * @param amount
	 * @param currency
	 * @param fee
	 * @param totalPayable
	 * @param quota
	 */
	public TransactionDTO(Double amount, String currency, Double fee, Double totalPayable, String quota) {
		super();
		this.amount = amount;
		this.currency = currency;
		this.fee = fee;
		this.totalPayable = totalPayable;
		this.quota = quota;
	}

	/**
	 * @return the amount
	 */
	public Double getAmount() {
		return amount;
	}

	/**
	 * @param amount
	 *            the amount to set
	 */
	public void setAmount(Double amount) {
		this.amount = amount;
	}

	/**
	 * @return the currency
	 */
	public String getCurrency() {
		return currency;
	}

	/**
	 * @param currency
	 *            the currency to set
	 */
	public void setCurrency(String currency) {
		this.currency = currency;
	}

	/**
	 * @return the fee
	 */
	public Double getFee() {
		return fee;
	}

	/**
	 * @param fee
	 *            the fee to set
	 */
	public void setFee(Double fee) {
		this.fee = fee;
	}

	/**
	 * @return the totalPayable
	 */
	public Double getTotalPayable() {
		return totalPayable;
	}

	/**
	 * @param totalPayable
	 *            the totalPayable to set
	 */
	public void setTotalPayable(Double totalPayable) {
		this.totalPayable = totalPayable;
	}

	/**
	 * @return the quota
	 */
	public String getQuota() {
		return quota;
	}

	/**
	 * @param quota
	 *            the quota to set
	 */
	public void setQuota(String quota) {
		this.quota = quota;
	}

}
