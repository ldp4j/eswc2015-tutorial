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
 *   Artifact    : org.ldp4j.tutorial.application:application-api:1.0.0-SNAPSHOT
 *   Bundle      : application-api-1.0.0-SNAPSHOT.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.ldp4j.tutorial.application.api;

import java.util.Collection;

public interface  Person {

    /**
     * Property: <a href="http://xmlns.com/foaf/0.1/account">foaf:account</a>
     * @return the application account id of the person.
     */
    public String getAccount();

    /**
     * Sets the account id of the person;
     * Property: <a href="http://xmlns.com/foaf/0.1/account">foaf:account</a>
     * @param account account id
     */
    public void setAccount(String account);

    /**
     * Property: <a href="http://xmlns.com/foaf/0.1/name">foaf:name</a>
     * @return the name of the person holding the account
     */
    public String getName();

    /**
     * Sets the name of the person
     * Property: <a href="http://xmlns.com/foaf/0.1/name">foaf:name</a>
     * @param name name of the person
     */
    public void setName(String name);

    /**
     * Property: <a href="http://xmlns.com/foaf/0.1/based_near">foaf:based_near</a>
     * @return
     */
    public String getLocation();

    /**
     * Sets the city of the person
     * Property: <a href="http://xmlns.com/foaf/0.1/based_near">foaf:based_near</a>
     * @return
     */
    public void setLocation(String location);

    /**
     * Property: <a href="http://xmlns.com/foaf/0.1/workplaceHomepage">foaf:workplaceHomepage</a>
     * @return
     */
    public String getWorkplacehomepage();

    /**
     * Property: <a href="http://xmlns.com/foaf/0.1/workplaceHomepage">foaf:workplaceHomepage</a>
     * @param workplacehomepage
     */
    public void setWorkplacehomepage(String workplacehomepage);

    /**
     * Adds a contact to this person's contact list
     * @param contact
     */
    public void addContact(Contact contact);

    /**
     * Gets a contact identified by the email address
     * @param email
     * @return
     */
    public Contact getContact(String email);

    /**
     * Update a contact
     * @param contact
     */
    public void updateContact(Contact contact);

    /**
     * Delete a contact from the person's contact list
     * @param email identifier for the contact
     * @return true if the contact was present and deleted, false otherwise
     */
    public boolean deleteContact(String email);

    /**
     * Returns the contacts of this person
     * @return a collection of contacts
     */
    public Collection<Contact> listContacts();


}
