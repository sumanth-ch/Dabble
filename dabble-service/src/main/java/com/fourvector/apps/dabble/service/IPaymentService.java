/**
 * 
 */
package com.fourvector.apps.dabble.service;

import java.util.List;

import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fourvector.apps.dabble.common.dto.CreditCardDTO;
import com.fourvector.apps.dabble.common.dto.SubscriptionDTO;
import com.fourvector.apps.dabble.model.payment.SubscriptionModel;
import com.fourvector.apps.dabble.model.user.UserModel;

/**
 * @author Anantha.Sharma
 */
/**
 * @author asharma
 */
public interface IPaymentService {
	/**
	 * perform a sale with Brain-tree.
	 * 
	 * @param userId
	 * @param jobId
	 * @param cardNumber
	 * @param expiryDate
	 * @param amount
	 * @param currency
	 * @return
	 */
// @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW, readOnly = false)
// String performSale(Integer userId, Integer jobId, String cardNumber, String expiryDate, Double amount, String currency);

	/**
	 * pre authorizes the sale, returns the amount the user needs to pay.
	 * 
	 * @param userId
	 * @return
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW, readOnly = true)
	SubscriptionDTO preAuthorizeSale(Integer userId);

	/**
	 * performs a refund operation with brain-tree.
	 * 
	 * @param userId
	 * @param jobId
	 * @param amount
	 * @param currency
	 * @return
	 */
// String performRefund(Integer userId, Integer jobId, Double amount, String currency);
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW, readOnly = false)
	void createCustomer(Integer userId);

	/**
	 * adds a card to a customer
	 * 
	 * @param userId
	 * @param cardNumber
	 * @param expiryDate
	 * @param cvv
	 * @param nonce
	 * @return
	 */
	String addCardToCustomer(Integer userId, String cardNumber, String expiryDate, String cvv, String nonce);

	/**
	 * performs a sale
	 * 
	 * @param userId
	 * @param jobId
	 * @param amount
	 * @param paymentMethodNonce
	 * @return
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, readOnly = false)
	void performSale(Integer userId, Integer jobId, Double amount, String paymentMethodNonce);

	/**
	 * returns a list of creditcards.
	 * 
	 * @param userId
	 * @return
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW, readOnly = true)
	List<CreditCardDTO> getCardList(Integer userId);

	/**
	 * generate a client token for a given user and return the token string.
	 * 
	 * @param userId
	 * @return
	 */
	String getClientToken(Integer userId);

	/**
	 * @param id
	 * @param piId
	 * @param subscriptionModel
	 * @param nonce
	 * @return
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW, readOnly = true)
	String setupRecurringPayments(Integer id, String piId, SubscriptionModel subscriptionModel, String nonce);

	/**
	 * @param userModel
	 * @param subscriptionReference
	 */
	void cancelSubscription(UserModel userModel, String subscriptionReference);

	void createCustomer(UserModel userModel, String email, String phone);

	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW, readOnly = false)
	void testSubscription(Integer user);

}
