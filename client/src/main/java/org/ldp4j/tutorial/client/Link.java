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
import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Link implements Comparable<Link> {

	private static final String LINK_PATTERN="\\s*<(.*)>\\s*;\\s*rel=(.*)";

	private final String relation;
	private final URI value;

	private Link(String relation, URI value) {
		this.relation = relation;
		this.value = value;
	}

	public String relation() {
		return relation;
	}

	public URI value() {
		return value;
	}

	public String toString() {
		return "<"+this.value+"> ; rel=\""+this.relation+"\"";
	}

	public static Link fromString(String rawLink) throws IOException {
		Pattern pattern = Pattern.compile(LINK_PATTERN);
		Matcher matcher = pattern.matcher(rawLink);
		if(matcher.matches()) {
			return new Link(matcher.group(2),URI.create(matcher.group(1)));
		}
		throw new IOException("Invalid link");
	}

	public static String toString(Link link) {
		return "<"+link.value+"> ; rel=\""+link.relation+"\"";
	}

	@Override
	public int compareTo(Link that) {
		if(this==that) {
			return 0;
		}
		if(that==null) {
			return -1;
		}
		int result=this.relation.compareTo(that.relation);
		if(result==0) {
			result=this.value.compareTo(that.value);
		}

		return result;
	}

}