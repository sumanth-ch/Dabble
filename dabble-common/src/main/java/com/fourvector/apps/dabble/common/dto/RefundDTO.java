/**
 * 
 */
package com.fourvector.apps.dabble.common.dto;

/**
 * @author asharma
 */
public class RefundDTO extends BaseDTO {

	private Double	percentageCharged;
	private Double	amountRefundable;
	private Double	originalJobCost;

	private String currency;

	/**
	 * 
	 */
	public RefundDTO() {
		super();
	}

	/**
	 * @return the percentageCharged
	 */
	public Double getPercentageCharged() {
		return percentageCharged;
	}

	/**
	 * @param percentageCharged the percentageCharged to set
	 */
	public void setPercentageCharged(Double percentageCharged) {
		this.percentageCharged = percentageCharged;
	}

	/**
	 * @return the amountRefundable
	 */
	public Double getAmountRefundable() {
		return amountRefundable;
	}

	/**
	 * @param amountRefundable the amountRefundable to set
	 */
	public void setAmountRefundable(Double amountRefundable) {
		this.amountRefundable = amountRefundable;
	}

	/**
	 * @return the originalJobCost
	 */
	public Double getOriginalJobCost() {
		return originalJobCost;
	}

	/**
	 * @param originalJobCost the originalJobCost to set
	 */
	public void setOriginalJobCost(Double originalJobCost) {
		this.originalJobCost = originalJobCost;
	}

	/**
	 * @return the currency
	 */
	public String getCurrency() {
		return currency;
	}

	/**
	 * @param currency the currency to set
	 */
	public void setCurrency(String currency) {
		this.currency = currency;
	}

}
