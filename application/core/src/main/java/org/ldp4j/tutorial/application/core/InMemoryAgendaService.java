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
 */
package org.ldp4j.tutorial.application.core;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.ldp4j.tutorial.application.api.AgendaService;
import org.ldp4j.tutorial.application.api.Contact;
import org.ldp4j.tutorial.application.api.Person;

public class InMemoryAgendaService extends AgendaService {

	private final Map<String,Person> persons;

	public InMemoryAgendaService() {
		this.persons=new LinkedHashMap<String, Person>();
	}

	@Override
	public Person createPerson(String account, String name, String location, String workplaceHomepage) {
		Person p=new MutablePerson();
		p.setAccount(account);
		p.setName(name);
		p.setLocation(location);
		p.setWorkplaceHomepage(workplaceHomepage);
		this.persons.put(account, p);
		return p;
	}

	@Override
	public Person getPerson(String account) {
		return this.persons.get(account);
	}

	@Override
	public Collection<Person> listPersons() {
		// TODO Auto-generated method stub
		return null;
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

		Person p = persons.get(personId);
		if(p == null){
			throw new RuntimeException(String.format("Invalid person id - '%s'", personId));
		}






		return null;
	}

	@Override
	public Contact getPersonContact(String account, String email) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Contact> listPersonContacts(String account) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean deletePersonContact(String account, String email) {
		// TODO Auto-generated method stub
		return false;
	}

}
