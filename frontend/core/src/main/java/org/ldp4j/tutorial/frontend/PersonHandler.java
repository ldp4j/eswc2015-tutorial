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
import org.ldp4j.application.data.Individual;
import org.ldp4j.application.data.ManagedIndividualId;
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
import org.ldp4j.tutorial.application.api.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Resource(
	id=PersonHandler.ID,
	attachments={
		@Attachment(
			id=PersonHandler.PERSON_CONTACTS,
			path="contacts/",
			handler=ContactContainerHandler.class),
	}
)
public class PersonHandler implements ResourceHandler, Modifiable, Deletable {

	private static final Logger LOGGER=LoggerFactory.getLogger(PersonHandler.class);

	public static final String ID="PersonHandler";
	public static final String PERSON_CONTACTS="personContacts";

	private final AgendaService service;

	protected PersonHandler(AgendaService service) {
		this.service = service;
	}

	private Person findPerson(ResourceSnapshot resource) throws UnknownResourceException {
		Person person = this.service.getPerson(resource.name().id().toString());
		if(person==null) {
			throw new UnknownResourceException("Could not find person for account '"+resource.name().id());
		}
		return person;
	}

	@Override
	public DataSet get(ResourceSnapshot resource) throws UnknownResourceException {
		Person person = findPerson(resource);
		return PersonMapper.toDataSet(person);
	}

	@Override
	public void delete(ResourceSnapshot resource, WriteSession session) throws UnknownResourceException, ApplicationRuntimeException {
		Person person=findPerson(resource);
		LOGGER.info("Deleting person {}...",AgendaApplicationHelper.toString(person));
		try {
			this.service.deletePerson(person.getEmail());
			session.delete(resource);
			session.saveChanges();
			LOGGER.info("Deleted person {}.",resource.name());
		} catch (WriteSessionException e) {
			// Recover if failed
			this.service.createPerson(person.getEmail(),person.getName(),person.getLocation(),person.getWorkplaceHomepage());
			LOGGER.error("Person "+resource.name()+" deletion failed ",e);
			throw new ApplicationRuntimeException("Person deletion failed",e);
		}
	}

	@Override
	public void update(ResourceSnapshot resource, DataSet content, WriteSession session) throws UnknownResourceException, UnsupportedContentException, InconsistentContentException, ApplicationRuntimeException {
		Person currentPerson=findPerson(resource);

		Individual<?,?> individual = content.individualOfId(ManagedIndividualId.createId(resource.name(), PersonHandler.ID));
		if(individual==null) {
			throw new ApplicationRuntimeException("Could not find input data");
		}

		Person updatedPerson=PersonMapper.enforceConsistency(individual, currentPerson);

		String oldName=currentPerson.getName();
		String oldLocation=currentPerson.getLocation();
		String oldWorkplaceHomepage=currentPerson.getWorkplaceHomepage();
		try {
			currentPerson.setName(updatedPerson.getName());
			currentPerson.setLocation(updatedPerson.getLocation());
			currentPerson.setWorkplaceHomepage(updatedPerson.getWorkplaceHomepage());
			session.modify(resource);
			session.saveChanges();
		} catch (WriteSessionException e) {
			currentPerson.setName(oldName);
			currentPerson.setLocation(oldLocation);
			currentPerson.setWorkplaceHomepage(oldWorkplaceHomepage);
			throw new IllegalStateException("Person update failed",e);
		}
	}

}