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

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.google.common.base.Throwables;


public abstract class AbstractLdpCommandProcessor extends AbstractCommandProcessor {


	protected final void processUnexpectedResponse(HttpResponse response, String message, Object... args) {
		console().error(message+"%n",args);
		StatusLine statusLine = response.getStatusLine();
		int statusCode = statusLine.getStatusCode();
		if(statusCode==404) {
			console().metadata("- Resource not found%n");
		} else if(statusCode==405) {
			console().metadata("- Operation not allowed%n");
		} else if(statusCode==410) {
			console().metadata("- Resource has been already deleted%n");
		} else if(statusCode==412) {
			console().metadata("- Current Last Modified: ").data(response.getFirstHeader("Last-Modified").getValue()).message("%n");
			console().metadata("- Current Entity Tag: ").data(response.getFirstHeader("ETag").getValue()).message("%n");
		} else if(statusCode>=500) {
			console().metadata("- Unexpected server failure: ").data("%d (%s)%n",statusCode,statusLine.getReasonPhrase());
		} else if(statusCode>=400) {
			console().metadata("- Unexpected failure on client request: ").data("%d (%s)%n",statusCode,statusLine.getReasonPhrase());
		} else {
			console().metadata("-Unexpected response: ").data("%d (%s)%n",statusCode,statusLine.getReasonPhrase());
		}

	}

	@Override
	public final boolean execute(CommandContext options) {
		CloseableHttpClient httpClient=HttpClients.createDefault();
		CloseableHttpResponse response=null;
		try {
			RequestConfig config =
				RequestConfig.
					custom().
						setConnectTimeout(5000).
						setRedirectsEnabled(true).
						setCircularRedirectsAllowed(false).
						build();
			HttpUriRequest method = getRequest(options,config);
			response = httpClient.execute(method);
			processResponse(response);
		} catch (ClientProtocolException cause) {
			fail("HTTP communication with the server failed",cause);
		} catch (IOException cause) {
			fail("I/O failure", cause);
		} finally {
			IOUtils.closeQuietly(response);
			IOUtils.closeQuietly(httpClient);
		}
		return true;
	}

	protected final Links getLinks(HttpResponse response) throws IOException {
		Links links=Links.create();
		Header[] headers = response.getHeaders("Link");
		for(Header header:headers) {
			for(HeaderElement element:header.getElements()) {
				links.withLink(Link.fromString(element.toString()));
			}
		}
		return links;
	}

	private void fail(String failure, Throwable cause) {
		console().error("%s: %n",failure);
		for(Throwable t:Throwables.getCausalChain(cause)) {
			console().metadata(" - %s",t.getClass().getCanonicalName());
			String message=t.getMessage();
			if(message!=null) {
				console().data(": %s%n",message);
			}
			console().data("%n",message);
		}
	}

	protected abstract void processResponse(HttpResponse response) throws IOException;

	protected abstract HttpUriRequest getRequest(CommandContext options, RequestConfig config);



}
