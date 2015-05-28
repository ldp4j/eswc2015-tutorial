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

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class DeleteCommandProcessor extends AbstractCommandProcessor {

	private String entityTag;

	@Override
	public boolean canExecute(CommandContext context) {
		if(!context.hasTarget()) {
			console().error("ERROR: No target specified");
			return false;
		}
		try {
			URI uri = new URI(context.target());
			if(!(uri.isAbsolute() && uri.getScheme().equalsIgnoreCase("http"))) {
				console().
					error("ERROR: Invalid target resource (not an absolute HTTP url)%n");
				return false;
			}
			this.entityTag=entityTag(context);
			if(this.entityTag==null) {
				console().
					error("ERROR: No entity tag specified%n");
				return false;
			}
			return true;
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
		HttpDelete httpGet=createHttpMethod(location,entityTag(options));
		try {
			httpResponse = httpClient.execute(httpGet);
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if(statusCode==204) {
				repository().delete(options.target());
				console().message("Resource deleted%n");
			} else if(statusCode==404) {
				console().error("Resource not found%n");
			} else if(statusCode==410) {
				console().error("Resource has been already deleted%n");
			} else if(statusCode==412) {
				console().error("Cannot delete resource%n");
				console().metadata("- Current Last Modified: ").data(httpResponse.getFirstHeader("Last-Modified").getValue()).message("%n");
				console().metadata("- Current Entity Tag: ").data(httpResponse.getFirstHeader("ETag").getValue()).message("%n");
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

	private String entityTag(CommandContext options) {
		String rawEntityTag=options.entityTag();
		if(rawEntityTag==null) {
			Resource resource = repository().resolveResource(options.target());
			if(resource!=null) {
				rawEntityTag=resource.entityTag();
			}
		}
		return rawEntityTag;
	}

	private HttpDelete createHttpMethod(String location, String entityTag) {
		HttpDelete httpGet = new HttpDelete(location);
		httpGet.setHeader("If-Match",entityTag);
		RequestConfig config =
			RequestConfig.
				custom().
					setConnectTimeout(5000).
					setRedirectsEnabled(true).
					setCircularRedirectsAllowed(false).
					build();
		httpGet.setConfig(config);
		console().
			message("Deleting resource...%n").
			metadata("- If-Match: ").data(entityTag).message("%n");
		return httpGet;
	}

}