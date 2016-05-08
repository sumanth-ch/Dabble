/**
 * 
 */
package com.aps.test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.fourvector.apps.dabble.common.dto.BidDTO;
import com.fourvector.apps.dabble.common.dto.JobDTO;
import com.fourvector.apps.dabble.notification.DabbleApplePushNotifier;
import com.fourvector.apps.dabble.service.IBidService;
import com.fourvector.apps.dabble.service.IPaymentService;
import com.fourvector.apps.dabble.service.config.NotificationEvent;
import com.fourvector.apps.dabble.service.config.ParameterKeys;

/**
 * @author asharma
 */
public class MainClass {

	ApplicationContext applicationContext = null;

	/**
	 * 
	 */
	public MainClass() {
		initComponents();
		try {
// testInsert();
// testSendNotification();
// testAcceptBid();
// testUserSubscription();
// testListMyBids();
			testUserSubscription();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (applicationContext != null) {
// ((ClassPathXmlApplicationContext) applicationContext).close();
			}
		}
	}

	public void testListMyBids() throws Exception {
		IBidService bidService = applicationContext.getBean(IBidService.class);
		List<JobDTO> bids = bidService.getMyBids(80);
		for (JobDTO jobDTO : bids) {
			for (BidDTO bid : jobDTO.getBids()) {
				System.out.println(bid.getCreatedDate());
			}
		}
	}

	public void testUserSubscription() throws Exception {
		IPaymentService ps = applicationContext.getBean(IPaymentService.class);
		ps.performSale(107, null, 1d, "2fb18884-2353-4b9c-881a-fe8a9c4949bf");
	}

	public void testAcceptBid() throws Exception {
		IBidService bs = applicationContext.getBean(IBidService.class);
		int bidId = bs.placeBid(43, "adsasd", 34, 10d, "USD");
		bs.acceptBid(42, bidId, "fake-payment-nonce");
	}

	private void testInsert() throws Exception {
// IBidService svc = applicationContext.getBean(IBidService.class);
// svc.preAuthorizeSale(8, 2);
	}

	public void testSendNotification() throws Exception {
		DabbleApplePushNotifier apns = applicationContext.getBean(DabbleApplePushNotifier.class);
		Map<String, String> params = new HashMap<>();
		params.put(ParameterKeys.KEY_EVENT, NotificationEvent.ACCEPT_BID_BIDDER.name());
		params.put(ParameterKeys.TIME_STAMP, "" + System.currentTimeMillis());
		String message = "15:Call anantha if you get this";
		String subject = "15:Call anantha if you get this";

		apns.notify("5d8fddb3c36b9f28b4c9068a1daf808d1269fa7214b4808350accb79d1ab3089", subject, message, params);
		apns.notify("d92b7e0ed35667f2c3600eca32928dbcd2b0cd2313f028adf982083dac645556", subject, message, params);
	}

	private void initComponents() {
		applicationContext = new ClassPathXmlApplicationContext("classpath:spring/*.xml");
	}

	public void testCreatePrfile() throws Exception {

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		MainClass mc = new MainClass();
// MainClass.testTime("PST");
// MainClass.testTime("EST");
// MainClass.testTime("CET");
// MainClass.testTime("GMT");
// MainClass.testTime("UTC");
	}

	public static void testTime(String timezone) {
		Calendar calendar = new GregorianCalendar();
		String timezoneStr = timezone;
// timezoneStr = timezoneStr == null ? "PST" : timezoneStr;
		calendar.setTimeZone(TimeZone.getTimeZone(timezoneStr));
		SimpleDateFormat sdf_date = new SimpleDateFormat("MM/dd/yyyy");
		SimpleDateFormat sdf_time = new SimpleDateFormat("hh:MM:ss");
		sdf_date.setTimeZone(TimeZone.getTimeZone(timezoneStr));
		sdf_time.setTimeZone(TimeZone.getTimeZone(timezoneStr));

		System.out.println(sdf_date.format(calendar.getTime()));
		System.out.println(sdf_time.format(calendar.getTime()));

	}

}
