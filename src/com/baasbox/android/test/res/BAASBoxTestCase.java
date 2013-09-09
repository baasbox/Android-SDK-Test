package com.baasbox.android.test.res;

import org.apache.http.client.methods.HttpDelete;

import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;

import com.baasbox.android.BAASBox;
import com.baasbox.android.BAASBoxConfig;
import com.baasbox.android.BAASBoxConfig.AuthType;
import com.baasbox.android.internal.Credentials;
import com.baasbox.android.internal.RESTInterface;
import com.baasbox.android.test.StubActivity;

public abstract class BAASBoxTestCase extends ActivityInstrumentationTestCase2<StubActivity> {

	public BAASBoxTestCase() {
		super(StubActivity.class);
	}
	
	protected BAASBox box;
	protected BAASBoxInspector inspector;
	
	@Override
	protected void setUp() throws Exception {
		Context context = getActivity();
		inspector = new BAASBoxInspector(context);
		
		// Database delete
		BAASBoxConfig config = buildConfig();
		config.AUTHENTICATION_TYPE = AuthType.BASIC_AUTHENTICATION;
		RESTInterface rest = new RESTInterface(config);
		
		String uri = rest.getURI("admin/db/0");
		
		HttpDelete delete = rest.delete(uri);
		Credentials credentials = new Credentials();
		credentials.username = "admin";
		credentials.password = "admin";
		rest.execute(delete, credentials, null, false);
		
		// Device preferences delete
		inspector.clearMemory();
		
		box = new BAASBox(buildConfig(), context);
	}
	
	protected BAASBoxConfig buildConfig() {
		BAASBoxConfig config = new BAASBoxConfig();
		config.API_DOMAIN = Server.API_DOMAIN;
		config.API_BASEPATH = Server.API_BASEPATH;
		return config;
	};
	
}
