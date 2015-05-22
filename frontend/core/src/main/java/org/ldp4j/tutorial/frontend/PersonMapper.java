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
import org.ldp4j.application.data.DataSetUtils;
import org.ldp4j.application.data.ExternalIndividual;
import org.ldp4j.application.data.Individual;
import org.ldp4j.application.data.ManagedIndividual;
import org.ldp4j.application.data.ManagedIndividualId;
import org.ldp4j.application.data.Name;
import org.ldp4j.application.data.NamingScheme;
import org.ldp4j.application.data.constraints.Constraints;
import org.ldp4j.application.data.constraints.Constraints.Cardinality;
import org.ldp4j.application.data.constraints.Constraints.Shape;
import org.ldp4j.application.domain.RDF;
import org.ldp4j.application.ext.InconsistentContentException;
import org.ldp4j.application.ext.UnsupportedContentException;
import org.ldp4j.tutorial.application.api.Person;

import com.google.common.base.Objects;
import com.google.common.base.Optional;

public final class PersonMapper {

	private static final String PERSON = "http://xmlns.com/foaf/0.1/Person";
	private static final String WORKPLACE_HOMEPAGE = "http://xmlns.com/foaf/0.1/workplaceHomepage";
	private static final String LOCATION = "http://xmlns.com/foaf/0.1/based_near";
	private static final String NAME = "http://xmlns.com/foaf/0.1/name";
	private static final String EMAIL = "http://xmlns.com/foaf/0.1/mbox";

	private static final class MutablePerson implements Person {
		private String email;
		private String name;
		private String location;
		private String workplaceHomepage;

		public String getEmail() {
			return email;
		}

		public void setEmail(String account) {
			this.email = account;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getLocation() {
			return location;
		}

		public void setLocation(String location) {
			this.location = location;
		}

		public String getWorkplaceHomepage() {
			return workplaceHomepage;
		}

		public void setWorkplaceHomepage(String workplaceHomepage) {
			this.workplaceHomepage = workplaceHomepage;
		}
	}

	private PersonMapper() {
	}

	private static void addDatatypePropertyValue(DataSet dataSet, Name<String> name, String propertyURI, Object rawValue) {
		DataSetUtils.
			newHelper(dataSet).
				managedIndividual(name, PersonHandler.ID).
					property(propertyURI).
						withLiteral(rawValue);
	}

	private static void addObjectPropertyValue(DataSet dataSet, Name<String> name, String propertyURI, String uri) {
		if(uri==null) {
			return;
		}
		ManagedIndividualId individualId = ManagedIndividualId.createId(name, PersonHandler.ID);
		ManagedIndividual individual = dataSet.individual(individualId, ManagedIndividual.class);
		URI propertyId = URI.create(propertyURI);
		ExternalIndividual external = dataSet.individual(URI.create(uri),ExternalIndividual.class);
		individual.addValue(propertyId,external);
	}

	private static <T> T firstLiteralValue(Individual<?, ?> self, String propertyURI, final Class<? extends T> clazz) {
		return
			DataSetUtils.
				newHelper(self).
					property(propertyURI).
						firstValue(clazz);
	}

	private static URI firstIndividualValue(Individual<?, ?> self, String propertyURI) {
		return
			DataSetUtils.
				newHelper(self).
					property(propertyURI).
						firstIndividual(ExternalIndividual.class);
	}

	public static Name<String> personName(Person person) {
		return NamingScheme.getDefault().name(person.getEmail());
	}

	public static Name<String> contactsName(Person person) {
		return NamingScheme.getDefault().name(person.getEmail(),"contacts");
	}

