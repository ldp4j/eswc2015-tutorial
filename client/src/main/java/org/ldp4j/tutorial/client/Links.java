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
import java.util.Collection;
import java.util.List;
import java.util.SortedSet;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.ImmutableList.Builder;

final class Links {

	private final Multimap<String,Link> links;

	private Links() {
		this.links=LinkedHashMultimap.create();
	}

	URI firstValue(String relation) {
		URI result = null;
		List<URI> values = values(relation);
		if(!values.isEmpty()) {
			result=values.get(0);
		}
		return result;
	}


	List<URI> values(String relation) {
		Builder<URI> builder = ImmutableList.<URI>builder();
		Collection<Link> relationLinks = this.links.get(relation);
		if(relationLinks!=null) {
			for(Link link:relationLinks) {
				builder.add(link.value());
			}
		}
		return builder.build();
	}

	boolean hasLink(String relation, URI value) {
		return values(relation).contains(value);
	}

	Links withLink(Link link) {
		this.links.put(link.relation(), link);
		return this;
	}

	SortedSet<Link> all() {
		return ImmutableSortedSet.copyOf(this.links.values());
	}

	static Links create() {
		return new Links();
	}

}