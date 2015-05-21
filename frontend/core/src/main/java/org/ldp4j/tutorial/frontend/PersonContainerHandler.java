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
import org.ldp4j.application.data.DataSetHelper;
import org.ldp4j.application.data.ExternalIndividual;
import org.ldp4j.application.data.Individual;
import org.ldp4j.application.data.IndividualVisitor;
import org.ldp4j.application.data.LanguageLiteral;
import org.ldp4j.application.data.Literal;
import org.ldp4j.application.data.LiteralVisitor;
import org.ldp4j.application.data.LocalIndividual;
import org.ldp4j.application.data.ManagedIndividual;
import org.ldp4j.application.data.Name;
import org.ldp4j.application.data.NamingScheme;
import org.ldp4j.application.data.NewIndividual;
import org.ldp4j.application.data.Property;
import org.ldp4j.application.data.RelativeIndividual;
import org.ldp4j.application.data.TypedLiteral;
import org.ldp4j.application.data.Value;
import org.ldp4j.application.data.ValueVisitor;
import org.ldp4j.application.ext.ApplicationRuntimeException;
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
public class PersonContainerHandler extends InMemoryContainerHandler {

	private static final Logger LOGGER=LoggerFactory.getLogger(PersonContainerHandler.class);

	public static final String ID="PersonContainerHandler";

	private PersonHandler handler;

	private ContactContainerHandler contactContainerHandler;

	private AgendaService service;

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
		Property property = self.property(URI.create(propertyURI));
		if(!property.hasValues()) {
			throw new RuntimeException();
		}
		Value value = property.iterator().next();
		class Extractor implements ValueVisitor {

			private T value=null;

			@Override
			public void visitLiteral(Literal<?> value) {
				value.accept(new LiteralVisitor() {

					private T cast(Object object) {
						// Extension for supporting conversions
						return clazz.cast(object);
					}

					@Override
					public void visitLiteral(Literal<?> literal) {
						Extractor.this.value=cast(literal.get());
					}

					@Override
					public void visitTypedLiteral(TypedLiteral<?> literal) {
						Extractor.this.value=cast(literal.get());
					}

					@Override
					public void visitLanguageLiteral(LanguageLiteral literal) {
						Extractor.this.value=cast(literal.get());
					}

				});
			}

			@Override
			public void visitIndividual(Individual<?, ?> value) {
				throw new RuntimeException("No literal value");
			}

			public T getValue() {
				return this.value;
			}

		};
		Extractor extractor = new Extractor();
		value.accept(extractor);
		return extractor.getValue();
	}
	private URI firstIndividualValue(Individual<?, ?> self, String propertyURI) {
		Property property = self.property(URI.create(propertyURI));
		if(!property.hasValues()) {
			throw new RuntimeException();
		}
		Value value = property.iterator().next();
		class Extractor implements ValueVisitor {

			private URI value=null;

			@Override
			public void visitLiteral(Literal<?> value) {
				throw new RuntimeException("No individual value");
			}

			@Override
			public void visitIndividual(Individual<?, ?> value) {
				value.accept(new IndividualVisitor() {

					@Override
					public void visitRelativeIndividual(RelativeIndividual individual) {
						throw new RuntimeException("No external individual");
					}

					@Override
					public void visitNewIndividual(NewIndividual individual) {
						throw new RuntimeException("No external individual");
					}

					@Override
					public void visitManagedIndividual(ManagedIndividual individual) {
						throw new RuntimeException("No external individual");
					}

					@Override
					public void visitLocalIndividual(LocalIndividual individual) {
						throw new RuntimeException("No external individual");
					}

					@Override
					public void visitExternalIndividual(ExternalIndividual individual) {
						Extractor.this.value=individual.id();
					}
				});
			}

			public URI getValue() {
				return this.value;
			}

		};
		Extractor extractor = new Extractor();
		value.accept(extractor);
		return extractor.getValue();
	}

	public void setAgendaService(AgendaService application) {
		this.service = application;
	}

	public AgendaService getAgendaService() {
		return this.service;
	}

}
