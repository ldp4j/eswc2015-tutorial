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

import static com.google.common.base.Preconditions.checkState;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicReference;

import org.ldp4j.application.data.FormatUtils;
import org.ldp4j.application.data.Individual;
import org.ldp4j.application.data.Literal;
import org.ldp4j.application.data.ManagedIndividualId;
import org.ldp4j.application.data.Name;
import org.ldp4j.application.data.NamingScheme;
import org.ldp4j.application.data.Value;
import org.ldp4j.application.data.ValueVisitor;
import org.ldp4j.application.session.ResourceSnapshot;
import org.ldp4j.tutorial.application.api.Contact;
import org.ldp4j.tutorial.application.api.Person;

import com.google.common.base.Objects;

final class AgendaApplicationUtils {

	private AgendaApplicationUtils() {
	}

	static String toString(ManagedIndividualId id) {
		return String.format("%s {Managed by: %s}",id.name(),id.managerId());
	}

	static String toString(Value value) {
		final AtomicReference<String> result=new AtomicReference<String>();
		value.accept(
			new ValueVisitor() {
				@Override
				public void visitLiteral(Literal<?> value) {
					result.set(String.format("%s [%s]",value.get(),value.get().getClass().getCanonicalName()));
				}
				@Override
				public void visitIndividual(Individual<?, ?> value) {
					result.set(FormatUtils.formatIndividualId(value));
				}
			}
		);
		return result.get();
	}

	static String toString(Contact contact) {
		return
			Objects.
				toStringHelper(Contact.class).
					omitNullValues().
						add("email",contact.getEmail()).
						add("fullName",contact.getFullName()).
						add("telephone",contact.getTelephone()).
						add("url",contact.getUrl()).
						toString();
	}

	static String toString(Person contact) {
		return
			Objects.
				toStringHelper(Person.class).
					omitNullValues().
						add("email",contact.getEmail()).
						add("name",contact.getName()).
						add("location",contact.getLocation()).
						add("workplaceHomepage",contact.getWorkplaceHomepage()).
						toString();
	}

	public static String personId(ResourceSnapshot resource) {
		Serializable id = resource.name().id();
		checkState(id instanceof String,"Person identifier should be a string not a %s",id.getClass().getCanonicalName());
		return (String)id;
	}

	public static ContactId contactId(ResourceSnapshot resource) {
		Serializable contactId = resource.name().id();
		checkState(contactId instanceof String,"Contact identifier should be a string not a %s",contactId.getClass().getCanonicalName());
		ResourceSnapshot contactsResource = resource.parent();
		checkState(contactsResource!=null,"Could not find contact's parent resource");
		ResourceSnapshot personResource = contactsResource.parent();
		checkState(personResource!=null,"Could not find contact's related person resource");
		Serializable personId = personResource.name().id();
		checkState(personId instanceof String,"Person identifier should be a string not a %s",personId.getClass().getCanonicalName());
		return ContactId.create((String)personId,(String)contactId);
	}

	public static Name<String> name(Contact contact, String... subKeys) {
		return NamingScheme.getDefault().name(contact.getEmail(),subKeys);
	}

	public static Name<String> name(Person person, String... subKeys) {
		return NamingScheme.getDefault().name(person.getEmail(),subKeys);
	}


}
