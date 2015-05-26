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
 *   Artifact    : org.ldp4j.tutorial.application:application-core:1.0.0-SNAPSHOT
 *   Bundle      : application-core-1.0.0-SNAPSHOT.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.ldp4j.tutorial.application.core;

import java.util.*;

import org.ldp4j.tutorial.application.api.ContactsService;
import org.ldp4j.tutorial.application.api.Contact;
import org.ldp4j.tutorial.application.api.Person;

public class InMemoryContactsService extends ContactsService {

	private final Map<String,Account> persons;

	public InMemoryContactsService() {
		this.persons=new LinkedHashMap<String, Account>();
	}

	@Override
	public Person createPerson(String account, String name, String location, String workplaceHomepage) {
		Person p=new MutablePerson();
		p.setEmail(account);
		p.setName(name);
		p.setLocation(location);
		p.setWorkplaceHomepage(workplaceHomepage);
		Account acc = new Account(p);
		this.persons.put(account, acc);
		return p;
	}

	@Override
	public Person getPerson(String account) {

		Account acc = this.persons.get(account);
		if(acc != null){
			return acc.getPerson();
		} else {
			return null;
		}
	}

	@Override
	public Collection<Person> listPersons() {

		List<Person> personList = new ArrayList<Person>(persons.size());
		for (Account account : persons.values()) {
			personList.add(account.getPerson());
		}

		return personList;
	}

	@Override
	public boolean deletePerson(String account) {
		if(persons.remove(account) != null){
			return true;
		} else {
			return false;
		}
	}

	@Override
	public Contact addContactToPerson(String personId, String fullName,
			String url, String email, String telephone) {
		// TODO Auto-generated method stub

		Account acc = persons.get(personId);
		if(acc == null){
			throw new RuntimeException(String.format("Invalid person id - '%s'", personId));
		}
		Contact contact = new MutableContact();
		contact.setEmail(email);
		contact.setFullName(fullName);
		contact.setUrl(url);
		contact.setTelephone(telephone);

		acc.addContact(contact);

		return contact;
	}

	@Override
	public Contact getPersonContact(String account, String email) {

		Account acc = persons.get(account);
		if(acc == null){
			return null;
		}

		return acc.getContact(email);
	}

	@Override
	public Collection<Contact> listPersonContacts(String account) {
		Account acc = persons.get(account);
		if(acc == null){
			throw new RuntimeException(String.format("Invalid person id - '%s'", account));
		}
		return acc.listContacts();
	}

	@Override
	public boolean deletePersonContact(String account, String email) {
		Account acc = persons.get(account);
		if(acc == null) {
			return false;
		}
		return acc.deleteContact(email);
	}

}
