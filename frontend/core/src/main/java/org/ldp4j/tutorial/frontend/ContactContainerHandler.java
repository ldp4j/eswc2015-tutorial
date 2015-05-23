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

@DirectContainer(
	id = ContactContainerHandler.ID,
	memberHandler = ContactHandler.class,
	membershipPredicate="http://www.ldp4j.org/ns/application#hasContact"
)
public class ContactContainerHandler extends Serviceable implements ContainerHandler {

	public static final String ID="ContactContainerHandler";


	protected ContactContainerHandler(AgendaService service) {
		super(service);
	}

	private Person findPerson(ResourceSnapshot resource) throws UnknownResourceException {
		String id = PersonMapper.personId(resource.name());
		Person person = agendaService().getPerson(id);
		if(person==null) {
			throw unknownResource(id, "person");
		}
		return person;
	}

	@Override
	public DataSet get(ResourceSnapshot resource) throws UnknownResourceException, ApplicationRuntimeException {
		return DataSetFactory.createDataSet(resource.name());
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

		Contact protoContact=ContactMapper.enforceConsistency(DataSetUtils.newHelper(representation).self());

		Name<?> contactName=ContactMapper.contactName(protoContact);

		trace("Creating contact from: %n%s",representation);

		Contact contact=
			agendaService().
					addContactToPerson(
						person.getEmail(),
						protoContact.getFullName(),
						protoContact.getUrl(),
						protoContact.getEmail(),
						protoContact.getTelephone());
		try {
			ResourceSnapshot contactResource=container.addMember(contactName);
			info("Created contact %s for person %s",contact.getEmail(),person.getEmail());
			session.saveChanges();
			return contactResource;
		} catch (WriteSessionException e) {
			agendaService().deletePersonContact(person.getEmail(),contact.getEmail());
			throw unexpectedFailure(e, "Could not create contact %s",contact);
		}
	}

}