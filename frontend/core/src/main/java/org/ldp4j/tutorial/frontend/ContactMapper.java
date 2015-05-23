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
import java.util.Set;

import org.ldp4j.application.data.DataSet;
import org.ldp4j.application.data.DataSetFactory;
import org.ldp4j.application.data.DataSetHelper;
import org.ldp4j.application.data.DataSetUtils;
import org.ldp4j.application.data.ExternalIndividual;
import org.ldp4j.application.data.Individual;
import org.ldp4j.application.data.IndividualHelper;
import org.ldp4j.application.data.Name;
import org.ldp4j.application.data.NamingScheme;
import org.ldp4j.application.domain.RDF;
import org.ldp4j.tutorial.application.api.Contact;

import com.google.common.base.Optional;

public final class ContactMapper implements ContactVocabulary {

	private static final class MutableContact implements Contact {
		private String fullName;
		private String url;
		private String email;
		private String telephone;
		@Override
		public String getFullName() {
			return fullName;
		}
		@Override
		public void setFullName(String fullName) {
			this.fullName = fullName;
		}
		@Override
		public String getUrl() {
			return url;
		}
		@Override
		public void setUrl(String url) {
			this.url = url;
		}
		@Override
		public String getEmail() {
			return email;
		}
		@Override
		public void setEmail(String email) {
			this.email = email;
		}
		@Override
		public String getTelephone() {
			return telephone;
		}
		@Override
		public void setTelephone(String telephone) {
			this.telephone = telephone;
		}
	}

	private ContactMapper() {
	}

	private static <T> T firstLiteralValue(Individual<?, ?> individual, String propertyURI, final Class<? extends T> clazz) {
		return
			DataSetUtils.
				newHelper(individual).
					property(propertyURI).
						firstValue(clazz);
	}

	private static URI firstIndividualValue(Individual<?, ?> individual, String propertyURI) {
		return
			DataSetUtils.
				newHelper(individual).
					property(propertyURI).
						firstIndividual(ExternalIndividual.class);
	}

	private static Set<URI> types(Individual<?,?> individual) {
		return
			DataSetUtils.
				newHelper(individual).
					types();
	}

	private static Name<String> telephoneName(Contact contact) {
		return NamingScheme.getDefault().name(contact.getEmail(),"telephone");
	}

	public static Name<String> contactName(Contact contact) {
		return NamingScheme.getDefault().name(contact.getEmail());
	}

	public static Typed<Contact> toContact(Individual<?,?> individual) {
		MutableContact contact = new MutableContact();
		Typed<Contact> result = Typed.<Contact>create(contact);

		for(URI type:types(individual)) {
			result.withType(type.toString());
		}

		contact.setFullName(firstLiteralValue(individual,FULL_NAME,String.class));

		Optional<URI> url = Optional.fromNullable(firstIndividualValue(individual,URL));
		contact.setUrl(url.isPresent()?url.get().toString():null);

		Optional<URI> email= Optional.fromNullable(firstIndividualValue(individual,EMAIL));
		contact.setEmail(email.isPresent()?email.get().toString():null);

		IndividualHelper helper =
			DataSetUtils.
				newHelper(individual).
					property(TELEPHONE).
						firstIndividual();

		if(helper!=null) {
			Set<URI> types = helper.types();
			if(types.contains(URI.create(VOICE)) && types.contains(URI.create(HOME))) {
				Optional<URI> number = Optional.fromNullable(helper.property(NUMBER).firstIndividual(ExternalIndividual.class));
				contact.setTelephone(number.isPresent()?number.get().toString():null);
			}
		}

		return result;
	}

	public static DataSet toDataSet(Contact contact) {
		Name<String> contactName=contactName(contact);
		Name<?> telephoneName=telephoneName(contact);

		DataSet dataSet=DataSetFactory.createDataSet(contactName);

		DataSetHelper helper=DataSetUtils.newHelper(dataSet);

		helper.
			managedIndividual(contactName, ContactHandler.ID).
				property(RDF.TYPE).
					withIndividual(INDIVIDUAL).
				property(FULL_NAME).
					withLiteral(contact.getFullName()).
				property(EMAIL).
					withIndividual(contact.getEmail()).
				property(URL).
					withIndividual(contact.getUrl()).
				property(TELEPHONE).
					withIndividual(telephoneName);

		helper.
			localIndividual(telephoneName).
				property(RDF.TYPE).
					withIndividual(HOME).
					withIndividual(VOICE).
				property(NUMBER).
					withIndividual(contact.getTelephone());

		return dataSet;
	}

}