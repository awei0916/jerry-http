/**
 *
 * jerry-http - Common Java Functionality
 * Copyright (c) 2012-2017, Sandeep Gupta
 * 
 * http://sangupta.com/projects/jerry-http
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * 		http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package com.sangupta.jerry.http;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.http.client.ClientProtocolException;
import org.junit.Assert;
import org.junit.Test;

import com.sangupta.jerry.http.mock.MockWebResponse;

/**
 * Unit tests for {@link HandledWebRawResponse}.
 * 
 * @author sangupta
 *
 */
public class TestHandledWebRawResponse {

	@Test
	public void test() throws ClientProtocolException, IOException {
		HandledWebRawResponse hwrr = new HandledWebRawResponse(new MockWebResponse("hello"));
		try {
			hwrr.webResponse(null);
			Assert.assertTrue(false);
		} catch(IllegalStateException e) {
			Assert.assertTrue(true);
		}
		
		File file = File.createTempFile("test-jerry-http-", ".dat");
		hwrr.writeToFile(file);
		Assert.assertEquals("hello", FileUtils.readFileToString(file));
		
		hwrr = new HandledWebRawResponse(new MockWebResponse(null));
		hwrr.writeToFile(file);
		Assert.assertEquals("", FileUtils.readFileToString(file));
		
		FileUtils.deleteQuietly(file);
	}
	
}
