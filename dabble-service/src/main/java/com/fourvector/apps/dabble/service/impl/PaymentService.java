/**
* 
*/
package com.fourvector.apps.dabble.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.ClientTokenRequest;
import com.braintreegateway.CreditCard;
import com.braintreegateway.Customer;
import com.braintreegateway.CustomerRequest;
import com.braintreegateway.Environment;
import com.braintreegateway.PaymentMethod;
import com.braintreegateway.PaymentMethodRequest;
import com.braintreegateway.Result;
import com.braintreegateway.Subscription;
import com.braintreegateway.SubscriptionRequest;
import com.braintreegateway.Transaction;
import com.braintreegateway.TransactionRequest;
import com.braintreegateway.ValidationError;
import com.braintreegateway.exceptions.NotFoundException;
import com.fourvector.apps.dabble.common.dto.CreditCardDTO;
import com.fourvector.apps.dabble.common.dto.SubscriptionDTO;
import com.fourvector.apps.dabble.dao.IPaymentDAO;
import com.fourvector.apps.dabble.dao.config.DAOConstants;
import com.fourvector.apps.dabble.dao.config.Status;
import com.fourvector.apps.dabble.exception.DabbleException;
import com.fourvector.apps.dabble.model.payment.SubscriptionModel;
import com.fourvector.apps.dabble.model.payment.TransactionModel;
import com.fourvector.apps.dabble.model.user.UserModel;
import com.fourvector.apps.dabble.model.user.UserSubscriptionModel;
import com.fourvector.apps.dabble.service.IPaymentService;
import com.fourvector.apps.dabble.service.config.ResponseStatus;
import com.fourvector.apps.dabble.service.config.TargetEnvironment;

/**
 * @author Anantha.Sharma
 */
public class PaymentService extends BaseService implements IPaymentService, InitializingBean {
	private static final org.slf4j.Logger	LOG	= org.slf4j.LoggerFactory.getLogger(PaymentService.class);
	private Environment						environment;
	private String							merchantId;
	private String							publicKey;
	private String							privateKey;
	private TargetEnvironment				targetEnvironment;
	private IPaymentDAO						paymentDAO;
	private BraintreeGateway				gateway;

	/**
	 * 
	 */
	public PaymentService() {
		super();
	}

	@Override
	public void testSubscription(Integer user) {
		LOG.debug("Method [testSubscription]: Called");

		System.out.println(paymentDAO.getActiveSubscription(user));

		LOG.debug("Method [testSubscription]: Returning.");

	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		LOG.trace("Method [afterPropertiesSet]: Called ");

		if (targetEnvironment == null) {
			LOG.info("Environment setup incomplete, defaulting to SANDBOX");
			targetEnvironment = TargetEnvironment.SANDBOX;
		}
		switch (targetEnvironment) {
			case PRODUCTION:
				environment = Environment.PRODUCTION;
				break;
			case SANDBOX:
				environment = Environment.SANDBOX;
				break;
		}

		Assert.notNull(paymentDAO, "PaymentDAO is null, please check the configuration");
		Assert.notNull(merchantId, "Braintree Config: Merchant ID cannot be null");
		Assert.notNull(publicKey, "Braintree Config: Public Key cannot be null");
		Assert.notNull(privateKey, "Braintree Config: Private Key cannot be null");
		gateway = new BraintreeGateway(environment, merchantId, publicKey, privateKey);

		LOG.trace("Method [afterPropertiesSet]: Returning ");

	}

