/**
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 *   This file is part of the LDP4j Project:
 *     http://www.ldp4j.org/
 *
 *   Center for Open Middleware
 *     http://www.centeropenmiddleware.com/
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 *   Copyright (C) 2014-2016 Center for Open Middleware.
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
 *   Artifact    : org.ldp4j.tutorial.frontend:frontend-core:1.0.0
 *   Bundle      : frontend-core-1.0.0.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.ldp4j.tutorial.frontend.contact;

import java.net.URI;

import org.ldp4j.application.data.DataSet;
import org.ldp4j.application.data.DataSets;
import org.ldp4j.application.data.ExternalIndividual;
import org.ldp4j.application.data.Name;
import org.ldp4j.application.data.NamingScheme;
import org.ldp4j.application.data.constraints.Constraints;
import org.ldp4j.application.data.constraints.Constraints.Cardinality;
import org.ldp4j.application.data.constraints.Constraints.PropertyConstraint;
import org.ldp4j.application.data.constraints.Constraints.Shape;
import org.ldp4j.application.ext.InconsistentContentException;
import org.ldp4j.application.ext.UnsupportedContentException;
import org.ldp4j.tutorial.application.api.Contact;
import org.ldp4j.tutorial.frontend.util.IdentityUtil;
import org.ldp4j.tutorial.frontend.util.Typed;

import com.google.common.base.Objects;

final class ContactConstraints implements ContactVocabulary {

	private static final String VCARD_EMAIL ="http://www.w3.org/2006/vcard/ns#Email";

	private static final String XSD_STRING = "http://www.w3.org/2001/XMLSchema#string";

	private static final String RDFS_RESOURCE = "http://www.w3.org/2000/01/rdf-schema#Resource";

	private ContactConstraints() {
	}

	private static Constraints createConstraints(Contact contact) {
		Name<?> name=NamingScheme.getDefault().name("");
		if(contact!=null) {
			name=IdentityUtil.name(contact);
		}

		DataSet tmp=DataSets.createDataSet(name);
		ExternalIndividual individualIndividual = tmp.individual(URI.create(INDIVIDUAL),ExternalIndividual.class);
		ExternalIndividual voiceIndividual      = tmp.individual(URI.create(VOICE),ExternalIndividual.class);
		ExternalIndividual homeIndividual       = tmp.individual(URI.create(HOME),ExternalIndividual.class);
		ExternalIndividual typeIndividual       = tmp.individual(URI.create(TYPE),ExternalIndividual.class);

		Shape telephoneShape=
			Constraints.
				shape().
					withLabel("TelephoneShape").
					withComment("Contact telephone resource shape").
					withPropertyConstraint(
						Constraints.
							propertyConstraint(typeIndividual.id()).
								withValue(homeIndividual,voiceIndividual)).
					withPropertyConstraint(
						Constraints.
							propertyConstraint(URI.create(NUMBER)).
								withValueType(URI.create(RDFS_RESOURCE)).
								withCardinality(Cardinality.mandatory()));

		PropertyConstraint emailConstraint = null;
		if(contact!=null) {
			ExternalIndividual emailIndividual=
				tmp.individual(
					URI.create(contact.getEmail()),
					ExternalIndividual.class);
			emailConstraint=
				Constraints.
					propertyConstraint(URI.create(EMAIL)).
						withValue(emailIndividual);
		} else {
			emailConstraint=
				Constraints.
						propertyConstraint(URI.create(EMAIL)).
							withCardinality(Cardinality.mandatory());
		}

		Shape contactShape=
			Constraints.
				shape().
					withLabel("ContactShape").
					withComment("Contact resource shape").
					withPropertyConstraint(
						Constraints.
							propertyConstraint(typeIndividual.id()).
								withValue(individualIndividual)).
					withPropertyConstraint(
						emailConstraint.
							withValueType(URI.create(VCARD_EMAIL))).
					withPropertyConstraint(
						Constraints.
							propertyConstraint(URI.create(FULL_NAME)).
								withDatatype(URI.create(XSD_STRING)).
								withCardinality(Cardinality.mandatory())).
					withPropertyConstraint(
						Constraints.
							propertyConstraint(URI.create(URL)).
								withValueType(URI.create(RDFS_RESOURCE)).
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
			constraints.
				withNodeShape(
					IdentityUtil.contactIndividual(tmp,contact),
					contactShape);
		} else {
			constraints.withTypeShape(URI.create(INDIVIDUAL),contactShape);
		}
		return constraints;
	}

	static void validate(Typed<Contact> typedContact) throws UnsupportedContentException {
		validate(null,typedContact);
	}

	static void validate(Contact currentContact, Typed<Contact> typedContact) throws UnsupportedContentException {
		Contact contact = typedContact.get();
		if(!typedContact.hasType(INDIVIDUAL) || contact.getEmail()==null || contact.getUrl()==null || contact.getFullName()==null || contact.getTelephone()==null) {
			throw new UnsupportedContentException("Incomplete contact definition",createConstraints(currentContact));
		}
	}

	static void checkConstraints(Contact currentContact, Typed<Contact> updatedContact) throws InconsistentContentException {
		if(!Objects.equal(currentContact.getEmail(),updatedContact.get().getEmail())) {
			throw new InconsistentContentException("Contact email cannot be modified",createConstraints(currentContact));
		}
	}

}
