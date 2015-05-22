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

import java.io.Serializable;

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
public class ContactHandler implements ResourceHandler, Modifiable, Deletable {

	public static final String ID="ContactHandler";

	private final AgendaService service;

	protected ContactHandler(AgendaService service) {
		this.service = service;
	}

	private Contact findContact(ResourceSnapshot resource) throws UnknownResourceException {
		Serializable personId = resource.parent().parent().name().id();
		Serializable contactId = resource.name().id();
		Contact contact = this.service.getPersonContact(personId.toString(),contactId.toString());
		if(contact==null) {
			throw new UnknownResourceException("Could not find contact '"+contactId+"' of person '"+personId+"'");
		}
		return contact;
	}

	@Override
	public DataSet get(ResourceSnapshot resource) throws UnknownResourceException {
		Contact contact = findContact(resource);
		return ContactMapper.toDataSet(contact);
	}

	@Override
	public void delete(ResourceSnapshot resource, WriteSession session) throws UnknownResourceException, ApplicationRuntimeException {
		Serializable personId = resource.parent().parent().name().id();
		Serializable contactId = resource.name().id();
		Contact contact = this.service.getPersonContact(personId.toString(),contactId.toString());
		if(contact==null) {
			throw new UnknownResourceException("Could not find contact '"+contactId+"' of person '"+personId+"'");
		}
		this.service.deletePersonContact(personId.toString(),contactId.toString());
		try {
			session.delete(resource);
			session.saveChanges();
		} catch (WriteSessionException e) {
			// Recover if failed
			this.service.addContactToPerson(personId.toString(), contact.getFullName(), contact.getUrl(), contact.getEmail(), contact.getTelephone());
			throw new IllegalStateException("Contact deletion failed",e);
		}
	}

	@Override
	public void update(ResourceSnapshot resource, DataSet content, WriteSession session) throws UnknownResourceException, UnsupportedContentException, InconsistentContentException, ApplicationRuntimeException {
		Serializable personId = resource.parent().parent().name().id();
		Serializable contactId = resource.name().id();
		Contact contact = this.service.getPersonContact(personId.toString(),contactId.toString());
		if(contact==null) {
			throw new UnknownResourceException("Could not find contact '"+contactId+"' of person '"+personId+"'");
		}
		Individual<?,?> individual = content.individualOfId(ManagedIndividualId.createId(resource.name(), ContactHandler.ID));
		if(individual==null) {
			throw new ApplicationRuntimeException("Could not find input data");
		}

		Contact newContact = ContactMapper.enforceConsistency(individual, contact);
		contact.setFullName(newContact.getFullName());
		contact.setTelephone(newContact.getTelephone());
		contact.setUrl(newContact.getUrl());
		try {
			session.modify(resource);
			session.saveChanges();
		} catch (WriteSessionException e) {
			// Recover if failed
			this.service.addContactToPerson(personId.toString(), contact.getFullName(), contact.getUrl(), contact.getEmail(), contact.getTelephone());
			throw new IllegalStateException("Contact update failed",e);
		}
	}

}
