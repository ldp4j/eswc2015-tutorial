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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

final class CreateCommandProcessor extends AbstractLdpCommandProcessor {

	private String location;
	private String contentType;
	private String content;

	@Override
	public boolean canExecute(CommandContext context) {
		boolean result=false;
		try {
			this.location=requireTargetResource(context);
			this.contentType=contentType(context);
			this.content=requireEntity(context);
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
		if(statusCode==201) {
			if(!response.location().isPresent()) {
				console().error("No new resource location found%n");
				return;
			}
			repository().updateResource(resource);
			console().message("Resource created:%n");
			console().metadata("- New resource available at ").data("%s%n",response.location().get());
			console().message("Target resource status:%n");
			ShellUtil.showResourceMetadata(console(), resource);
			ShellUtil.showLinks(console(),links);
		} else {
			processUnexpectedResponse(response, "Could not create resource");
		}
	}

	@Override
	protected HttpUriRequest getRequest(CommandContext options, RequestConfig config) {
		console().
			message("Creating resource...%n").
			metadata("- Content-Type: ").data(this.contentType).message("%n").
			metadata("- Content:%n").data(this.content).message("%n");

		HttpEntity entity=
			new StringEntity(
				this.content,
				ContentType.parse(this.contentType));

		HttpPost method = new HttpPost(this.location);
		method.setEntity(entity);
		method.setConfig(config);
		return method;
	}

	private String requireEntity(CommandContext context) {
		String fileName=context.entity();
		if(fileName==null) {
			throw new CommandRequirementException("No entity available");
		}
		File file=new File(fileName);
		if(!file.canRead()) {
			throw new CommandRequirementException("Cannot read entity");
		}
		FileInputStream fis = null;
		try {
			fis=new FileInputStream(file);
			return IOUtils.toString(fis);
		} catch (IOException e) {
			throw new CommandRequirementException("Cannot load entity",e);
		} finally {
			IOUtils.closeQuietly(fis);
		}
	}

	private Resource refreshResource(CommandResponse response) throws IOException {
		Resource resource = getOrCreateResource(response.resource());
		resource.
			withLastModified(response.lastModified().orNull()).
			withEntityTag(response.entityTag().orNull()).
			withContentType(null).
			withEntity(null);
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