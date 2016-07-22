/**
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 *   This file is part of the LDP4j Project:
 *     http://www.ldp4j.org/
 *
 *   Center for Open Middleware
 *     http://www.centeropenmiddleware.com/
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 *   Copyright (C) 2014-2016 Center for Open Middleware.
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
 *   Artifact    : org.ldp4j.tutorial.client:eswc-2015-client:1.0.0
 *   Bundle      : eswc-2015-client-1.0.0.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.ldp4j.tutorial.client;

import java.io.IOException;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpUriRequest;

final class DeleteCommandProcessor extends AbstractLdpCommandProcessor {

	private String entityTag;
	private String location;

	@Override
	public boolean canExecute(CommandContext context) {
		boolean result=false;
		try {
			this.entityTag=requireEntityTag(context);
			this.location=requireTargetResource(context);
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
			repository().delete(this.location);
			console().message("Resource deleted%n");
			if(response.body().isPresent()) {
				console().message("Side effects:%n");
				ShellUtil.showResourceContent(console(),resource);
			}
		} else {
			processUnexpectedResponse(response, "Could not delete resource");
		}
	}

	@Override
	protected HttpUriRequest getRequest(CommandContext options, RequestConfig config) {
		console().
			message("Deleting resource...%n").
			metadata("- If-Match: ").data(this.entityTag).message("%n");

		HttpDelete method = new HttpDelete(this.location);
		method.setHeader("If-Match",this.entityTag);
		method.setConfig(config);
		return method;
	}

}