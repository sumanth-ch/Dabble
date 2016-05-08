/**
 * 
 */
package com.fourvector.dabble.test.service;

import java.io.File;
import java.io.InputStream;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.fourvector.apps.dabble.common.dto.UserDTO;
import com.fourvector.apps.dabble.service.IChatService;
import com.fourvector.apps.dabble.service.IUserService;
import com.fourvector.apps.dabble.web.util.DabbleDateTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author asharma
 */
public class UserServiceTest {

	private static ApplicationContext	applicationContext;
	private IUserService				userService;
	private IChatService				chatService;

	private static final Logger	LOG		= LoggerFactory.getLogger(UserServiceTest.class);
	protected Gson				gson	= new GsonBuilder().serializeNulls().registerTypeAdapter(Date.class, new DabbleDateTypeAdapter()).setPrettyPrinting().create();

	/**
	 * 
	 */
	public UserServiceTest() {
		super();
		initComponents();
	}

	private void initComponents() {
		LOG.debug("Method [initComponents]: Called");
// applicationContext = new ClassPathXmlApplicationContext("classpath:spring/*.xml");
// chatService = applicationContext.getBean(IChatService.class);
// System.out.println(chatService.sendMessage(2, "Test", 2));
// System.out.println(chatService.getMessage(2, 1439836359256l));
// userService.authenticate("anantha@fourvector.in", "Test1");
		LOG.debug("Method [initComponents]: Returning.");
	}

	public static void main(String[] args) {
		System.out.println("starting..");
		UserServiceTest ust = new UserServiceTest();
		try {
// System.out.println(BaseRestController.gson.toJson(new UserDetailsDTO()));
// ust.testCreateUser();
// ust.testAuthenticateUser();
// ust.testForgotPassword();
// ust.testUserDetails();
		} catch (Exception e) {
			LOG.error("Found exception Exception in method main", e);
		} finally {
			System.out.println("Finished.");
		}

	}

	private void testUserDetails() {
		LOG.debug("Method [testUserDetails]: Called");
// System.out.println(BaseRestController.gson.toJson(userService.getUserDetails(33)));
		LOG.debug("Method [testUserDetails]: Returning.");

	}

	public void testCreateUser() throws Exception {
		String userJson = "{'gender': 1,'dob': '22-Oct-1983', 'password':'test', 'firstName': 'Anantha','lastName': 'Sharma','aboutMe': 'A USer','email': 'anantha.sharma4@gmail.com','phone': '9886489306','address': {'addressLine1': '#49, west hataway drive','addressLine2': 'Wisconsin','city': 'Wisconsin','state': 'CT','zip': 776901}}";
		UserDTO userDto = gson.fromJson(userJson, UserDTO.class);
//		InputStream is = FileUtils.openInputStream(new File("/home/asharma/projects/FourVector/dabble/IMG_0016.PNG"));
// System.out.println("User created with Id : " + userService.createUser(userDto, is));
	}

	public void testUpdateUser() throws Exception {
		String userJson = "{'gender': 2,'dob': '22-Oct-1983','firstName': 'Anantha','middleName':'new Middlename added', lastName': 'Sharma','aboutMe': 'A USer','email': 'anantha.sharma4@gmail.com','phone': '9886489306','address': {'addressLine1': '#49, west hataway drive','addressLine2': 'Wisconsin','city': 'Wisconsin','state': 'CT','zip': 776901}}";
		Integer userId = 21;
		UserDTO userDto = gson.fromJson(userJson, UserDTO.class);
// userService.updateUser(userId, userDto, null);
	}

	public void testAuthenticateUser() throws Exception {
// System.out.println("Auth response: " + BaseRestController.gson.toJson(userService.authenticate("anantha@fourvector.in", "Test")));
	}

	public void testForgotPassword() throws Exception {
// userService.forgotPassword("anantha@fourvector.in");
// userService.forgotPassword("anantha.sharma455@gmail.com");
	}

	public void testAddAddress() throws Exception {
// String addressJson = "";
// Integer userId = 21;
// AddressDTO address = gson.fromJson(addressJson, AddressDTO.class);
// System.out.println("This should succeed");
// userService.addAddress(address, userId);
// LOG.info("[testAddAddress] Test Succeed");
// System.out.println("This should fail");
// try {
// userService.addAddress(address, userId);
// } catch (Exception e) {
// LOG.info("[testAddAddress] Test Succeed");
// }
	}

}
