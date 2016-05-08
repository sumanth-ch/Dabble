/**
 * 
 */
package com.fourvector.apps.dabble.model.payment;

import com.fourvector.apps.dabble.dao.config.DAOUtils;
import com.fourvector.apps.dabble.model.Auditable;

/**
 * @author Anantha.Sharma
 */
public class TransactionDetailModel extends TransactionModel implements Auditable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2737630843404290776L;

	private TransactionModel	transaction;
	private boolean				captured;

	/**
	 * 
	 */
	public TransactionDetailModel() {
		super();
	}

	/**
	 * @return the transaction
	 */
	public TransactionModel getTransaction() {
		return transaction;
	}

	/**
	 * @param transaction
	 *            the transaction to set
	 */
	public void setTransaction(TransactionModel transaction) {
		this.transaction = transaction;
	}

	/**
	 * @return the captured
	 */
	public boolean isCaptured() {
		return captured;
	}

	/**
	 * @return the captured
	 */
	public boolean getCaptured() {
		return captured;
	}

	/**
	 * @param captured
	 *            the captured to set
	 */
	public void setCaptured(boolean captured) {
		this.captured = captured;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("TransactionDetailModel [transaction=%s, captured=%s]", DAOUtils.showEntity(transaction), captured);
	}

}
