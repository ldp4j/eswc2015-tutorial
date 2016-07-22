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
 *   Artifact    : org.ldp4j.tutorial.application:application-core:1.0.0
 *   Bundle      : application-core-1.0.0.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.ldp4j.tutorial.application.core;

import org.ldp4j.tutorial.application.api.Person;

final class MutablePerson implements Person {

	String account;
	String name;
	String location;
	String workplaceHomepage;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setWorkplaceHomepage(final String workplacehomepage) {
		this.workplaceHomepage = workplacehomepage;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setName(final String name) {
		this.name = name;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLocation(final String location) {
		this.location = location;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setEmail(final String account) {
		this.account = account;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getWorkplaceHomepage() {
		return this.workplaceHomepage;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return this.name;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getLocation() {
		return this.location;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEmail() {
		return this.account;
	}

}