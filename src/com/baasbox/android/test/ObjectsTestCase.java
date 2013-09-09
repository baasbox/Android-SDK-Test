package com.baasbox.android.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.client.methods.HttpPost;
import org.json.JSONArray;
import org.json.JSONObject;

import com.baasbox.android.BAASBoxClientException;
import com.baasbox.android.BAASBoxException;
import com.baasbox.android.BAASBoxResult;
import com.baasbox.android.internal.Credentials;
import com.baasbox.android.internal.RESTInterface;
import com.baasbox.android.test.res.BAASBoxTestCase;

public class ObjectsTestCase extends BAASBoxTestCase {
	
	private static final String COLLECTION = "test-res";
	private static final String OBJECT_1 = "{'name':'object1','flag':true,'val': 1}";
	private static final String OBJECT_2 = "{'name':'object2','flag':false,'val':3}";
	private static final String OBJECT_3 = "{'name':'object3','flag':true,'val': 5}";
	private static final String OBJECT_4 = "{'name':'object4','flag':false,'val':7}";
	private static final String OBJECT_5 = "{'name':'object5','flag':true,'val': 9}";
	private static final String OBJECT_6 = "{'name':'object6','flag':false,'val':8}";
	private static final String OBJECT_7 = "{'name':'object7','flag':true,'val': 6}";
	private static final String OBJECT_8 = "{'name':'object8','flag':false,'val':4}";
	private static final String OBJECT_9 = "{'name':'object9','flag':true,'val': 2}";
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		RESTInterface rest = new RESTInterface(buildConfig());
		String uri = rest.getURI("admin/collection/?", COLLECTION);
		HttpPost post = rest.post(uri);
		Credentials credentials = new Credentials();
		credentials.username = "admin";
		credentials.password = "admin";
		rest.execute(post, credentials, null, false);
		
