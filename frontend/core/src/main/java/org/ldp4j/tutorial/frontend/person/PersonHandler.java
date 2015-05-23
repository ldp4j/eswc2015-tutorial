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
package org.ldp4j.tutorial.frontend.person;

import java.util.Collection;

import org.ldp4j.application.data.DataSet;
import org.ldp4j.application.data.Individual;
import org.ldp4j.application.ext.ApplicationRuntimeException;
import org.ldp4j.application.ext.Deletable;
import org.ldp4j.application.ext.InconsistentContentException;
import org.ldp4j.application.ext.Modifiable;
import org.ldp4j.application.ext.ResourceHandler;
import org.ldp4j.application.ext.UnknownResourceException;
import org.ldp4j.application.ext.UnsupportedContentException;
import org.ldp4j.application.ext.annotations.Attachment;
import org.ldp4j.application.ext.annotations.Resource;
import org.ldp4j.application.session.ResourceSnapshot;
import org.ldp4j.application.session.WriteSession;
import org.ldp4j.application.session.WriteSessionException;
import org.ldp4j.tutorial.application.api.AgendaService;
import org.ldp4j.tutorial.application.api.Contact;
import org.ldp4j.tutorial.application.api.Person;
import org.ldp4j.tutorial.frontend.contact.ContactContainerHandler;
import org.ldp4j.tutorial.frontend.util.FormatUtil;
import org.ldp4j.tutorial.frontend.util.IdentityUtil;
import org.ldp4j.tutorial.frontend.util.Serviceable;
import org.ldp4j.tutorial.frontend.util.Typed;

@Resource(
	id=PersonHandler.ID,
	attachments={
		@Attachment(
			id=PersonHandler.PERSON_CONTACTS,
			path="contacts/",
			handler=ContactContainerHandler.class),
	}
)
public class PersonHandler extends Serviceable implements ResourceHandler, Modifiable, Deletable {

	public static final String ID="PersonHandler";
	public static final String PERSON_CONTACTS="personContacts";

	public PersonHandler(AgendaService service) {
		super(service);
	}

	private Person findPerson(String personId) throws UnknownResourceException {
		Person person = agendaService().getPerson(personId);
		if(person==null) {
			throw unknownResource(personId,"Person");
		}
		return person;
	}

	@Override
	public DataSet get(ResourceSnapshot resource) throws UnknownResourceException {
		String personId = IdentityUtil.personId(resource);
		trace("Requested person %s retrieval...",personId);
		Person person = findPerson(personId);
		info("Retrieved person %s: %s",personId,FormatUtil.toString(person));
		return PersonMapper.toDataSet(person);
	}

	@Override
	public void delete(ResourceSnapshot resource, WriteSession session) throws UnknownResourceException, ApplicationRuntimeException {
		String personId = IdentityUtil.personId(resource);
		trace("Requested person %s deletion...",personId);
		Person person=findPerson(personId);
		info("Deleting person %s...",personId);
		Collection<Contact> contacts = agendaService().listPersonContacts(personId);
		try {
			agendaService().deletePerson(personId);
			session.delete(resource);
			session.saveChanges();
			info("Deleted person %s : %s",personId,FormatUtil.toString(person));
			for(Contact contact:contacts) {
				info(" - Deleted contact %s",FormatUtil.toString(contact));
			}
		} catch (WriteSessionException e) {
			throw unexpectedFailure(e, "Person %s deletion failed",personId);
		}
	}

	@Override
	public void update(
			ResourceSnapshot resource,
			DataSet content,
			WriteSession session)
					throws
						UnknownResourceException,
						UnsupportedContentException,
						InconsistentContentException,
						ApplicationRuntimeException {
		String personId = IdentityUtil.personId(resource);
		trace("Requested person %s update using: %n%s",personId,content);

		Person currentPerson=findPerson(personId);

		Individual<?,?> individual=
			IdentityUtil.
				personIndividual(content,currentPerson);

		Typed<Person> updatedPerson=PersonMapper.toPerson(individual);

		PersonConstraints.validate(updatedPerson);
		PersonConstraints.checkConstraints(currentPerson, updatedPerson);

		Person backupPerson = PersonMapper.clone(currentPerson);
		PersonMapper.copy(updatedPerson.get(), currentPerson);
		try {
			session.modify(resource);
			session.saveChanges();
			info("Updated person %s : %s",personId,FormatUtil.toString(currentPerson));
		} catch (WriteSessionException e) {
			PersonMapper.copy(backupPerson, currentPerson);
			throw unexpectedFailure(e, "Person %s update failed",personId);
		}
	}

}