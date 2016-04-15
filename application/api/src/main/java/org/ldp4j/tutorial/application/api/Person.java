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


public interface  Person {

    /**
     * Property: <a href="http://xmlns.com/foaf/0.1/mbox">foaf:mbox</a>
     * @return the email of the person.
     */
    public String getEmail();

    /**
     * Sets the email of the person;
     * Property: <a href="http://xmlns.com/foaf/0.1/mbox">foaf:mbox</a>
     * @param email email of the person
     */
    public void setEmail(String email);

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
     */
    public void setLocation(String location);

    /**
     * Property: <a href="http://xmlns.com/foaf/0.1/workplaceHomepage">foaf:workplaceHomepage</a>
     * @return
     */
    public String getWorkplaceHomepage();

    /**
     * Property: <a href="http://xmlns.com/foaf/0.1/workplaceHomepage">foaf:workplaceHomepage</a>
     * @param workplacehomepage
     */
    public void setWorkplaceHomepage(String workplacehomepage);

}