	@Override
	public String setupRecurringPayments(Integer userId, String piId, SubscriptionModel subscriptionModel, String nonce) {
		LOG.debug("Method [setupRecurringPayments]: Called");

		UserModel userModel = paymentDAO.findById(UserModel.class, userId);
		verifyUser(userModel);
		String paymentToken = null;
		List<CreditCard> cards = gateway.customer().find(userModel.getBraintreeReference()).getCreditCards();

		for (CreditCard cc : cards) {
			if (cc.getUniqueNumberIdentifier().equals(piId)) {
				paymentToken = cc.getToken();
				break;
			}
		}
		if (paymentToken == null) {
			throw new DabbleException(ResponseStatus.UNAUTHORISED_ACCESS, "Payment Instrument not configured for the calling customer.");
		}

		SubscriptionRequest request = new SubscriptionRequest().paymentMethodToken(paymentToken).paymentMethodNonce(nonce).planId(subscriptionModel.getPlanReference()).options().startImmediately(true).done();
		Result<Subscription> result = gateway.subscription().create(request);
		if (!result.isSuccess()) {
			throw new DabbleException(ResponseStatus.INCOMPLETE_DATA, result.getMessage());
		}
		String id = result.getTarget().getId();
		LOG.debug("Method [setupRecurringPayments]: Returning {}.", id);
		return id;
	}

	@Override
	public void cancelSubscription(UserModel userModel, String subscriptionReference) {
		LOG.debug("Method [cancelSubscription]: Called");

		Result<Subscription> result = gateway.subscription().cancel(subscriptionReference);
		if (result.isSuccess()) {
			// all is well
		} else {
			throw new DabbleException(ResponseStatus.INCOMPLETE_DATA, "Unable to cancel subscription, please try again");
		}

		LOG.debug("Method [cancelSubscription]: Returning");
	}

	@Override
	public List<CreditCardDTO> getCardList(Integer userId) {
		LOG.trace("Method [getCardList]: Called ");

		List<CreditCardDTO> cardDTOs = new LinkedList<>();

		UserModel userModel = paymentDAO.findById(UserModel.class, userId);
		verifyUser(userModel);
		Customer customer = gateway.customer().find(userModel.getBraintreeReference());
		if (customer == null) {
			// no customer found, something is wrong here.
			throw new DabbleException(ResponseStatus.INACTIVE, "customer not found");
		}
		for (CreditCard cc : customer.getCreditCards()) {
			CreditCardDTO dto = new CreditCardDTO();
			dto.setCardNumber(cc.getMaskedNumber());
			dto.setCardType(cc.getCardType());
			dto.setToken(cc.getToken());
			dto.setExpiryDate(cc.getExpirationDate());
			dto.setId(cc.getUniqueNumberIdentifier());
			cardDTOs.add(dto);

		}

		LOG.trace("Method [getCardList]: Returning {} elements.", cardDTOs.size());
		return cardDTOs;

	}

	@Override
	public SubscriptionDTO preAuthorizeSale(Integer userId) {
		LOG.trace("Method [preAuthorizeSale]: Called ");
		UserModel userModel = paymentDAO.findById(UserModel.class, userId);
		verifyUser(userModel);
		String hql = "from " + UserSubscriptionModel.class + " p where p.user=? and p.status=?";
		SubscriptionModel subscriptionModel = (SubscriptionModel) paymentDAO.runSingleResponseQuery(hql, userModel, Status.ACTIVE);
		SubscriptionDTO dto = new SubscriptionDTO();
		prepareSubscriptionDTO(dto, subscriptionModel);
		LOG.trace("Method [preAuthorizeSale]: Returning {} ", dto);
		return dto;
	}

