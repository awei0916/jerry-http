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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Consts;
import org.apache.http.HttpHeaders;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.utils.DateUtils;

import com.sangupta.jerry.http.helper.HttpHelper;


/**
 * Class that encapsulates the response information obtained from invoking a HTTP URL. 
 * 
 * @author sangupta
 * 
 * @since 0.9.0
 */
public class WebResponse implements Serializable {
	
	/**
	 * Generated using Eclipse
	 */
	private static final long serialVersionUID = 5896497905018410580L;
	
	protected final URI originalURI;

	/**
     * The response code returned by the webservice invocation.
     */
    protected int responseCode;
    
    /**
     * The response message returned by the webservice invocation.
     */
    protected String message;
    
    /**
     * The response body returned by the webservice invocation.
     */
    protected final byte[] bytes;
    
    /**
     * The charset of the content received
     */
    protected Charset charSet;
    
    /**
     * The content-type as specified by the server
     */
    protected String contentType;
    
    /**
     * The response headers received
     */
    protected final Map<String, String> headers = new HashMap<String, String>();
    
    /**
     * The redirect chain of {@link URI}s
     */
    protected List<URI> redirectChain;
    
    /**
     * The size of the response
     */
    protected long size;
    
    protected WebResponse(URI originalURI, String responseBody) {
    	this.originalURI = originalURI;
    	
    	if(responseBody == null) {
    		this.bytes = null;
    		this.size = 0;
    		return;
    	}
    	
    	this.bytes = responseBody.getBytes();
    	this.size = this.bytes.length;
	}
    
    public WebResponse(URI originalURI, byte[] bytes) {
    	this.originalURI = originalURI;
    	
    	this.bytes = bytes;
    	if(bytes != null) {
    		this.size = bytes.length;
    	} else {
    		this.size = 0;
    	}
    }
    
    /**
     * Set the redirect chain
     * 
     * @param redirectChain
     */
	void setRedirectChain(List<URI> redirectChain) {
		this.redirectChain = redirectChain;
	}

    /**
	 * Read the response stream as a {@link String} object considering UTF-8
	 * encoding.
	 * 
	 * @return the contents as UTF-8 {@link String}
	 */
    public String getContent() {
    	return asString(Consts.UTF_8);
    }
    
    /**
	 * Read the response stream as a {@link String} object considering the given
	 * {@link Charset} encoding
	 * 
	 * @param charset
	 *            the {@link Charset} to use
	 * 
	 * @return the contents in given charset
	 */
    public String asContent(Charset charset) {
    	return asString(charset);
    }
    
    /**
	 * Returns the fetched response as a {@link String} parsed using the
	 * response {@link Charset} or the provided {@link Charset} if none is
	 * specified.
	 * 
	 * @param charset
	 *            the {@link Charset} to be used for deconding the response
	 * 
	 * @return the string representation of the string
	 */
    public String asString(Charset charset) {
    	if(this.bytes != null) {
            try {
                if (this.charSet != null) {
	                return new String(this.bytes, this.charSet.name());
                }
                
                return new String(this.bytes, charset);
            } catch (UnsupportedEncodingException ex) {
                // eat up
            }
            
            return new String(this.bytes);
    	}
    	
    	return null;
    }
    
    /**
     * Returns the fetched response as an {@link InputStream}.
     * 
     * @return the {@link InputStream} of the response
     */
    public InputStream asStream() {
    	if(this.bytes != null) {
    		return new ByteArrayInputStream(this.bytes);
    	}
    	
    	return null;
    }
    
    /**
     * Return the fetched response as a byte-array. The returned byte-array is the actual
     * byte-array. Any modification to the same, will cause a change in the subsequent call
     * to {@link #asStream()}, {@link #getContent()} or {@link #asString(Charset)} methods.
     * 
     * @return the byte array representation of the response
     */
    public byte[] asBytes() {
    	return this.bytes;
    }
    
    /**
	 * Return the fetched response as a byte-array. The returned byte-array is a
	 * clone of the response array.
	 * 
	 * @return the byte array representation of the response
	 */
    public byte[] asClonedBytes() {
    	if(this.bytes == null) {
    		return null;
    	}
    	
    	return this.bytes.clone();
    }
    
    /**
	 * Return the last modified date as a long value
	 * 
	 * @return the millis timestamp representing the last modified header, or
	 *         <code>-1</code> if the header is not present or cannot be parsed
	 *         successfully
	 */
    public long getLastModified() {
    	String headerValue = getHeader(HttpHeaders.LAST_MODIFIED);
    	if(headerValue != null) {
    		try {
    			Date date = DateUtils.parseDate(headerValue);
    			if(date != null) {
    				return date.getTime();
    			}
    		} catch(Exception e) {
    			// eat this up
    		}
    	}
    	
    	return -1;
    }
    
