package com.fourvector.apps.dabble.service.impl;

import com.fourvector.apps.dabble.service.ITChatService;
import com.twilio.sdk.auth.AccessToken;
import com.twilio.sdk.auth.IpMessagingGrant;

public class TChatService implements ITChatService {

	public String getToken(String userId, String firstName) {

		// Create an endpoint ID which uniquely identifies the user on their
		// current device
		String appName = "Dabble";
		String endpointId = appName + ":" + userId + ":" + userId;

		// Create IP messaging grant
		IpMessagingGrant grant = new IpMessagingGrant();
		grant.setEndpointId(endpointId);
		grant.setServiceSid("IS0533e208336f43fd9b440c4637c6afb0");
		grant.setPushCredentialSid("CR7d3c6df45623d8c202d38996e51fd7c1");

		// Create access token
		AccessToken token = new AccessToken.Builder("ACa91caacf5efcbe8d8fbe0de336a288d0",
				"SK07f823f1671be7bd6ea44c4d15bb08d8", "Hcftuopr5bQf9K87KTzfWjjw1zC8qoBk").identity(firstName).grant(grant)
						.build();
		return token.toJWT();
	}

	/*public static void main(String args[]) {

		TChatService tc = new TChatService();
		System.out.println(tc.getToken("123"));

		TChatService tc1 = new TChatService();
		System.out.println(tc1.getToken("345"));

	}*/

}
