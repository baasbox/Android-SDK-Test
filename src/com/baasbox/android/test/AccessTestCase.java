package com.baasbox.android.test;

import org.json.JSONObject;

import com.baasbox.android.BAASBoxClientException;
import com.baasbox.android.BAASBoxException;
import com.baasbox.android.BAASBoxResult;
import com.baasbox.android.test.res.BAASBoxTestCase;

public class AccessTestCase extends BAASBoxTestCase {
	
	private static final String USER_JSON = "{'username':'davide','password':'pass','visibleByTheUser':{'U':true},'visibleByFriend':{'F':true},'visibleByRegisteredUsers':{'R':true},'visibleByAnonymousUsers':{'A':true}}";
	private static final String USER_EMAIL_JSON = "{'username':'davide','password':'pass','visibleByTheUser':{'email':'a@b.com'},'visibleByFriend':{},'visibleByRegisteredUsers':{},'visibleByAnonymousUsers':{}}";

	public void testSignup() throws Throwable {
		BAASBoxResult<Void> result = box.signup("davide", "pass");		
		assertNotNull(result);
		
		try {
			result.get();
			box.login("davide", "pass").get();
		} catch (BAASBoxException e) {
			fail("Unexpected BAASBoxException: " + e);
		}
	}
	
	public void testSignupWithUser() throws Throwable {
		BAASBoxResult<Void> result = box.signup(new JSONObject(USER_JSON));		
		assertNotNull(result);
		
		try {
			result.get();
			box.login("davide", "pass").get();
		} catch (BAASBoxException e) {
			fail("Unexpected BAASBoxException: " + e);
		}
	}
	
	public void testLoginSuccess() throws Throwable {
		box.signup("davide", "pass").get();
		
		BAASBoxResult<Void> result = box.login("davide", "pass");
		assertNotNull(result);
		
		try {
			result.get();
		} catch (BAASBoxException e) {
			fail("Unexpected BAASBoxException: " + e);
		}
		
		assertEquals("davide", inspector.getInMemoryUsername(box));
		assertEquals("davide", inspector.getStoredUsername());
		assertEquals("pass", inspector.getInMemoryPassword(box));
		assertEquals("pass", inspector.getStoredPassword());
	}
	
	public void testLoginFailure() throws Throwable {		
		BAASBoxResult<Void> result = box.login("davide", "1234");
		assertNotNull(result);
		
		try {
			result.get();
			fail("User logged with invalid credentials");
		} catch (BAASBoxClientException e) {
			assertEquals(401, e.httpStatus);
		} catch (BAASBoxException e) {
			fail("Unexpected BAASBoxException: " + e);
		}
		
		assertNull(inspector.getInMemoryUsername(box));
		assertNull(inspector.getStoredUsername());
		assertNull(inspector.getInMemoryPassword(box));
		assertNull(inspector.getStoredPassword());
		assertNull(inspector.getStoredSessionToken());
		assertNull(inspector.getInMemorySessionToken(box));
	}
	
	public void testRequestPasswordResetNoEmail() throws Throwable {
		box.signup("davide", "pass").get();
		box.login("davide", "pass").get();
		
		BAASBoxResult<Void> result = box.requestPasswordReset("davide");
		assertNotNull(result);
		
		try {
			result.get();
			fail("Password reset sent with no email");
		} catch (BAASBoxClientException e) {
			assertEquals(400, e.httpStatus);
		}
	}
	
//	public void testRequestPasswordReset() throws Throwable {
//		box.signup(new JSONObject(USER_EMAIL_JSON)).get();
//		box.login("davide", "pass").get();
//		
//		BAASBoxResult<Void> result = box.requestPasswordReset("davide");
//		assertNotNull(result);
//		
//		try {
//			result.get();
//			fail("Password reset sent with no email");
//		} catch (BAASBoxClientException e) {
//			assertEquals(400, e.httpStatus);
//		}
//	}
	
	public void testChangePassword() throws Throwable {
		box.signup("davide", "pass").get();
		box.login("davide", "pass").get();
		
		BAASBoxResult<Void> result = box.changePassword("pass", "pass1");
		assertNotNull(result);
		
		try {
			result.get();
		} catch (BAASBoxException e) {
			fail("Unexpected BAASBoxException: " + e);
		}
		
		try {
			box.login("davide", "pass").get();
			fail("Password not changed");
		} catch (BAASBoxClientException e) {
			assertEquals(401, e.httpStatus);
		}
		
		result = box.login("davide", "pass1");
		assertNotNull(result);
		
		try {
			result.get();
		} catch (BAASBoxException e) {
			fail("Unexpected BAASBoxException: " + e);
		}
		
		assertEquals("davide", inspector.getInMemoryUsername(box));
		assertEquals("davide", inspector.getStoredUsername());
		assertEquals("pass1", inspector.getInMemoryPassword(box));
		assertEquals("pass1", inspector.getStoredPassword());
	}
	
	public void testGetUser() throws Throwable {
		box.signup(new JSONObject(USER_JSON)).get();
		box.login("davide", "pass").get();
		
		BAASBoxResult<JSONObject> result = box.getUser();
		assertNotNull(result);
		
		JSONObject user = null;
		try {
			user = result.get();
		} catch (BAASBoxException e) {
			fail("Unexpected BAASBoxException: " + e);
		}
		
		assertNotNull(user);
		assertTrue(user.getJSONObject("visibleByTheUser").getBoolean("U"));
		assertTrue(user.getJSONObject("visibleByFriend").getBoolean("F"));
		assertTrue(user.getJSONObject("visibleByRegisteredUsers").getBoolean("R"));
		assertTrue(user.getJSONObject("visibleByAnonymousUsers").getBoolean("A"));
	}
	
	public void testUpdateUser() throws Throwable {
		box.signup(new JSONObject(USER_JSON)).get();
		box.login("davide", "pass").get();
		
		JSONObject expected = new JSONObject(USER_EMAIL_JSON);
		
		BAASBoxResult<Void> result = box.updateUser(expected);
		assertNotNull(result);
		
		try {
			result.get();
		} catch (BAASBoxException e) {
			fail("Unexpected BAASBoxException: " + e);
		}
		
		JSONObject user = box.getUser().get();
		
		assertNotNull(user);
		assertEquals(expected.getJSONObject("visibleByTheUser").getString("email"), user.getJSONObject("visibleByTheUser").getString("email"));
		assertFalse(user.getJSONObject("visibleByTheUser").has("U"));
		assertFalse(user.getJSONObject("visibleByFriend").has("F"));
		assertFalse(user.getJSONObject("visibleByRegisteredUsers").has("R"));
		assertFalse(user.getJSONObject("visibleByAnonymousUsers").has("A"));
	}
}
