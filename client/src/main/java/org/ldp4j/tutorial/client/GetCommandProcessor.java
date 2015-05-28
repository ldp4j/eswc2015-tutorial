/**
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 *   This file is part of the LDP4j Project:
 *     http://www.ldp4j.org/
 *
 *   Center for Open Middleware
 *     http://www.centeropenmiddleware.com/
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 *   Copyright (C) 2014 Center for Open Middleware.
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 *   Artifact    : org.ldp4j.tutorial.client:eswc-2015-client:1.0.0-SNAPSHOT
 *   Bundle      : eswc-2015-client-1.0.0-SNAPSHOT.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.ldp4j.tutorial.client;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class GetCommandProcessor extends AbstractCommandProcessor {

	@Override
	public boolean canExecute(CommandContext context) {
		if(!context.hasTarget()) {
			console().error("ERROR: No target specified");
			return false;
		}
		try {
			URI uri = new URI(context.target());
			boolean result = uri.isAbsolute() && uri.getScheme().equalsIgnoreCase("http");
			if(!result) {
				console().
					error("ERROR: Invalid target resource (not an absolute HTTP url)%n");
			}
			return result;
		} catch (URISyntaxException e) {
			console().
				error("ERROR: Invalid target '").
				metadata(context.target()).
				error("' (%s)%n",e.getMessage());
			return false;
		}
	}

	@Override
	public boolean execute(CommandContext options) {
		CloseableHttpClient httpClient=HttpClients.createDefault();
		CloseableHttpResponse httpResponse=null;
		String location = options.target();
		HttpGet httpGet=createGetMethod(location,contentType(options));
		try {
			httpResponse = httpClient.execute(httpGet);
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			Resource resource = refreshResource(location, httpResponse);
			if(statusCode==200) {
				repository().updateResource(resource);
				ShellUtil.showResource(console(),resource);
			} else if(statusCode==404) {
				console().error("Resource not found%n");
			} else if(statusCode==410) {
				console().error("Resource has been already deleted%n");
			} else {
				console().error("Unexpected response: %d (%s)%n",statusCode,httpResponse.getStatusLine().getReasonPhrase());
			}
		} catch (Exception cause) {
			console().error("Communication with the server failed: %s%n",cause.getMessage());
		} finally {
			IOUtils.closeQuietly(httpResponse);
			IOUtils.closeQuietly(httpClient);
		}
		return true;
	}

	private Resource refreshResource(String location,
			CloseableHttpResponse httpResponse) throws IOException {
		Resource resource=repository().resolveResource(location);
		if(resource==null) {
			resource=repository().createResource(location);
		}
		HttpEntity entity = httpResponse.getEntity();
		if(entity!=null) {
			resource.
				withContentType(entity.getContentType().getValue()).
				withEntity(EntityUtils.toString(entity));
		}
		Header etag=httpResponse.getFirstHeader("ETag");
		if(etag!=null) {
			resource.withEntityTag(etag.getValue());
		}
		Header lastModified=httpResponse.getFirstHeader("Last-Modified");
		if(lastModified!=null) {
			resource.withLastModified(lastModified.getValue());
		}
		return resource;
	}

	private String contentType(CommandContext options) {
		String rawContentType=options.contentType();
		if(rawContentType==null) {
			rawContentType="text/turtle";
		}
		return rawContentType;
	}


	private HttpGet createGetMethod(String location, String contentType) {
		HttpGet httpGet = new HttpGet(location);
		httpGet.setHeader("Accept", contentType+"; charset=utf-8");
		httpGet.setHeader("User-Agent", " Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.90 Safari/537.36");
		RequestConfig config =
			RequestConfig.
				custom().
					setConnectTimeout(5000).
					setRedirectsEnabled(true).
					setCircularRedirectsAllowed(false).
					build();
		httpGet.setConfig(config);
		return httpGet;
	}

}