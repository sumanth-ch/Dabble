/**
 * 
 */
package com.fourvector.apps.dabble.model.payment;

import com.fourvector.apps.dabble.model.BaseModel;

/**
 * @author Anantha.Sharma
 */
public class PaymentTypeModel extends BaseModel {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 5716104657528929259L;
	private String				description;

	/**
	 * 
	 */
	public PaymentTypeModel() {
		super();
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(final String description) {
		this.description = description;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("TransactionType [description=%s, id=%s, status=%s]", this.description, this.id, this.status);
	}

}
