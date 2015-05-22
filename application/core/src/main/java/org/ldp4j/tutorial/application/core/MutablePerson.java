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
 *   Artifact    : org.ldp4j.tutorial.application:application-core:1.0.0-SNAPSHOT
 *   Bundle      : application-core-1.0.0-SNAPSHOT.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.ldp4j.tutorial.application.core;

import org.ldp4j.tutorial.application.api.Person;

final class MutablePerson implements Person {
	String account;
	String name;
	String location;
	String workplaceHomepage;

	@Override
	public void setWorkplaceHomepage(String workplacehomepage) {
		this.workplaceHomepage = workplacehomepage;

	}

	@Override
	public void setName(String name) {
		this.name = name;

	}

	@Override
	public void setLocation(String location) {
		this.location = location;

	}

	@Override
	public void setEmail(String account) {
		this.account = account;

	}

	@Override
	public String getWorkplaceHomepage() {
		return workplaceHomepage;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getLocation() {
		return location;
	}

	@Override
	public String getEmail() {
		return account;
	}
}