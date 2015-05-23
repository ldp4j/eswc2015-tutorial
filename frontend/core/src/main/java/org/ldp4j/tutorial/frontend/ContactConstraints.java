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
import org.ldp4j.application.data.constraints.Constraints.PropertyConstraint;
import org.ldp4j.application.data.constraints.Constraints.Shape;
import org.ldp4j.application.ext.InconsistentContentException;
import org.ldp4j.application.ext.UnsupportedContentException;
import org.ldp4j.tutorial.application.api.Contact;

import com.google.common.base.Objects;

public final class ContactConstraints implements ContactVocabulary {

	private ContactConstraints() {
	}

	private static Constraints createConstraints(Contact contact) {
		Name<?> name=NamingScheme.getDefault().name("");
		if(contact!=null) {
			name=ContactMapper.contactName(contact);
		}
		DataSet tmp=DataSetFactory.createDataSet(name);
		ExternalIndividual individualIndividual = tmp.individual(URI.create(INDIVIDUAL),ExternalIndividual.class);
		ExternalIndividual voiceIndividual      = tmp.individual(URI.create(VOICE),ExternalIndividual.class);
		ExternalIndividual homeIndividual       = tmp.individual(URI.create(HOME),ExternalIndividual.class);
		ExternalIndividual typeIndividual       = tmp.individual(URI.create(TYPE),ExternalIndividual.class);
		Shape telephoneShape=
			Constraints.
				shape().
					withLabel("telephone").
					withComment("Contact telephone resource shape").
					withPropertyConstraint(
						Constraints.
							propertyConstraint(typeIndividual.id()).
								withValue(homeIndividual,voiceIndividual)).
					withPropertyConstraint(
						Constraints.
							propertyConstraint(URI.create(NUMBER)).
								withCardinality(Cardinality.mandatory()));
		PropertyConstraint emailConstraint = null;
		if(contact!=null) {
			emailConstraint=
				Constraints.
					propertyConstraint(URI.create(EMAIL)).
						withValue(
							DataSetUtils.newLiteral(contact.getEmail()));
		} else {
			emailConstraint=
					Constraints.
							propertyConstraint(URI.create(EMAIL)).
								withCardinality(Cardinality.mandatory());
		}
		Shape contactShape=
			Constraints.
				shape().
					withLabel("contact").
					withComment("Contact resource shape").
					withPropertyConstraint(
						Constraints.
							propertyConstraint(typeIndividual.id()).
								withValue(individualIndividual)).
					withPropertyConstraint(emailConstraint).
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
								withCardinality(Cardinality.mandatory()).
								withValueShape(telephoneShape));
		Constraints constraints =
				Constraints.
					constraints();
		if(contact!=null) {
			ManagedIndividualId contactIndividualId = ManagedIndividualId.createId(name, ContactHandler.ID);
			ManagedIndividual contactIndividual = tmp.individual(contactIndividualId, ManagedIndividual.class);
			constraints.withNodeShape(contactIndividual,contactShape);
		} else {
			constraints.withTypeShape(URI.create(INDIVIDUAL),contactShape);
		}
		return constraints;
	}

	@Deprecated
	private static Constraints createConstraints(Individual<?, ?> individual, Contact contact) {
		return createConstraints(contact);
	}

	@Deprecated
	public static Contact enforceConsistency(Individual<?, ?> individual) throws UnsupportedContentException {
		Typed<Contact> metaContact = ContactMapper.toContact(individual);
		Contact contact = metaContact.get();
		if(!metaContact.hasType(INDIVIDUAL) || contact.getEmail()==null || contact.getUrl()==null || contact.getFullName()==null || contact.getTelephone()==null) {
			throw new UnsupportedContentException("Incomplete contact definition",createConstraints(individual,null));
		}
		return contact;
	}

	@Deprecated
	public static Contact checkConstraints(Individual<?, ?> individual, Contact currentContact) throws InconsistentContentException, UnsupportedContentException {
		Contact updatedContact = enforceConsistency(individual);
		checkConstraints(currentContact, updatedContact);
		return updatedContact;
	}

	public static void checkConstraints(Contact currentContact, Contact updatedContact) throws InconsistentContentException {
		if(!Objects.equal(currentContact.getEmail(),updatedContact.getEmail())) {
			throw new InconsistentContentException("Contact email cannot be modified",createConstraints(currentContact));
		}
	}

	public static void validate(Typed<Contact> typedContact) throws UnsupportedContentException {
		Contact contact = typedContact.get();
		if(!typedContact.hasType(INDIVIDUAL) || contact.getEmail()==null || contact.getUrl()==null || contact.getFullName()==null || contact.getTelephone()==null) {
			throw new UnsupportedContentException("Incomplete contact definition",createConstraints(null));
		}
	}

}
