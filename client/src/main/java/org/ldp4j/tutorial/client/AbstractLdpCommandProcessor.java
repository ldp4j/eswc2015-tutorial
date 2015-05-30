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
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.google.common.base.Throwables;


public abstract class AbstractLdpCommandProcessor extends AbstractCommandProcessor {


	@Override
	public final boolean execute(CommandContext options) {
		CloseableHttpClient httpClient=HttpClients.createDefault();
		CloseableHttpResponse httpResponse=null;
		try {
			RequestConfig config =
				RequestConfig.
					custom().
						setConnectTimeout(5000).
						setRedirectsEnabled(true).
						setCircularRedirectsAllowed(false).
						build();
			HttpUriRequest method = getRequest(options,config);
			httpResponse = httpClient.execute(method);
			processResponse(toCommandResponse(options.target(), httpResponse));
		} catch (ClientProtocolException cause) {
			fail("HTTP communication with the server failed",cause);
		} catch (IOException cause) {
			fail("I/O failure", cause);
		} finally {
			IOUtils.closeQuietly(httpResponse);
			IOUtils.closeQuietly(httpClient);
		}
		return true;
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

	private CommandResponse toCommandResponse(String resource, HttpResponse httpResponse) throws IOException {
		FluentCommandResponse result=
			new FluentCommandResponse().
				withResource(resource).
				withStatus(httpResponse.getStatusLine()).
				withLinks(getLinks(httpResponse));
		HttpEntity entity = httpResponse.getEntity();
		if(entity!=null) {
			result.
				withContentType(entity.getContentType().getValue()).
				withBody(EntityUtils.toString(entity));
		}
		Header etag=httpResponse.getFirstHeader("ETag");
		if(etag!=null) {
			result.withEntityTag(etag.getValue());
		}
		Header lastModified=httpResponse.getFirstHeader("Last-Modified");
		if(lastModified!=null) {
			result.withLastModified(lastModified.getValue());
		}
		Header locationHeader = httpResponse.getFirstHeader("Location");
		if(locationHeader!=null) {
			result.withLocation(locationHeader.getValue());
		}
		return result;

	}

	private String loadConstraintReport(URI constraintReport) throws IOException {
		URL url = constraintReport.toURL();
		InputStream is=null;
		try {
			is=url.openStream();
			return IOUtils.toString(is);
		} finally {
			IOUtils.closeQuietly(is);
		}
	}

	private Links getLinks(HttpResponse response) throws IOException {
		Links links=Links.create();
		Header[] headers = response.getHeaders("Link");
		for(Header header:headers) {
			for(HeaderElement element:header.getElements()) {
				links.withLink(Link.fromString(element.toString()));
			}
		}
		return links;
	}

	protected final void processUnexpectedResponse(CommandResponse response, String message, Object... args) {
		console().error(message+"%n",args);
		int statusCode = response.statusCode();
		if(statusCode==404) {
			console().metadata("- Resource not found%n");
		} else if(statusCode==405) {
			console().metadata("- Operation not allowed%n");
		} else if(statusCode==409) {
			try {
				URI constraintReport = response.links().firstValue("http://www.w3.org/ns/ldp#constrainedBy");
				console().metadata("- Conflict: ").data(constraintReport.toString()).metadata("]:%n");
				String report = loadConstraintReport(constraintReport);
				console().metadata("- Conflict [%n").data(constraintReport.toString()).metadata("]:%n");
				console().data(report).data("%n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if(statusCode==410) {
			console().metadata("- Resource has been already deleted%n");
		} else if(statusCode==412) {
			console().metadata("- Current Entity Tag: ").data(response.entityTag().get()).message("%n");
			console().metadata("- Current Last Modified: ").data(response.lastModified().get()).message("%n");
		} else if(statusCode>=500) {
			console().metadata("- Unexpected server failure: ").data("%d (%s)%n",statusCode,response.statusMessage());
		} else if(statusCode>=400) {
			console().metadata("- Unexpected failure on client request: ").data("%d (%s)%n",statusCode,response.statusMessage());
		} else {
			console().metadata("- Unexpected response: ").data("%d (%s)%n",statusCode,response.statusMessage());
		}

	}

	protected abstract void processResponse(CommandResponse response) throws IOException;

	protected abstract HttpUriRequest getRequest(CommandContext options, RequestConfig config);

	protected final Resource getOrCreateResource(String location) {
		Resource resource=repository().resolveResource(location);
		if(resource==null) {
			resource=repository().createResource(location);
		}
		return resource;
	}



}
