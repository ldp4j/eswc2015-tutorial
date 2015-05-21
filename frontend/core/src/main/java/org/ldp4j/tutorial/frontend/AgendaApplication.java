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

import static org.ldp4j.application.data.IndividualReferenceBuilder.newReference;

import java.net.URI;
import java.util.Date;

import org.ldp4j.application.data.DataDSL;
import org.ldp4j.application.data.DataSet;
import org.ldp4j.application.data.DataSetFactory;
import org.ldp4j.application.data.Name;
import org.ldp4j.application.data.NamingScheme;
import org.ldp4j.application.domain.LDP;
import org.ldp4j.application.domain.RDF;
import org.ldp4j.application.ext.Application;
import org.ldp4j.application.ext.Configuration;
import org.ldp4j.application.session.WriteSession;
import org.ldp4j.application.session.WriteSessionException;
import org.ldp4j.application.setup.Bootstrap;
import org.ldp4j.application.setup.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AgendaApplication extends Application<Configuration> {

	private static final String PERSON_CONTAINER_NAME   = "PersonContainer";

	private static final String ROOT_PERSON_CONTAINER_PATH    = "persons/";

	private static final Logger LOGGER=LoggerFactory.getLogger(AgendaApplication.class);

	private final Name<String> personContainerName;

	public AgendaApplication() {
		this.personContainerName=NamingScheme.getDefault().name(PERSON_CONTAINER_NAME);
	}

	protected final DataSet getInitialData(String templateId, String name, boolean markContainer) {
		DataSet initial=null;
		if(!markContainer) {
			initial=
				DataDSL.
					dataSet().
						individual(newReference().toManagedIndividual(templateId).named(name)).
							hasProperty(AgendaApplicationHelper.READ_ONLY_PROPERTY.toString()).
								withValue(new Date().toString()).
							build();
		} else {
			initial=
				DataDSL.
					dataSet().
						individual(newReference().toManagedIndividual(templateId).named(name)).
							hasProperty(AgendaApplicationHelper.READ_ONLY_PROPERTY.toString()).
								withValue(new Date().toString()).
							hasLink(RDF.TYPE.qualifiedEntityName()).
								referringTo(newReference().toExternalIndividual().atLocation(LDP.BASIC_CONTAINER.as(URI.class))).
							build();
		}
		return initial;
	}

	@Override
	public void setup(Environment environment, Bootstrap<Configuration> bootstrap) {
		LOGGER.info("Configuring Agenda Application");

		PersonContainerHandler personContainerHandler   = new PersonContainerHandler();
		PersonHandler          personHandler            = new PersonHandler();

		ContactContainerHandler contactContainerHandler = new ContactContainerHandler();
		ContactHandler          contactHandler          = new ContactHandler();

		personContainerHandler.
			add(
				this.personContainerName,
				DataSetFactory.createDataSet(this.personContainerName));

		personContainerHandler.setPersonHandler(personHandler);
		personContainerHandler.setContactContainerHandler(contactContainerHandler);

		personHandler.setContactContainerHandler(contactContainerHandler);

		contactContainerHandler.setContactHandler(contactHandler);

		bootstrap.addHandler(personContainerHandler);
		bootstrap.addHandler(personHandler);
		bootstrap.addHandler(contactContainerHandler);
		bootstrap.addHandler(contactHandler);

		environment.publishResource(this.personContainerName, PersonContainerHandler.class, ROOT_PERSON_CONTAINER_PATH);

		LOGGER.info("Agenda Application Configuration completed.");
	}

	@Override
	public void initialize(WriteSession session) {
		LOGGER.info("Initializing Agenda Application");
		try {
			session.saveChanges();
			LOGGER.info("Agenda Application Initialization completed.");
		} catch (WriteSessionException e) {
			LOGGER.warn("Agenda Application Initialization failed.",e);
			throw new IllegalStateException(e);
		}
	}

	@Override
	public void shutdown() {
		LOGGER.info("Shutting down Agenda Application");
	}

}