	@Override
	public void performSale(Integer userId, Integer jobId, Double amount, String paymentMethodNonce) {
		LOG.debug("Method [performSale]: Called");

		UserModel userModel = paymentDAO.findById(UserModel.class, userId);
		verifyUser(userModel);

		String hql = "from " + UserSubscriptionModel.class.getName() + " p where p.user.id=? and current_date() between p.subscriptionDate and p.expiryDate order by p.subscription.subscriptionType asc";
		@SuppressWarnings("unchecked")
		List<UserSubscriptionModel> usmList = (List<UserSubscriptionModel>) paymentDAO.runQuery(hql, userId);
		if (usmList == null) {
			// this can never be the case.
			throw new DabbleException(ResponseStatus.SUBSCRIPTION_EXPIRED, "Subscription has expired");
		}
		boolean isFree = false;
		boolean isPayAsYouGo = false;
		for (UserSubscriptionModel usm : usmList) {
			if (usm.getSubscription().getSubscriptionType() == DAOConstants.SUBSCRIPTION_TYPE_FREE) {
				isFree = true;
				break;
			}
			if (usm.getSubscription().getSubscriptionType() == DAOConstants.SUBSCRIPTION_TYPE_MONTHLY) {
				// using free value since the payment will happen on a monthly basis which from this transactions perspective means free.
				isFree = true;
				break;
			}
			if (usm.getSubscription().getSubscriptionType() == DAOConstants.SUBSCRIPTION_TYPE_PAY_AS_YOU_GO) {
				isPayAsYouGo = true;
				break;
			}
		}

		List<CreditCardDTO> dto = getCardList(userId);
		String paymentMethodToken = dto.get(0).getToken();

		if (!isFree && isPayAsYouGo) {
			TransactionRequest request = new TransactionRequest().customerId(userModel.getBraintreeReference()).amount(new BigDecimal("" + amount.floatValue()))
					.paymentMethodToken(paymentMethodToken)
//					.paymentMethodNonce(paymentMethodNonce)
					.options().submitForSettlement(true).done();
			String transactionId = null;
			final Result<Transaction> result = gateway.transaction().sale(request);
			if (amount > 0) {
				if (result.isSuccess()) {
					final Transaction transaction = result.getTarget();
					transactionId = transaction.getId();
					TransactionModel tm = new TransactionModel();
					verifyUser(userModel);
					tm.setTransactingUser(userModel);
					tm.setJobId(jobId);
					tm.setTransactionReference(transactionId);
					tm.setCreatedOn(new Date());
					tm.setAmount(amount);
					tm.setSubscription(paymentDAO.getActiveSubscription(userId).getSubscription());
					tm.setUserTypeCode(userModel.getUserTypeCode());
					paymentDAO.saveObject(tm);
				} else {
					logTransactionErrors(result);
				}
			}
		} else {
			LOG.info("Customer is in monthly billing or free mode, we don't need to charge user#{} for any operation till subscription expires; returning.", userId);
		}
		LOG.debug("Method [performSale]: Returning.");

	}

	@Override
	public String getClientToken(Integer userId) {
		LOG.debug("Method [getClientToken]: Called ({})", userId);

		UserModel userModel = paymentDAO.findById(UserModel.class, userId);
		verifyUser(userModel);

		ClientTokenRequest clientTokenRequest = new ClientTokenRequest().customerId(userModel.getBraintreeReference());
		String clientToken = gateway.clientToken().generate(clientTokenRequest);
		LOG.debug("Method [getClientToken]: Returning {}.", clientToken);
		return clientToken;
	}

	@Override
	public String addCardToCustomer(Integer userId, String cardNumber, String expiryDate, String cvv, String nonce) {
		LOG.debug("Method [addCardToCustomer]: Called");
		UserModel userModel = paymentDAO.findById(UserModel.class, userId);
		String token = UUID.randomUUID().toString().replace('-', 'a');
		verifyUser(userModel);
		String piId = null;
		try {
			List<CreditCard> cards = gateway.customer().find(userModel.getBraintreeReference()).getCreditCards();
			if (cards != null) {
				for (CreditCard card : cards) {
					if (card.getLast4().equals(getLast4(cardNumber))) {
						token = card.getToken();
						piId = card.getUniqueNumberIdentifier();
						LOG.info("Card found, returning PiID " + piId);
						return piId;
					}
				}
			}
			LOG.info("Setting add to false, need to make further changes.");
			LOG.info("Card not found, adding new.");
			PaymentMethodRequest paymentMethodRequest = new PaymentMethodRequest();
			paymentMethodRequest.customerId(userModel.getBraintreeReference()).paymentMethodNonce(nonce).cardholderName(userModel.getFirstName() + " " + userModel.getLastName()).number(cardNumber).cvv(cvv);
			paymentMethodRequest.paymentMethodToken(token);
			Result<? extends PaymentMethod> result = gateway.paymentMethod().create(paymentMethodRequest);
			if (!result.isSuccess()) {
				throw new DabbleException(ResponseStatus.INCOMPLETE_DATA, result.getMessage());
			}
			return addCardToCustomer(userId, cardNumber, expiryDate, cvv, nonce);
		} catch (NotFoundException nfe) {
			// incorrect customer setup
			LOG.error("Customer information not found in Braintree", nfe);
		}
		LOG.debug("Method [addCardToCustomer]: Returning {}.", piId);
		return null;
	}

