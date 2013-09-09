package com.baasbox.android.test;

import org.json.JSONObject;

import com.baasbox.android.BAASBoxClientException;
import com.baasbox.android.BAASBoxConfig;
import com.baasbox.android.BAASBoxConfig.AuthType;
import com.baasbox.android.BAASBoxException;
import com.baasbox.android.BAASBoxResult;
import com.baasbox.android.test.res.BAASBoxTestCase;

public class BasicAuthTestCase extends BAASBoxTestCase {
	
	protected BAASBoxConfig buildConfig() {
		BAASBoxConfig config = super.buildConfig();
		config.AUTHENTICATION_TYPE = AuthType.BASIC_AUTHENTICATION;
		
		return config;
	}

	public void testSuccess() throws Throwable {
		box.signup("davide", "pass").get();
		box.login("davide", "pass").get();
		
		inspector.setSessionToken(box, null);
		
		BAASBoxResult<JSONObject> user = box.getUser();
		
		try {
			user.get();
		} catch (BAASBoxException e) {
			fail("Unexpected BAASBoxException: " + e);
		}
	}
	
	public void testFailure() throws Throwable {
		box.signup("davide", "pass").get();
		box.login("davide", "pass").get();
		
		inspector.setSessionToken(box, null);
		inspector.setUsernameAndPassword(box, "davide", "1234");
		
		BAASBoxResult<JSONObject> user = box.getUser();
		
		try {
			user.get();
			fail("User authenticated with invalid credentials");
		} catch (BAASBoxClientException e) {
			assertEquals(401, e.httpStatus);
		}
	}
	
}
