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
package org.ldp4j.tutorial.frontend.contact;

import java.net.URI;
import java.util.Set;

import org.ldp4j.application.data.DataSet;
import org.ldp4j.application.data.DataSetFactory;
import org.ldp4j.application.data.DataSetHelper;
import org.ldp4j.application.data.DataSetUtils;
import org.ldp4j.application.data.Individual;
import org.ldp4j.application.data.Name;
import org.ldp4j.application.domain.RDF;
import org.ldp4j.tutorial.application.api.Contact;
import org.ldp4j.tutorial.frontend.util.IdentityUtil;
import org.ldp4j.tutorial.frontend.util.Mapper;
import org.ldp4j.tutorial.frontend.util.Typed;

import com.google.common.base.Optional;

final class ContactMapper implements ContactVocabulary {

	private static final class MutableContact implements Contact {

		private String fullName;
		private String url;
		private String email;
		private String telephone;

		private MutableContact() {
		}

		private MutableContact(Contact contact) {
			this();
			setEmail(contact.getEmail());
			setFullName(contact.getFullName());
			setTelephone(contact.getTelephone());
			setUrl(contact.getUrl());
		}

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

	public static Typed<Contact> toContact(Individual<?,?> individual) {
		MutableContact contact = new MutableContact();
		Typed<Contact> result = Typed.<Contact>create(contact);

		Mapper contactMapper=Mapper.create(individual);

		for(URI type:contactMapper.types()) {
			result.withType(type.toString());
		}

		contact.setFullName(contactMapper.literal(FULL_NAME,String.class));

		Optional<URI> url = contactMapper.individual(URL);
		contact.setUrl(Mapper.toStringOrNull(url));

		Optional<URI> email=contactMapper.individual(EMAIL);
		contact.setEmail(Mapper.toStringOrNull(email));

		Mapper telephoneMapper=contactMapper.individualMapper(TELEPHONE);

		Set<URI> types = telephoneMapper.types();
		if(types.contains(URI.create(VOICE)) && types.contains(URI.create(HOME))) {
			Optional<URI> number = telephoneMapper.individual(NUMBER);
			contact.setTelephone(Mapper.toStringOrNull(number));
		}

		return result;
	}

	public static DataSet toDataSet(Contact contact) {
		Name<String> contactName=IdentityUtil.name(contact);
		Name<?> telephoneName=IdentityUtil.name(contact,"telephone");

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

	public static Contact clone(Contact contact) {
		return new MutableContact(contact);
	}

	public static void copy(Contact source, Contact target) {
		target.setFullName(source.getFullName());
		target.setTelephone(source.getTelephone());
		target.setUrl(source.getUrl());
	}

}