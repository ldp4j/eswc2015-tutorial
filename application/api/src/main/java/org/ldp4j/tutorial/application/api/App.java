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

public interface App {

    /**
     * Adds a new Person to the application. This person can manage the her
     * contacts using the application. If the App changes any of the values from newPerson
     * it will be reflected in the returned person object.
     * @param newPerson a new person to be added to the app
     * @return Person object that reflects the state of the newly created person
     */
    public Person createPerson(Person newPerson);

    /***
     * Returns the person with the given account id.
     * @param account id of the person
     * @return Person with the given id if she exists, null otherwise
     */
    public Person getPerson(String account);


    /***
     * Updates the data about a given person.
     * @param person A person with updated data.
     */
    public void updatePerson(Person person);

    /***
     * Deletes the person with the given account id.
     * @param account id of the person
     * @return true if the person was found and deleted, false otherwise
     */
    public boolean deletePerson(String account);

    /***
     * Returns the current list of persons managed by the application.
     * @return a collection of Persons
     */
    public Collection<Person> listPersons();
}
