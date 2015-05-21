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

import java.net.URI;

import org.ldp4j.application.data.DataSet;
import org.ldp4j.application.data.DataSetFactory;
import org.ldp4j.application.data.DataSetHelper;
import org.ldp4j.application.data.ExternalIndividual;
import org.ldp4j.application.data.Individual;
import org.ldp4j.application.data.IndividualHelper;
import org.ldp4j.application.data.Name;
import org.ldp4j.application.data.NamingScheme;
import org.ldp4j.application.ext.ApplicationRuntimeException;
import org.ldp4j.application.ext.ContainerHandler;
import org.ldp4j.application.ext.UnknownResourceException;
import org.ldp4j.application.ext.UnsupportedContentException;
import org.ldp4j.application.ext.annotations.BasicContainer;
import org.ldp4j.application.session.ContainerSnapshot;
import org.ldp4j.application.session.ResourceSnapshot;
import org.ldp4j.application.session.WriteSession;
import org.ldp4j.application.session.WriteSessionException;
import org.ldp4j.tutorial.application.api.AgendaService;
import org.ldp4j.tutorial.application.api.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@BasicContainer(
	id = PersonContainerHandler.ID,
	memberHandler = PersonHandler.class
)
public class PersonContainerHandler implements ContainerHandler {

	private static final Logger LOGGER=LoggerFactory.getLogger(PersonContainerHandler.class);

	public static final String ID="PersonContainerHandler";

	private final AgendaService service;

	protected PersonContainerHandler(AgendaService service) {
		this.service = service;
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

		DataSetHelper helper=
				DataSetHelper.newInstance(representation);

		Individual<?, ?> self = helper.self();

		String account = firstLiteralValue(self, "http://xmlns.com/foaf/0.1/account",String.class);
		String name= firstLiteralValue(self, "http://xmlns.com/foaf/0.1/name",String.class);
		String location= firstIndividualValue(self, "http://xmlns.com/foaf/0.1/based_near").toString();
		String workplaceHomepage= firstIndividualValue(self, "http://xmlns.com/foaf/0.1/workplaceHomepage").toString();

		Person person = this.service.createPerson(account, name, location, workplaceHomepage);

		Name<?> personName=NamingScheme.getDefault().name(person.getAccount());
		Name<?> contactsName=NamingScheme.getDefault().name(person.getAccount(),"contacts");

		LOGGER.trace("Creating account {} for person {} from: \n{}",person.getAccount(),person.getName(),representation);

		try {
			ResourceSnapshot personResource=container.addMember(personName);
			ContainerSnapshot contactsResource = personResource.
				createAttachedResource(
					ContainerSnapshot.class,
					PersonHandler.PERSON_CONTACTS,
					contactsName,
					ContactContainerHandler.class);
			LOGGER.info("Created person {} with contacts {}",personResource.name(),contactsResource.name());
			session.saveChanges();
			return personResource;
		} catch (WriteSessionException e) {
			this.service.deletePerson(person.getAccount());
			throw new IllegalStateException("Could not create person",e);
		}
	}

	private <T> T firstLiteralValue(Individual<?, ?> self, String propertyURI, final Class<? extends T> clazz) {
		return
			new IndividualHelper(self).
				property(propertyURI).
				firstValue(clazz);
	}

	private URI firstIndividualValue(Individual<?, ?> self, String propertyURI) {
		return
			new IndividualHelper(self).
				property(propertyURI).
				firstIndividual(ExternalIndividual.class);
	}

}