	public static DataSet toDataSet(Person person) {
		Name<String> personName=personName(person);

		DataSet dataSet = DataSetFactory.createDataSet(personName);

		addObjectPropertyValue(dataSet,personName,RDF.TYPE.qualifiedEntityName(),PERSON);
		addObjectPropertyValue(dataSet,personName,EMAIL,person.getEmail());
		addDatatypePropertyValue(dataSet,personName,NAME,person.getName());
		addObjectPropertyValue(dataSet,personName,LOCATION,person.getLocation());
		addObjectPropertyValue(dataSet,personName,WORKPLACE_HOMEPAGE,person.getWorkplaceHomepage());

		return dataSet;
	}

	public static Person toPerson(Individual<?,?> self) {
		MutablePerson result = new MutablePerson();
		Optional<URI> email = Optional.fromNullable(firstIndividualValue(self,EMAIL));
		result.setEmail(email.isPresent()?email.get().toString():null);
		result.setName(firstLiteralValue(self,NAME,String.class));
		Optional<URI> location = Optional.fromNullable(firstIndividualValue(self,LOCATION));
		result.setLocation(location.isPresent()?location.get().toString():null);
		Optional<URI> workplaceHomepage= Optional.fromNullable(firstIndividualValue(self,WORKPLACE_HOMEPAGE));
		result.setWorkplaceHomepage(workplaceHomepage.isPresent()?workplaceHomepage.get().toString():null);
		return result;
	}

	public static Person enforceConsistency(Individual<?, ?> individual) throws UnsupportedContentException {
		Optional<URI> type = Optional.fromNullable(firstIndividualValue(individual,RDF.TYPE.qualifiedEntityName()));
		Person newPerson = toPerson(individual);
		if(!type.isPresent() || !type.get().toString().equals(PERSON) || newPerson.getEmail()==null || newPerson.getName()==null || newPerson.getLocation()==null || newPerson.getWorkplaceHomepage()==null) {
			Shape shape=
				Constraints.
					shape().
						withLabel("person").
						withComment("Person resource shape").
						withPropertyConstraint(
							Constraints.
								propertyConstraint(URI.create(EMAIL)).
									withCardinality(Cardinality.mandatory())).
						withPropertyConstraint(
							Constraints.
								propertyConstraint(URI.create(NAME)).
									withCardinality(Cardinality.mandatory())).
						withPropertyConstraint(
							Constraints.
								propertyConstraint(URI.create(LOCATION)).
									withCardinality(Cardinality.mandatory())).
						withPropertyConstraint(
							Constraints.
								propertyConstraint(URI.create(WORKPLACE_HOMEPAGE)).
									withCardinality(Cardinality.mandatory()));
			Constraints constraints =
				Constraints.
					constraints().
						withTypeShape(URI.create(PERSON),shape);
			throw new UnsupportedContentException("Incomplete person definition",constraints);
		}
		return newPerson;
	}

	public static Person enforceConsistency(Individual<?, ?> individual, Person currentPerson) throws InconsistentContentException {
		Person updatedPerson = toPerson(individual);
		if(!Objects.equal(currentPerson.getEmail(),updatedPerson.getEmail())) {
			DataSet tmp =
					DataSetFactory.createDataSet(individual.dataSet().name());
				ExternalIndividual emailIndividual = tmp.individual(URI.create(currentPerson.getEmail()),ExternalIndividual.class);
			Shape shape=
				Constraints.
					shape().
						withPropertyConstraint(
							Constraints.
								propertyConstraint(URI.create(EMAIL)).
									withValue(emailIndividual)).
						withPropertyConstraint(
							Constraints.
								propertyConstraint(URI.create(NAME)).
									withCardinality(Cardinality.mandatory())).
						withPropertyConstraint(
							Constraints.
								propertyConstraint(URI.create(LOCATION)).
									withCardinality(Cardinality.mandatory())).
						withPropertyConstraint(
							Constraints.
								propertyConstraint(URI.create(WORKPLACE_HOMEPAGE)).
									withCardinality(Cardinality.mandatory()));
			Constraints constraints =
				Constraints.
					constraints().
						withNodeShape(individual,shape);
			throw new InconsistentContentException("Person account cannot be modified",constraints);
		}
		return updatedPerson;
	}

}