		box.signup("davide", "pass").get();
		box.login("davide", "pass").get();
	}

	public void testCreate() throws Throwable {
		JSONObject obj1 = new JSONObject(OBJECT_1);
		
		BAASBoxResult<JSONObject> result = box.createDocument(COLLECTION, obj1);		
		assertNotNull(result);
		
		JSONObject obj = null;
		try {
			obj = result.get();
		} catch (BAASBoxException e) {
			fail("Unexpected BAASBoxException: " + e);
		}
		
		assertNotNull(result);
		assertEquals(obj1.get("name"), obj.get("name"));
		assertTrue(obj.has("id"));
	}
	
	public void testUpdate() throws Throwable {
		JSONObject obj1 = new JSONObject(OBJECT_1);
		JSONObject obj2 = new JSONObject(OBJECT_2);
		
		JSONObject old = box.createDocument(COLLECTION, obj1).get();
		String id = old.getString("id");
		BAASBoxResult<JSONObject> result = box.updateDocument(COLLECTION, id, obj2);
		
		assertNotNull(result);
		
		JSONObject obj = null;
		try {
			obj = result.get();
		} catch (BAASBoxException e) {
			fail("Unexpected BAASBoxException: " + e);
		}
		
		assertNotNull(result);
		assertEquals(obj2.get("name"), obj.get("name"));
		assertTrue(obj.has("id"));
		assertEquals(id, obj.getString("id"));
	}
	
	public void testGetByCollection() throws Throwable {
		JSONObject obj1 = new JSONObject(OBJECT_1);		
		JSONObject expected = box.createDocument(COLLECTION, obj1).get();
		String id = expected.getString("id");
		BAASBoxResult<JSONObject> result = box.getDocument(COLLECTION, id);
		
		assertNotNull(result);
		
		JSONObject obj = null;
		try {
			obj = result.get();
		} catch (BAASBoxException e) {
			fail("Unexpected BAASBoxException: " + e);
		}
		
		assertNotNull(result);
		assertEquals(expected.get("name"), obj.get("name"));
		assertTrue(obj.has("id"));
		assertEquals(id, obj.getString("id"));
	}
	
	public void testGetByCollectionUnexistent() throws Throwable {
		String id = "550e8400-e29b-41d4-a716-446655440000";
		BAASBoxResult<JSONObject> result = box.getDocument(COLLECTION, id);
		
		assertNotNull(result);
		
		JSONObject obj = null;
		try {
			obj = result.get();
			fail("Returned unexistent object: " + obj.toString());
		} catch (BAASBoxClientException e) {
			assertEquals(404, e.httpStatus);
		}
	}
	
	public void testDelete() throws Throwable {		
		JSONObject obj = box.createDocument(COLLECTION, new JSONObject(OBJECT_1)).get();
		String id = obj.getString("id");
		BAASBoxResult<Void> result = box.deleteDocument(COLLECTION, id);
		
		assertNotNull(result);
		
		try {
			result.get();
		} catch (BAASBoxException e) {
			fail("Unexpected BAASBoxException: " + e);
		}
		
		try {
			box.getDocument(COLLECTION, id).get();
			fail("Document not deleted");
		} catch (BAASBoxClientException e) {
			assertEquals(404, e.httpStatus);
		}
	}
	
	public void testCount() throws Throwable {		
		box.createDocument(COLLECTION, new JSONObject(OBJECT_1)).get();
		box.createDocument(COLLECTION, new JSONObject(OBJECT_2)).get();
		box.createDocument(COLLECTION, new JSONObject(OBJECT_3)).get();
		box.createDocument(COLLECTION, new JSONObject(OBJECT_4)).get();
		box.createDocument(COLLECTION, new JSONObject(OBJECT_5)).get();
		box.createDocument(COLLECTION, new JSONObject(OBJECT_6)).get();
		box.createDocument(COLLECTION, new JSONObject(OBJECT_7)).get();
		box.createDocument(COLLECTION, new JSONObject(OBJECT_8)).get();
		box.createDocument(COLLECTION, new JSONObject(OBJECT_9)).get();
		
		BAASBoxResult<Long> result = box.getCount(COLLECTION);
		assertNotNull(result);
		
		try {
			long count = result.get();
			assertEquals(9, count);
		} catch (BAASBoxException e) {
			fail("Unexpected BAASBoxException: " + e);
		}
	}
	
	public void testGetAll() throws Throwable {		
		box.createDocument(COLLECTION, new JSONObject(OBJECT_1)).get();
		box.createDocument(COLLECTION, new JSONObject(OBJECT_2)).get();
		box.createDocument(COLLECTION, new JSONObject(OBJECT_3)).get();
		box.createDocument(COLLECTION, new JSONObject(OBJECT_4)).get();
		box.createDocument(COLLECTION, new JSONObject(OBJECT_5)).get();
		box.createDocument(COLLECTION, new JSONObject(OBJECT_6)).get();
		box.createDocument(COLLECTION, new JSONObject(OBJECT_7)).get();
		box.createDocument(COLLECTION, new JSONObject(OBJECT_8)).get();
		box.createDocument(COLLECTION, new JSONObject(OBJECT_9)).get();
		
		BAASBoxResult<JSONArray> result = box.getAllDocuments(COLLECTION);
		assertNotNull(result);
		
		JSONArray array = null;
		try {
			array = result.get();
		} catch (BAASBoxException e) {
			fail("Unexpected BAASBoxException: " + e);
		}
		
		assertEquals(9, array.length());
	}
	
	public void testGetAllOrderBy() throws Throwable {		
		box.createDocument(COLLECTION, new JSONObject(OBJECT_1)).get();
		box.createDocument(COLLECTION, new JSONObject(OBJECT_2)).get();
		box.createDocument(COLLECTION, new JSONObject(OBJECT_3)).get();
		box.createDocument(COLLECTION, new JSONObject(OBJECT_4)).get();
		box.createDocument(COLLECTION, new JSONObject(OBJECT_5)).get();
		box.createDocument(COLLECTION, new JSONObject(OBJECT_6)).get();
		box.createDocument(COLLECTION, new JSONObject(OBJECT_7)).get();
		box.createDocument(COLLECTION, new JSONObject(OBJECT_8)).get();
		box.createDocument(COLLECTION, new JSONObject(OBJECT_9)).get();
		
		BAASBoxResult<JSONArray> result = box.getAllDocuments(COLLECTION, "val ASC", -1, -1);
		assertNotNull(result);
		
		JSONArray array = null;
		try {
			array = result.get();
		} catch (BAASBoxException e) {
			fail("Unexpected BAASBoxException: " + e);
		}
		
		assertEquals(9, array.length());
		
		ArrayList<String> actual = new ArrayList<String>(9);
		for (int i = 0; i < 9; i++)
			actual.add(array.getJSONObject(i).getString("name"));
		
		List<String> expected = Arrays.asList(new String[] {
			"object1", "object9", "object2", "object8", "object3", "object7", "object4", "object6", "object5"
		});
		
		assertEquals(expected, actual);
	}
	
	public void testGetAllWhere() throws Throwable {		
		box.createDocument(COLLECTION, new JSONObject(OBJECT_1)).get();
		box.createDocument(COLLECTION, new JSONObject(OBJECT_2)).get();
		box.createDocument(COLLECTION, new JSONObject(OBJECT_3)).get();
		box.createDocument(COLLECTION, new JSONObject(OBJECT_4)).get();
		box.createDocument(COLLECTION, new JSONObject(OBJECT_5)).get();
		box.createDocument(COLLECTION, new JSONObject(OBJECT_6)).get();
		box.createDocument(COLLECTION, new JSONObject(OBJECT_7)).get();
		box.createDocument(COLLECTION, new JSONObject(OBJECT_8)).get();
		box.createDocument(COLLECTION, new JSONObject(OBJECT_9)).get();
		
		BAASBoxResult<JSONArray> result = box.getAllDocuments(COLLECTION, "flag=true");
		assertNotNull(result);
		
		JSONArray array = null;
		try {
			array = result.get();
		} catch (BAASBoxException e) {
			fail("Unexpected BAASBoxException: " + e);
		}
		
		assertEquals(5, array.length());
	}
	
	public void testGetAllWhereWithParams() throws Throwable {		
		box.createDocument(COLLECTION, new JSONObject(OBJECT_1)).get();
		box.createDocument(COLLECTION, new JSONObject(OBJECT_2)).get();
		box.createDocument(COLLECTION, new JSONObject(OBJECT_3)).get();
		box.createDocument(COLLECTION, new JSONObject(OBJECT_4)).get();
		box.createDocument(COLLECTION, new JSONObject(OBJECT_5)).get();
		box.createDocument(COLLECTION, new JSONObject(OBJECT_6)).get();
		box.createDocument(COLLECTION, new JSONObject(OBJECT_7)).get();
		box.createDocument(COLLECTION, new JSONObject(OBJECT_8)).get();
		box.createDocument(COLLECTION, new JSONObject(OBJECT_9)).get();
		
		BAASBoxResult<JSONArray> result = box.getAllDocuments(COLLECTION, "flag=? AND name=?", "true", "object3");
		assertNotNull(result);
		
		JSONArray array = null;
		try {
			array = result.get();
		} catch (BAASBoxException e) {
			fail("Unexpected BAASBoxException: " + e);
		}
		
		assertEquals(1, array.length());
		assertEquals("object3", array.getJSONObject(0).getString("name"));
	}
	
	public void testGetAllWithPagination() throws Throwable {		
		box.createDocument(COLLECTION, new JSONObject(OBJECT_1)).get();
		box.createDocument(COLLECTION, new JSONObject(OBJECT_2)).get();
		box.createDocument(COLLECTION, new JSONObject(OBJECT_3)).get();
		box.createDocument(COLLECTION, new JSONObject(OBJECT_4)).get();
		box.createDocument(COLLECTION, new JSONObject(OBJECT_5)).get();
		box.createDocument(COLLECTION, new JSONObject(OBJECT_6)).get();
		box.createDocument(COLLECTION, new JSONObject(OBJECT_7)).get();
		box.createDocument(COLLECTION, new JSONObject(OBJECT_8)).get();
		box.createDocument(COLLECTION, new JSONObject(OBJECT_9)).get();
		
		BAASBoxResult<JSONArray> result = box.getAllDocuments(COLLECTION, "val ASC", 1, 2);
		assertNotNull(result);
		
		JSONArray array = null;
		try {
			array = result.get();
		} catch (BAASBoxException e) {
			fail("Unexpected BAASBoxException: " + e);
		}
		
		assertEquals(2, array.length());
		
		ArrayList<String> actual = new ArrayList<String>(9);
		for (int i = 0; i < 2; i++)
			actual.add(array.getJSONObject(i).getString("name"));
		
		List<String> expected = Arrays.asList(new String[] {
			"object2", "object8"
		});
		
		assertEquals(expected, actual);
	}

}
