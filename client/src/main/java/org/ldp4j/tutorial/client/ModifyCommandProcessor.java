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
import java.io.IOException;
import java.net.URI;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

final class ModifyCommandProcessor extends AbstractLdpCommandProcessor {

	private String entityTag;
	private String location;
	private String contentType;
	private String content;

	@Override
	public boolean canExecute(CommandContext context) {
		boolean result=false;
		try {
			this.location=requireTargetResource(context);
			this.entityTag=requireEntityTag(context);
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
		int statusCode = response.statusCode();
		Resource resource = refreshResource(response);
		if(statusCode==200 || statusCode==204) {
			Links links = response.links();
			if(!links.hasLink("type",URI.create("http://www.w3.org/ns/ldp#Resource"))) {
				console().error("Not a LDP resource%n");
				ShellUtil.showLinks(console(),links);
				return;
			}

			repository().updateResource(resource);
			console().message("Resource modified:%n");
			ShellUtil.showResourceMetadata(console(), resource);
			ShellUtil.showLinks(console(),links);
			if(response.body().isPresent()) {
				console().message("Side effects:%n");
				ShellUtil.showResourceContent(console(),resource);
			}
		} else {
			processUnexpectedResponse(response, "Could not modify resource");
		}
	}

	@Override
	protected HttpUriRequest getRequest(CommandContext options, RequestConfig config) {
		console().
			message("Modifying resource...%n").
			metadata("- If-Match    : ").data(this.entityTag).message("%n").
			metadata("- Content-Type: ").data(this.contentType).message("%n").
			metadata("- Content:%n").data(this.content).message("%n");

		HttpEntity entity=
			new StringEntity(
				this.content,
				ContentType.parse(this.contentType));

		HttpPut method = new HttpPut(this.location);
		method.setHeader("If-Match",this.entityTag);
		method.setEntity(entity);
		method.setConfig(config);
		return method;
	}

	private String requireEntity(CommandContext context) {
		String entity=null;
		String entityFileName=context.entity();
		if(entityFileName==null) {
			entity=loadPersistedEntity();
		} else {
			entity=loadSpecifiedEntity(entityFileName);
		}
		if(entity==null) {
			throw new CommandRequirementException("No entity available");
		}
		return entity;
	}

	private String loadSpecifiedEntity(String fileName) {
		File file=new File(fileName);
		try {
			return FileUtils.readFileToString(file);
		} catch (IOException e) {
			throw new CommandRequirementException("Cannot load entity",e);
		}
	}

	private String loadPersistedEntity() {
		try {
			return manager().get(this.location);
		} catch (IOException e) {
			throw new CommandRequirementException("Could not load persisted entity");
		}
	}

	private String contentType(CommandContext options) {
		String rawContentType=options.contentType();
		if(rawContentType==null) {
			rawContentType="text/turtle";
		}
		return rawContentType;
	}

}