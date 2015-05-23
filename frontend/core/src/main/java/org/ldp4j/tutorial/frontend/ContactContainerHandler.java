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
import org.ldp4j.application.data.Individual;
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
		String id = AgendaApplicationUtils.personId(resource);
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
		trace("Requested contact creation from: %n%s",representation);

		ResourceSnapshot parent = container.parent();
		Person person=findPerson(parent);

		Individual<?, ?> individual = DataSetUtils.newHelper(representation).self();

		Typed<Contact> typedContact=ContactMapper.toContact(individual);
		ContactConstraints.validate(typedContact);

		Contact protoContact=typedContact.get();

		Name<?> contactName=AgendaApplicationUtils.name(protoContact);

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
			ContactId contactId=AgendaApplicationUtils.contactId(contactResource);
			session.saveChanges();
			info("Created contact %s: %s",contactId,AgendaApplicationUtils.toString(contact),person.getEmail());
			return contactResource;
		} catch (WriteSessionException e) {
			agendaService().
				deletePersonContact(person.getEmail(),contact.getEmail());
			throw unexpectedFailure(e, "Could not create contact %s",AgendaApplicationUtils.toString(contact));
		}
	}

}