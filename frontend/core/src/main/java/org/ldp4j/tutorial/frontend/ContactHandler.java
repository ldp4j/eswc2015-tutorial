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
import org.ldp4j.application.ext.annotations.Resource;
import org.ldp4j.application.session.ResourceSnapshot;
import org.ldp4j.application.session.WriteSession;
import org.ldp4j.application.session.WriteSessionException;
import org.ldp4j.tutorial.application.api.AgendaService;
import org.ldp4j.tutorial.application.api.Contact;

@Resource(
	id=ContactHandler.ID
)
public class ContactHandler extends Serviceable implements ResourceHandler, Modifiable, Deletable {

	public static final String ID="ContactHandler";

	protected ContactHandler(AgendaService service) {
		super(service);
	}

	private Contact findContact(ContactId id) throws UnknownResourceException {
		Contact contact = agendaService().getPersonContact(id.getPerson(),id.getEmail());
		if(contact==null) {
			super.unknownResource(id,"Contact");
		}
		return contact;
	}

	@Override
	public DataSet get(ResourceSnapshot resource) throws UnknownResourceException {
		ContactId contactId = AgendaApplicationUtils.contactId(resource);
		trace("Requested contact %s retrieval",contactId);
		Contact contact = findContact(contactId);
		info("Retrieved contact %s: %s",contactId,AgendaApplicationUtils.toString(contact));
		return ContactMapper.toDataSet(contact);
	}

	@Override
	public void delete(ResourceSnapshot resource, WriteSession session) throws UnknownResourceException, ApplicationRuntimeException {
		ContactId contactId = AgendaApplicationUtils.contactId(resource);
		trace("Requested contact %s deletion",contactId);
		Contact currentContact = findContact(contactId);
		agendaService().deletePersonContact(contactId.getPerson(),contactId.getEmail());
		try {
			session.delete(resource);
			session.saveChanges();
			info("Deleted contact %s: %s",contactId,AgendaApplicationUtils.toString(currentContact));
		} catch (WriteSessionException e) {
			// Recover if failed
			agendaService().
				addContactToPerson(
					contactId.getPerson(),
					currentContact.getFullName(),
					currentContact.getUrl(),
					currentContact.getEmail(),
					currentContact.getTelephone());
			throw unexpectedFailure(e, "Contact %s deletion failed",contactId);
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
		ContactId contactId = AgendaApplicationUtils.contactId(resource);
		trace("Requested contact %s updated using: %n%s",contactId,content);

		Contact currentContact = findContact(contactId);

		Individual<?,?> individual=
			content.
				individualOfId(
					ManagedIndividualId.
						createId(resource.name(), ContactHandler.ID));
		if(individual==null) {
			throw unexpectedFailure("Could not find input data");
		}

		Typed<Contact> typedContact=ContactMapper.toContact(individual);
		ContactConstraints.validate(typedContact);
		ContactConstraints.checkConstraints(currentContact,typedContact);

		Contact backupContact=ContactMapper.clone(currentContact);

		Contact updatedContact = typedContact.get();
		currentContact.setFullName(updatedContact.getFullName());
		currentContact.setTelephone(updatedContact.getTelephone());
		currentContact.setUrl(updatedContact.getUrl());
		try {
			session.modify(resource);
			session.saveChanges();
			info("Updated contact %s : %s",AgendaApplicationUtils.toString(currentContact));
		} catch (WriteSessionException e) {
			// Recover if failed
			currentContact.setFullName(backupContact.getFullName());
			currentContact.setTelephone(backupContact.getTelephone());
			currentContact.setUrl(backupContact.getUrl());
			throw unexpectedFailure(e, "Contact %s update failed",contactId);
		}
	}

}
