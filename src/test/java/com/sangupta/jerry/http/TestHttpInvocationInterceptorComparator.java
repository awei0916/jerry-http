/**
 *
 * jerry-http - Common Java Functionality
 * Copyright (c) 2012-2016, Sandeep Gupta
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

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link HttpInvocationInterceptorComparator}.
 * 
 * @author sangupta
 *
 */
public class TestHttpInvocationInterceptorComparator {

	@Test
	public void testComparator() {
		HttpInvocationInterceptorComparator comparator = new HttpInvocationInterceptorComparator();
		
		Assert.assertEquals(0, comparator.compare(new Hii(3), new Hii(3)));
		Assert.assertEquals(1, comparator.compare(new Hii(0), new Hii(3)));
		Assert.assertEquals(-1, comparator.compare(new Hii(3), new Hii(0)));
	}

	private static class Hii implements HttpInvocationInterceptor {
		
		public Hii(int priority) {
			this.priority = priority;
		}
		
		private int priority;

		@Override
		public int getPriority() {
			return this.priority;
		}

		@Override
		public WebResponse beforeInvocation(WebRequest request) {
			return null;
		}

		@Override
		public WebResponse afterInvocation(WebResponse response, IOException e) {
			return null;
		}
		
	}
	
}
