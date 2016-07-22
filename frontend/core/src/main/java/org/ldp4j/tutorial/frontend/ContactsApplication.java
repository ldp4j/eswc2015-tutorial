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
 *   Artifact    : org.ldp4j.tutorial.frontend:frontend-core:1.0.0
 *   Bundle      : frontend-core-1.0.0.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.ldp4j.tutorial.frontend;

import org.ldp4j.application.data.Name;
import org.ldp4j.application.data.NamingScheme;
import org.ldp4j.application.ext.Application;
import org.ldp4j.application.session.WriteSession;
import org.ldp4j.application.session.WriteSessionException;
import org.ldp4j.application.setup.Bootstrap;
import org.ldp4j.application.setup.Environment;
import org.ldp4j.tutorial.application.api.ContactsService;
import org.ldp4j.tutorial.frontend.contact.ContactContainerHandler;
import org.ldp4j.tutorial.frontend.contact.ContactHandler;
import org.ldp4j.tutorial.frontend.person.PersonContainerHandler;
import org.ldp4j.tutorial.frontend.person.PersonHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ContactsApplication extends Application<ContactsConfiguration> {

	private static final Logger LOGGER=LoggerFactory.getLogger(ContactsApplication.class);

	private static final String PERSON_CONTAINER_NAME     = "PersonContainer";

	private static final String ROOT_PERSON_CONTAINER_PATH= "persons/";

	private final Name<String> personContainerName;

	public ContactsApplication() {
		this.personContainerName=
			NamingScheme.
				getDefault().
					name(PERSON_CONTAINER_NAME);
	}

	@Override
	public void setup(Environment environment, Bootstrap<ContactsConfiguration> bootstrap) {
		LOGGER.info("Starting Contacts Application configuration...");

		ContactsService service = ContactsService.getInstance();

		bootstrap.addHandler(new PersonContainerHandler(service));
		bootstrap.addHandler(new PersonHandler(service));
		bootstrap.addHandler(new ContactContainerHandler(service));
		bootstrap.addHandler(new ContactHandler(service));

		environment.
			publishResource(
				this.personContainerName,
				PersonContainerHandler.class,
				ROOT_PERSON_CONTAINER_PATH);

		LOGGER.info("Contacts Application configuration completed.");
	}

	@Override
	public void initialize(WriteSession session) {
		LOGGER.info("Initializing Contacts Application...");
		try {
			session.saveChanges();
			LOGGER.info("Contacts Application initialization completed.");
		} catch (WriteSessionException e) {
			LOGGER.warn("Contacts Application initialization failed.",e);
			throw new IllegalStateException(e);
		}
	}

	@Override
	public void shutdown() {
		LOGGER.info("Starting Contacts Application shutdown...");
		LOGGER.info("Contacts Application shutdown completed.");
	}

}