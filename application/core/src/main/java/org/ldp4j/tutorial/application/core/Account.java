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
 *   Artifact    : org.ldp4j.tutorial.application:application-core:1.0.0-SNAPSHOT
 *   Bundle      : application-core-1.0.0-SNAPSHOT.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.ldp4j.tutorial.application.core;

import org.ldp4j.tutorial.application.api.Contact;
import org.ldp4j.tutorial.application.api.Person;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class Account {

    private Person person;

    private Map<String, Contact> contacts = new LinkedHashMap<String, Contact>();

    public Account(Person person){
        this.person = person;
    }

    public Person getPerson() {
        return person;
    }

    public void addContact (Contact contact){
        contacts.put(contact.getEmail(), contact);
    }

    public Contact getContact(String email) {
        return contacts.get(email);
    }

    public boolean deleteContact(String email) {
        Contact contact =contacts.remove(email);
        if(contact!= null){
            return true;
        } else {
            return false;
        }
    }

    public Collection<Contact> listContacts(){
        return contacts.values();
    }
}
