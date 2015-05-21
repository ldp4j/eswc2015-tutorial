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
import org.ldp4j.application.data.Name;
import org.ldp4j.application.ext.ApplicationRuntimeException;
import org.ldp4j.application.ext.Deletable;
import org.ldp4j.application.ext.InconsistentContentException;
import org.ldp4j.application.ext.Modifiable;
import org.ldp4j.application.ext.UnknownResourceException;
import org.ldp4j.application.ext.UnsupportedContentException;
import org.ldp4j.application.ext.annotations.Attachment;
import org.ldp4j.application.ext.annotations.Resource;
import org.ldp4j.application.session.AttachmentSnapshot;
import org.ldp4j.application.session.ResourceSnapshot;
import org.ldp4j.application.session.WriteSession;
import org.ldp4j.application.session.WriteSessionException;
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
public class PersonHandler extends InMemoryResourceHandler implements Modifiable, Deletable {

	private static final Logger LOGGER=LoggerFactory.getLogger(PersonHandler.class);

	public static final String ID="PersonHandler";
	public static final String PERSON_CONTACTS="personContacts";

	private ContactContainerHandler contactContainerHandler;

	protected PersonHandler() {
		super(ID);
	}

	protected final void setContactContainerHandler(ContactContainerHandler handler) {
		this.contactContainerHandler=handler;
	}

	protected final ContactContainerHandler contactContainerHandler() {
		return this.contactContainerHandler;
	}


	@Override
	public void delete(ResourceSnapshot resource, WriteSession session) throws UnknownResourceException, ApplicationRuntimeException {
		LOGGER.info("Deleting person {}...",resource.name());
		DataSet personDataSet = get(resource);
		AttachmentSnapshot contacts = resource.attachmentById(PERSON_CONTACTS);
		Name<?> contactsName = contacts.resource().name();
		DataSet contactsDataSet = contactContainerHandler().get(contacts.resource());
		try {
			contactContainerHandler().remove(contactsName);
			remove(resource.name());
			session.delete(resource);
			session.saveChanges();
			LOGGER.info("Deleted person {} and contacts {}.",resource.name(),contactsName);
		} catch (WriteSessionException e) {
			// Recover if failed
			add(resource.name(),personDataSet);
			contactContainerHandler().add(contactsName,contactsDataSet);
			throw new IllegalStateException("Person deletion failed",e);
		}
	}

	@Override
	public void update(ResourceSnapshot resource, DataSet content, WriteSession session) throws UnknownResourceException, UnsupportedContentException, InconsistentContentException, ApplicationRuntimeException {
		DataSet dataSet = get(resource);
		AgendaApplicationHelper.enforceConsistency(resource.name(),PersonHandler.ID,content,dataSet);
		try {
			add(resource.name(),content);
			session.modify(resource);
			session.saveChanges();
		} catch (WriteSessionException e) {
			// Recover if failed
			add(resource.name(),dataSet);
			throw new IllegalStateException("Person update failed",e);
		}
	}

}