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
import org.ldp4j.application.data.IndividualHelper;
import org.ldp4j.application.data.Name;
import org.ldp4j.application.data.NamingScheme;
import org.ldp4j.application.data.constraints.Constraints;
import org.ldp4j.application.data.constraints.Constraints.Cardinality;
import org.ldp4j.application.data.constraints.Constraints.Shape;
import org.ldp4j.application.domain.RDF;
import org.ldp4j.application.ext.InconsistentContentException;
import org.ldp4j.application.ext.UnsupportedContentException;
import org.ldp4j.tutorial.application.api.Contact;
import org.slf4j.LoggerFactory;

import com.google.common.base.Objects;
import com.google.common.base.Optional;

public final class ContactMapper {

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

	private static final String TYPE = RDF.TYPE.qualifiedEntityName();

	private static final String INDIVIDUAL = "http://www.w3.org/2006/vcard/ns#Individual";
	private static final String HOME       = "http://www.w3.org/2006/vcard/ns#Home";
	private static final String VOICE      = "http://www.w3.org/2006/vcard/ns#Voice";

	private static final String TELEPHONE  = "http://www.w3.org/2006/vcard/ns#hasTelephone";
	private static final String NUMBER     = "http://www.w3.org/2006/vcard/ns#hasValue";
	private static final String URL        = "http://www.w3.org/2006/vcard/ns#hasURL";
	private static final String EMAIL      = "http://www.w3.org/2006/vcard/ns#hasEmail";
	private static final String FULL_NAME  = "http://www.w3.org/2006/vcard/ns#fn";

	private ContactMapper() {
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

	private static Name<String> telephoneName(Contact contact) {
		return NamingScheme.getDefault().name(contact.getEmail(),"telephone");
	}

	public static Name<String> contactName(Contact contact) {
		return NamingScheme.getDefault().name(contact.getEmail());
	}

	public static DataSet toDataSet(Contact contact) {
		Name<String> contactName=contactName(contact);
		Name<?> telephoneName=telephoneName(contact);

		DataSet dataSet = DataSetFactory.createDataSet(contactName);

		DataSetUtils.
			newHelper(dataSet).
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
		DataSetUtils.
			newHelper(dataSet).
				localIndividual(telephoneName).
					property(RDF.TYPE).
						withIndividual(HOME).
						withIndividual(VOICE).
					property(NUMBER).
						withIndividual(contact.getTelephone());

		return dataSet;
	}

	public static Contact toContact(Individual<?,?> self) {
		MutableContact result = new MutableContact();
		result.setFullName(firstLiteralValue(self,FULL_NAME,String.class));
		Optional<URI> url = Optional.fromNullable(firstIndividualValue(self,URL));
		result.setUrl(url.isPresent()?url.get().toString():null);
		Optional<URI> email= Optional.fromNullable(firstIndividualValue(self,EMAIL));
		result.setEmail(email.isPresent()?email.get().toString():null);

		IndividualHelper helper =
			DataSetUtils.
				newHelper(self).
					property(TELEPHONE).
						firstIndividual();

		if(helper!=null) {
			Optional<URI> number = Optional.fromNullable(helper.property(NUMBER).firstIndividual(ExternalIndividual.class));
			result.setTelephone(number.isPresent()?number.get().toString():null);
		}

		return result;
	}

	public static Contact enforceConsistency(Individual<?, ?> individual) throws UnsupportedContentException {
		Optional<URI> type = Optional.fromNullable(firstIndividualValue(individual,RDF.TYPE.qualifiedEntityName()));
		Contact newPerson = toContact(individual);
		if(!type.isPresent() || !type.get().toString().equals(INDIVIDUAL) || newPerson.getEmail()==null || newPerson.getUrl()==null || newPerson.getFullName()==null || newPerson.getTelephone()==null) {
			DataSet tmp =
				DataSetFactory.createDataSet(individual.dataSet().name());
			ExternalIndividual individualIndividual = tmp.individual(URI.create(INDIVIDUAL),ExternalIndividual.class);
			ExternalIndividual voiceIndividual = tmp.individual(URI.create(VOICE),ExternalIndividual.class);
			ExternalIndividual homeIndividual = tmp.individual(URI.create(HOME),ExternalIndividual.class);
			ExternalIndividual typeIndividual = tmp.individual(URI.create(TYPE),ExternalIndividual.class);
			Shape telephoneShape=
				Constraints.
					shape().
						withLabel("telephone").
						withComment("Telephone resource shape").
						withPropertyConstraint(
							Constraints.
								propertyConstraint(typeIndividual.id()).
									withValue(homeIndividual,voiceIndividual)).
						withPropertyConstraint(
							Constraints.
								propertyConstraint(URI.create(NUMBER)).
									withCardinality(Cardinality.mandatory()));
			Shape individualShape=
				Constraints.
					shape().
						withLabel("individual").
						withComment("Individual resource shape").
						withPropertyConstraint(
							Constraints.
								propertyConstraint(typeIndividual.id()).
									withValue(individualIndividual)).
						withPropertyConstraint(
							Constraints.
								propertyConstraint(URI.create(FULL_NAME)).
									withCardinality(Cardinality.mandatory())).
						withPropertyConstraint(
							Constraints.
								propertyConstraint(URI.create(EMAIL)).
									withCardinality(Cardinality.mandatory())).
						withPropertyConstraint(
							Constraints.
								propertyConstraint(URI.create(URL)).
									withCardinality(Cardinality.mandatory())).
						withPropertyConstraint(
							Constraints.
								propertyConstraint(URI.create(TELEPHONE)).
									withCardinality(Cardinality.mandatory()).
									withValueShape(telephoneShape));
			Constraints constraints =
				Constraints.
					constraints().
						withTypeShape(URI.create(INDIVIDUAL),individualShape);

			LoggerFactory.getLogger(ContactMapper.class).trace(constraints.toString());

			throw new UnsupportedContentException("Incomplete contact definition",constraints);
		}
		return newPerson;
	}

	public static Contact enforceConsistency(Individual<?, ?> individual, Contact currentContact) throws InconsistentContentException {
		Contact updatedContact = toContact(individual);
		if(!Objects.equal(currentContact.getEmail(),updatedContact.getEmail())) {
			Shape shape=
				Constraints.
					shape().
						withPropertyConstraint(
							Constraints.
								propertyConstraint(URI.create(EMAIL)).
									withValue(DataSetUtils.newLiteral(currentContact.getEmail()))).
						withPropertyConstraint(
							Constraints.
								propertyConstraint(URI.create(FULL_NAME)).
									withCardinality(Cardinality.mandatory())).
						withPropertyConstraint(
							Constraints.
								propertyConstraint(URI.create(URL)).
									withCardinality(Cardinality.mandatory())).
						withPropertyConstraint(
							Constraints.
								propertyConstraint(URI.create(TELEPHONE)).
									withCardinality(Cardinality.mandatory()));
			// TODO: Add additional constraints for "formatting" vcard:Telephone
			Constraints constraints =
				Constraints.
					constraints().
						withNodeShape(individual,shape);
			throw new InconsistentContentException("Contact email cannot be modified",constraints);
		}
		return updatedContact;
	}

}