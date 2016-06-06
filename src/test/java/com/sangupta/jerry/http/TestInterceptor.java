package com.sangupta.jerry.http;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.sangupta.jerry.http.mock.MockWebResponse;
import com.sangupta.jerry.http.service.HttpService;
import com.sangupta.jerry.http.service.impl.DefaultHttpServiceImpl;

/**
 * Unit tests involving {@link HttpInvocationInterceptor}.
 * 
 * @author sangupta
 *
 */
public class TestInterceptor {
	
	@Test
	public void testInterception() {
		HttpService service = new DefaultHttpServiceImpl();
		service.setConnectionTimeout(10);
		service.setSocketTimeout(10);
		
		MockWebResponse response = new MockWebResponse("hello world");
		
		HttpInvocationInterceptor interceptor = new MyInterceptor(0, null, null);
		HttpExecutor.DEFAULT.addInvocationInterception(interceptor);
		Assert.assertNull(service.getResponse("http://localhost/hit"));
		HttpExecutor.DEFAULT.removeInvocationInterceptor(interceptor);
		
		interceptor = new MyInterceptor(0, response, null);
		HttpExecutor.DEFAULT.addInvocationInterception(interceptor);
		Assert.assertEquals("hello world", service.getTextResponse("http://localhost/hit"));
		HttpExecutor.DEFAULT.removeInvocationInterceptor(interceptor);
		
		interceptor = new MyInterceptor(0, null, response);
		HttpExecutor.DEFAULT.addInvocationInterception(interceptor);
		Assert.assertEquals("hello world", service.getTextResponse("http://localhost/hit"));
		HttpExecutor.DEFAULT.removeInvocationInterceptor(interceptor);
		
		interceptor = new MyInterceptor(0, new MockWebResponse("hello1"), new MockWebResponse("hello2"));
		HttpExecutor.DEFAULT.addInvocationInterception(interceptor);
		Assert.assertEquals("hello1", service.getTextResponse("http://localhost/hit"));
		HttpExecutor.DEFAULT.removeInvocationInterceptor(interceptor);

		interceptor = new MyInterceptor(10, new MockWebResponse("hello1"), new MockWebResponse("hello2"));
		HttpInvocationInterceptor interceptor2 = new MyInterceptor(0, new MockWebResponse("hello3"), new MockWebResponse("hello4"));

		HttpExecutor.DEFAULT.addInvocationInterception(interceptor);
		HttpExecutor.DEFAULT.addInvocationInterception(interceptor2);
		Assert.assertEquals("hello1", service.getTextResponse("http://localhost/hit"));
		HttpExecutor.DEFAULT.removeInvocationInterceptor(interceptor);
		HttpExecutor.DEFAULT.removeInvocationInterceptor(interceptor2);
		
		// clear all interceptors
		HttpExecutor.DEFAULT.removeAllInterceptors();
	}

	private class MyInterceptor implements HttpInvocationInterceptor {
		
		private int priority = 0;
		
		private WebResponse before = null;
		
		private WebResponse after = null;
		
		public MyInterceptor(int priority, WebResponse before, WebResponse after) {
			this.priority = priority;
			this.before = before;
			this.after = after;
		}
		
		@Override
		public int getPriority() {
			return this.priority;
		}

		@Override
		public WebResponse beforeInvocation(WebRequest request) {
			return this.before;
		}

		@Override
		public WebResponse afterInvocation(WebResponse response, IOException e) {
			return this.after;
		}
		
	}
}