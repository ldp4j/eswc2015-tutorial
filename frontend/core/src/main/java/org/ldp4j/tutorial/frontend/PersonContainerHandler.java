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

import java.util.Date;

import org.ldp4j.application.data.DataSet;
import org.ldp4j.application.data.DataSetFactory;
import org.ldp4j.application.data.DataSetHelper;
import org.ldp4j.application.data.DataSetModificationException;
import org.ldp4j.application.data.DataSetUtils;
import org.ldp4j.application.data.ManagedIndividual;
import org.ldp4j.application.data.ManagedIndividualId;
import org.ldp4j.application.data.Name;
import org.ldp4j.application.data.constraints.Constraints;
import org.ldp4j.application.ext.ApplicationRuntimeException;
import org.ldp4j.application.ext.UnknownResourceException;
import org.ldp4j.application.ext.UnsupportedContentException;
import org.ldp4j.application.ext.annotations.BasicContainer;
import org.ldp4j.application.session.ContainerSnapshot;
import org.ldp4j.application.session.ResourceSnapshot;
import org.ldp4j.application.session.WriteSession;
import org.ldp4j.application.session.WriteSessionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@BasicContainer(
	id = PersonContainerHandler.ID,
	memberHandler = PersonHandler.class
)
public class PersonContainerHandler extends InMemoryContainerHandler {

	private static final Logger LOGGER=LoggerFactory.getLogger(PersonContainerHandler.class);

	public static final String ID="PersonContainerHandler";

	private PersonHandler handler;

	private ContactContainerHandler contactContainerHandler;

	protected PersonContainerHandler() {
		super(ID);
	}

	protected final void setPersonHandler(PersonHandler handler) {
		this.handler=handler;
	}

	protected final PersonHandler personHandler() {
		return this.handler;
	}

	protected final void setContactContainerHandler(ContactContainerHandler handler) {
		this.contactContainerHandler=handler;
	}

	protected final ContactContainerHandler contactContainerHandler() {
		return this.contactContainerHandler;
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

		Name<?> personName=AgendaApplicationHelper.nextName(PersonHandler.ID);
		Name<?> contactsName=AgendaApplicationHelper.nextName(ContactContainerHandler.ID);

		LOGGER.trace("Creating person from: \n{}",representation);

		DataSetHelper helper=
			DataSetHelper.newInstance(representation);

		ManagedIndividualId newId =
			ManagedIndividualId.
				createId(
					personName,
					PersonHandler.ID);

		try {
			ManagedIndividual individual=helper.manage(newId);
			individual.
				addValue(
					AgendaApplicationHelper.READ_ONLY_PROPERTY,
					DataSetUtils.newLiteral(new Date().toString()));
		} catch (DataSetModificationException e) {
			// TODO: Verify this weird error
			Constraints constraints = Constraints.constraints();
			throw new UnsupportedContentException("Could not process request", e,constraints);
		}

		try {
			personHandler().add(personName,representation);
			contactContainerHandler().add(contactsName,DataSetFactory.createDataSet(contactsName));
			ResourceSnapshot person=container.addMember(personName);
			ContainerSnapshot contacts = person.
				createAttachedResource(
					ContainerSnapshot.class,
					PersonHandler.PERSON_CONTACTS,
					contactsName,
					ContactContainerHandler.class);
			LOGGER.info("Created person {} with contacts {}",person.name(),contacts.name());
			session.saveChanges();
			return person;
		} catch (WriteSessionException e) {
			contactContainerHandler().remove(contactsName);
			personHandler().remove(personName);
			throw new IllegalStateException("Could not create person",e);
		}
	}

}
