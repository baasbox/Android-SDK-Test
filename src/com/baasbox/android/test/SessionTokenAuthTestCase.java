package com.baasbox.android.test;

import org.json.JSONObject;

import com.baasbox.android.BAASBoxConfig;
import com.baasbox.android.BAASBoxConfig.AuthType;
import com.baasbox.android.BAASBoxException;
import com.baasbox.android.BAASBoxResult;
import com.baasbox.android.test.res.BAASBoxTestCase;

public class SessionTokenAuthTestCase extends BAASBoxTestCase {

	protected BAASBoxConfig buildConfig() {
		BAASBoxConfig config = super.buildConfig();
		config.AUTHENTICATION_TYPE = AuthType.SESSION_TOKEN;
		return config;
	}

	public void testWithValidSessionToken() throws Throwable {
		box.signup("davide", "pass").get();
		box.login("davide", "pass").get();
		
		inspector.setUsernameAndPassword(box, null, null);
		
		BAASBoxResult<JSONObject> user = box.getUser();
		
		try {
			user.get();
		} catch (BAASBoxException e) {
			fail("Unexpected BAASBoxException: " + e);
		}
	}
		
	public void testLogout() throws Throwable {
		box.signup("davide", "pass").get();
		box.login("davide", "pass").get();
		
		BAASBoxResult<Void> result = box.logout();
		assertNotNull(result);
		
		try {
			result.get();
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
}
