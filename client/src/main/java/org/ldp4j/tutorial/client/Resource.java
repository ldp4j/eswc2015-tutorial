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
 *   Artifact    : org.ldp4j.tutorial.client:eswc-2015-client:1.0.0-SNAPSHOT
 *   Bundle      : eswc-2015-client-1.0.0-SNAPSHOT.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.ldp4j.tutorial.client;

final class Resource {

	private final String location;

	private String entityTag;
	private String lastModified;
	private String contentType;
	private String entity;

	Resource(String location) {
		this.location=location;
	}

	Resource(Resource resource) {
		this(resource.location());
		withContentType(resource.contentType());
		withEntityTag(resource.entityTag());
		withLastModified(resource.lastModified());
		withEntity(resource.entity());
	}

	public String location() {
		return this.location;
	}
	public String entityTag() {
		return this.entityTag;
	}
	public String lastModified() {
		return this.lastModified;
	}
	public String contentType() {
		return this.contentType;
	}
	public String entity() {
		return this.entity;
	}

	Resource withEntityTag(String etag) {
		this.entityTag = etag;
		return this;
	}

	Resource withLastModified(String lastModified) {
		this.lastModified = lastModified;
		return this;
	}

	Resource withContentType(String contentType) {
		this.contentType = contentType;
		return this;
	}

	Resource withEntity(String entity) {
		this.entity = entity;
		return this;
	}

}