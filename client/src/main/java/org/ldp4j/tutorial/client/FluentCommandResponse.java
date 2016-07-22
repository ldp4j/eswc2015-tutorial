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

import org.apache.http.StatusLine;

import com.google.common.base.Optional;


final class FluentCommandResponse implements CommandResponse {

	private String resource;

	private String entityTag;
	private String lastModified;
	private String contentType;
	private String body;

	private String location;

	private Links links;

	private StatusLine status;

	@Override
	public String resource() {
		return this.resource;
	}

	@Override
	public int statusCode() {
		return this.status.getStatusCode();
	}

	@Override
	public String statusMessage() {
		return this.status.getReasonPhrase();
	}


	@Override
	public Optional<String> entityTag() {
		return Optional.fromNullable(this.entityTag);
	}

	@Override
	public Optional<String> lastModified() {
		return Optional.fromNullable(this.lastModified);
	}

	@Override
	public Optional<String> contentType() {
		return Optional.fromNullable(this.contentType);
	}

	@Override
	public Optional<String> body() {
		return Optional.fromNullable(this.body);
	}

	@Override
	public Optional<String> location() {
		return Optional.fromNullable(this.location);
	}

	@Override
	public Links links() {
		return this.links;
	}

	FluentCommandResponse withLocation(String location) {
		this.location = location;
		return this;
	}

	FluentCommandResponse withLinks(Links links) {
		this.links = links;
		return this;
	}

	FluentCommandResponse withResource(String resource) {
		this.resource=resource;
		return this;
	}

	FluentCommandResponse withEntityTag(String etag) {
		this.entityTag = etag;
		return this;
	}

	FluentCommandResponse withLastModified(String lastModified) {
		this.lastModified = lastModified;
		return this;
	}

	FluentCommandResponse withContentType(String contentType) {
		this.contentType = contentType;
		return this;
	}

	FluentCommandResponse withBody(String body) {
		this.body = body;
		return this;
	}

	FluentCommandResponse withStatus(StatusLine status) {
		this.status = status;
		return this;
	}

}