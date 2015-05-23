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

import java.net.URI;

import org.ldp4j.application.data.DataSet;
import org.ldp4j.application.data.DataSetFactory;
import org.ldp4j.application.data.DataSetUtils;
import org.ldp4j.application.data.ExternalIndividual;
import org.ldp4j.application.data.Individual;
import org.ldp4j.application.data.ManagedIndividual;
import org.ldp4j.application.data.ManagedIndividualId;
import org.ldp4j.application.data.Name;
import org.ldp4j.application.domain.RDF;
import org.ldp4j.tutorial.application.api.Person;
import org.ldp4j.tutorial.frontend.util.IdentityUtil;
import org.ldp4j.tutorial.frontend.util.Mapper;
import org.ldp4j.tutorial.frontend.util.Typed;

import com.google.common.base.Optional;

final class PersonMapper implements PersonVocabulary {

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

	public static DataSet toDataSet(Person person) {
		Name<String> personName=IdentityUtil.name(person);

		DataSet dataSet = DataSetFactory.createDataSet(personName);

		addObjectPropertyValue(dataSet,personName,RDF.TYPE.qualifiedEntityName(),PERSON);
		addObjectPropertyValue(dataSet,personName,EMAIL,person.getEmail());
		addDatatypePropertyValue(dataSet,personName,NAME,person.getName());
		addObjectPropertyValue(dataSet,personName,LOCATION,person.getLocation());
		addObjectPropertyValue(dataSet,personName,WORKPLACE_HOMEPAGE,person.getWorkplaceHomepage());

		return dataSet;
	}

	public static Typed<Person> toPerson(Individual<?,?> individual) {
		MutablePerson person = new MutablePerson();
		Typed<Person> result=Typed.<Person>create(person);
		for(URI uri:Mapper.create(individual).types()) {
			result.withType(uri.toString());
		}
		Optional<URI> email = Optional.fromNullable(firstIndividualValue(individual,EMAIL));
		person.setEmail(email.isPresent()?email.get().toString():null);
		person.setName(firstLiteralValue(individual,NAME,String.class));
		Optional<URI> location = Optional.fromNullable(firstIndividualValue(individual,LOCATION));
		person.setLocation(location.isPresent()?location.get().toString():null);
		Optional<URI> workplaceHomepage= Optional.fromNullable(firstIndividualValue(individual,WORKPLACE_HOMEPAGE));
		person.setWorkplaceHomepage(workplaceHomepage.isPresent()?workplaceHomepage.get().toString():null);
		return result;
	}

}