	@Override
	public void createCustomer(Integer userId) {
		LOG.debug("Method [createCustomer]: Called");
		UserModel userModel = paymentDAO.findById(UserModel.class, userId);
		verifyUser(userModel);
		this.createCustomer(userModel, userModel.getEmails().iterator().next().getEmail(), userModel.getPhones().iterator().next().getPhone());
		LOG.debug("Method [createCustomer]: Returning.");

	}

	@Override
	public void createCustomer(UserModel userModel, String email, String phone) {
		LOG.debug("Method [createCustomer]: Called");
		if (userModel.getBraintreeReference() == null) {
			CustomerRequest request = new CustomerRequest().firstName(userModel.getFirstName()).lastName(userModel.getLastName()).email(email).phone(phone);
			Result<Customer> result = gateway.customer().create(request);
			if (result.isSuccess()) {
				userModel.setBraintreeReference(result.getTarget().getId());
			}
			paymentDAO.updateObject(userModel);
		} else {
			// no need to create another customer, the customer already exists
		}
		LOG.debug("Method [createCustomer]: Returning.");

	}

	private void logTransactionErrors(Result<Transaction> result) {
		if (result.getTransaction() != null) {

			final Transaction transaction = result.getTransaction();
			LOG.error("Error processing refund transaction: Status: {}, Code: {}, Text: {}", transaction.getStatus(), transaction.getProcessorResponseCode(), transaction.getProcessorResponseText());
		} else {
			LOG.error("Found errors in refund transaction");
			for (final ValidationError error : result.getErrors().getAllDeepValidationErrors()) {
				LOG.error("Txn Error: Attribute: {}, Code: {}, Message: {}", error.getAttribute(), error.getCode(), error.getMessage());
				throw new DabbleException(ResponseStatus.INCOMPLETE_DATA, error.getMessage());
			}
		}
	}

	/**
	 * @return the environment
	 */
	public Environment getEnvironment() {
		return environment;
	}

	/**
	 * @param environment
	 *            the environment to set
	 */
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	/**
	 * @return the merchantId
	 */
	public String getMerchantId() {
		return merchantId;
	}

	/**
	 * @param merchantId
	 *            the merchantId to set
	 */
	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	/**
	 * @return the publicKey
	 */
	public String getPublicKey() {
		return publicKey;
	}

	/**
	 * @param publicKey
	 *            the publicKey to set
	 */
	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	/**
	 * @return the privateKey
	 */
	public String getPrivateKey() {
		return privateKey;
	}

	/**
	 * @param privateKey
	 *            the privateKey to set
	 */
	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}

	/**
	 * @return the targetEnvironment
	 */
	public TargetEnvironment getTargetEnvironment() {
		return targetEnvironment;
	}

	/**
	 * @param targetEnvironment
	 *            the targetEnvironment to set
	 */
	public void setTargetEnvironment(TargetEnvironment targetEnvironment) {
		this.targetEnvironment = targetEnvironment;
	}

	/**
	 * @return the paymentDAO
	 */
	public IPaymentDAO getPaymentDAO() {
		return paymentDAO;
	}

	/**
	 * @param paymentDAO
	 *            the paymentDAO to set
	 */
	public void setPaymentDAO(IPaymentDAO paymentDAO) {
		this.paymentDAO = paymentDAO;
	}

}