    /**
	 * Check if the request returned a successful response. All response codes
	 * between HTTP 200 and HTTP 299 are considered to be successful.
	 * 
	 * @return <code>true</code> for success, <code>false</code> otherwise
	 */
    public boolean isSuccess() {
    	if(this.responseCode >= 200 && this.responseCode <= 299) {
    		return true;
    	}
    	
    	return false;
    }
    
    /**
     * Check if the response indicates a redirected request. All response codes between HTTP 300
     * and HTTP 399 are considered to be a redirect.
     * 
     * @return <code>true</code> if redirect response, <code>false</code> otherwise
     */
    public boolean isRedirect() {
    	if(this.responseCode >= 300 && this.responseCode <= 399) {
    		return true;
    	}
    	
    	return false;
    }
    
    /**
	 * Check if the response indicates an erroneous client request. All response
	 * codes between HTTP 400 and HTTP 499 are considered to be a client error.
	 * 
	 * @return <code>true</code> if client error is indicated,
	 *         <code>false</code> otherwise
	 */
    public boolean isClientError() {
    	if(this.responseCode >= 400 && this.responseCode <= 499) {
    		return true;
    	}
    	
    	return false;
    }
    
    /**
	 * Check if the response indicates an error on the server. All response
	 * codes between HTTP 500 and HTTP 599 are considered to be a server error.
	 * 
	 * @return <code>true</code> if server error is indicated,
	 *         <code>false</code> otherwise
	 */
    public boolean isServerError() {
    	if(this.responseCode >= 500 && this.responseCode <= 599) {
    		return true;
    	}
    	
    	return false;
    }
    
    /**
	 * Return the value of the header if present, or <code>null</code>
	 * 
	 * @param headerName
	 *            the name of the header
	 * 
	 * @return the value of the header, or <code>null</code> if header is not
	 *         present
	 */
    private String getHeader(String headerName) {
    	return headers.get(headerName);
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
    	return this.responseCode + " " + this.message;
    }
    
    /**
	 * Utility function that returns a string representation of this response
	 * which can be used for debugging purposes. Should not be used in
	 * production code as is slow.
	 * 
	 * @return debug trace information represented as string
	 */
    public String trace() {
    	StringBuilder builder = new StringBuilder();
    	
    	builder.append("[Response: code=");
    	builder.append(this.responseCode);
    	
    	builder.append(", message=");
    	builder.append(this.message);
    	
    	builder.append(", contentType=");
    	builder.append(this.contentType);
    	
    	builder.append(", size=");
    	builder.append(this.size);
    	
    	builder.append("]");
    	return builder.toString();
    }
    
    /**
	 * Write the response stream to the given file. If the HTTP status code is
	 * greater than or equal to HTTP 300, an {@link HttpResponseException} is
	 * thrown.
	 * 
	 * @param file
	 *            the file to write the response to.
	 * 
	 * @throws IOException
	 *             if something fails during HTTP connection
	 * 
	 * @throws HttpResponseException
	 *             if the HTTP status code is greater than or equal to HTTP 300
	 * 
	 * @throws NullPointerException
	 *             if the file to which the response needs to be written is
	 *             <code>null</code>
	 */
    public void writeToFile(final File file) throws IOException {
        HttpHelper.writeToFile(this, file);
    }
    
    // Usual accessor's follow

    /** 
     * Returns the responseCode.
     *
     * @return the responseCode.
     */
    public int getResponseCode() {
        return this.responseCode;
    }

	/**
	 * @return the size
	 */
	public long getSize() {
		return size;
	}

	/**
	 * @return the bytes
	 */
	public byte[] getBytes() {
		return bytes;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @return the headers
	 */
	public Map<String, String> getHeaders() {
		return Collections.unmodifiableMap(headers);
	}

	/**
	 * @return the contentType
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * @return the charSet
	 */
	public Charset getCharSet() {
		return charSet;
	}

	/**
	 * @return the redirectChain
	 */
	public List<URI> getRedirectChain() {
		return redirectChain;
	}
	
	/**
	 * Indicates if the response was fetched from a redirected resource.
	 * 
	 * @return <code>true</code> if request redirects, <code>false</code>
	 *         otherwise
	 */
	public boolean hasRedirects() {
		if(this.redirectChain == null) {
			return false;
		}
		
		return !this.redirectChain.isEmpty();
	}
	
	/**
	 * Returns the actual URI that generated the response. In case of redirects
	 * this is the last redirected request, that eventually led to response.
	 * 
	 * @return the {@link URI} used for this response
	 */
	public URI getURI() {
		URI redirected = this.getFinalURI();
		if(redirected != null) {
			return redirected;
		}
		
		return this.originalURI;
	}
	
	/**
	 * Returns the final {@link URI} that was used to hit a resource.
	 * 
	 * @return the final {@link URI} that was used for a redirected request,
	 *         <code>null</code> if no redirects happened
	 */
	public URI getFinalURI() {
		if(!this.hasRedirects()) {
			return null;
		}
		
		return this.redirectChain.get(this.redirectChain.size() - 1);
	}

}
