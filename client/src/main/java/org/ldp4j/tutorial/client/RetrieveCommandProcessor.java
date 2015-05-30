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

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;

final class RetrieveCommandProcessor extends AbstractLdpCommandProcessor {

	private String location;

	@Override
	public boolean canExecute(CommandContext context) {
		boolean result=false;
		try {
			this.location=requireTargetResource(context);
			result=true;
		} catch (CommandRequirementException e) {
			console().error("ERROR: %s%n",e.getMessage());
		}
		return result;
	}

	@Override
	protected void processResponse(CommandResponse response) throws IOException {
		Links links = response.links();
		if(!links.hasLink("type",URI.create("http://www.w3.org/ns/ldp#Resource"))) {
			console().error("Not a LDP resource%n");
			ShellUtil.showLinks(console(),links);
			return;
		}

		int statusCode = response.statusCode();
		Resource resource = refreshResource(response);
		if(statusCode==200) {
			repository().updateResource(resource);
			console().message("Resource retrieved:%n");
			ShellUtil.showResourceMetadata(console(),resource);
			ShellUtil.showLinks(console(),links);
			ShellUtil.showResourceContent(console(),resource);
			persist(response.body().orNull());
		} else {
			processUnexpectedResponse(response, "Cannot retrieve resource");
		}
	}

	private void persist(String contents) {
		try {
			manager().persist(this.location,contents);
			console().
				message("Representation persisted to ").
				metadata(manager().file(this.location).toString()).
				data("%n");
		} catch (IOException e) {
			console().
				error("ERROR: Could not persist representation (%s)", e.getMessage());
		}

	}

	@Override
	protected HttpUriRequest getRequest(CommandContext options, RequestConfig config) {
		console().
			message("Retrieving resource...%n");

		HttpGet method = new HttpGet(this.location);
		method.setHeader("Accept", contentType(options)+"; charset=utf-8");
		method.setConfig(config);

		return method;
	}

	private Resource refreshResource(CommandResponse response) throws IOException {
		Resource resource = getOrCreateResource(response.resource());
		resource.
			withContentType(response.contentType().orNull()).
			withLastModified(response.lastModified().orNull()).
			withEntityTag(response.entityTag().orNull()).
			withEntity(response.body().orNull());
		return resource;
	}

	private String contentType(CommandContext options) {
		String rawContentType=options.contentType();
		if(rawContentType==null) {
			rawContentType="text/turtle";
		}
		return rawContentType;
	}

}