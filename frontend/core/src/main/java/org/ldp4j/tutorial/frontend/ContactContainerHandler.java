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
 *   Artifact    : org.ldp4j.tutorial.frontend:frontend-core:1.0.0-SNAPSHOT
 *   Bundle      : frontend-core-1.0.0-SNAPSHOT.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.ldp4j.tutorial.frontend;

import org.ldp4j.application.data.DataSet;
import org.ldp4j.application.data.DataSetFactory;
import org.ldp4j.application.data.DataSetUtils;
import org.ldp4j.application.data.Name;
import org.ldp4j.application.ext.ApplicationRuntimeException;
import org.ldp4j.application.ext.ContainerHandler;
import org.ldp4j.application.ext.UnknownResourceException;
import org.ldp4j.application.ext.UnsupportedContentException;
import org.ldp4j.application.ext.annotations.DirectContainer;
import org.ldp4j.application.session.ContainerSnapshot;
import org.ldp4j.application.session.ResourceSnapshot;
import org.ldp4j.application.session.WriteSession;
import org.ldp4j.application.session.WriteSessionException;
import org.ldp4j.tutorial.application.api.AgendaService;
import org.ldp4j.tutorial.application.api.Contact;
import org.ldp4j.tutorial.application.api.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@DirectContainer(
	id = ContactContainerHandler.ID,
	memberHandler = ContactHandler.class,
	membershipPredicate="http://www.ldp4j.org/ns/application#hasContact"
)
public class ContactContainerHandler implements ContainerHandler {

	private static final Logger LOGGER=LoggerFactory.getLogger(PersonContainerHandler.class);

	public static final String ID="ContactContainerHandler";

	private final AgendaService service;

	protected ContactContainerHandler(AgendaService service) {
		this.service = service;
	}

	@Override
	public DataSet get(ResourceSnapshot resource) throws UnknownResourceException, ApplicationRuntimeException {
		return DataSetFactory.createDataSet(resource.name());
	}

	private Person findPerson(ResourceSnapshot resource) throws UnknownResourceException {
		Person person = this.service.getPerson(resource.name().id().toString());
		if(person==null) {
			throw new UnknownResourceException("Could not find person for account '"+resource.name().id());
		}
		return person;
	}

	@Override
	public ResourceSnapshot create(
			ContainerSnapshot container,
			DataSet representation,
			WriteSession session)
					throws
						UnknownResourceException,
						UnsupportedContentException,
						ApplicationRuntimeException {
		ResourceSnapshot parent = container.parent();
		Person person=findPerson(parent);

		Contact protoContact=ContactMapper.toContact(DataSetUtils.newHelper(representation).self());

		Name<?> contactName=ContactMapper.contactName(protoContact);

		LOGGER.trace("Creating contact from: \n{}",representation);

		Contact contact=
			this.service.
					addContactToPerson(
						person.getEmail(),
						protoContact.getFullName(),
						protoContact.getUrl(),
						protoContact.getEmail(),
						protoContact.getTelephone());
		try {
			ResourceSnapshot contactResource=container.addMember(contactName);
			LOGGER.info("Created contact {} for person {} ",contact.getEmail(),person.getEmail());
			session.saveChanges();
			return contactResource;
		} catch (WriteSessionException e) {
			this.service.deletePersonContact(person.getEmail(),contact.getEmail());
			throw new IllegalStateException("Could not create contact",e);
		}
	}

}