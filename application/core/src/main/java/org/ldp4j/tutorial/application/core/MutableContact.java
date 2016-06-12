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

public class MutableContact implements Contact {

    private String fullName;
    private String url;
    private String telephone;
    private String email;

    /**
     * Property: <a href="http://www.w3.org/TR/vcard-rdf/#d4e891">vcard:fn</a>
     *
     * @return the formatted name of the contact.
     */
    @Override
    public String getFullName() {
        return fullName;
    }

    /**
     * Property: <a href="http://www.w3.org/TR/vcard-rdf/#d4e605">vcard:hasURL</a>
     *
     * @return the url (e.g., homepage) of the contact.
     */
    @Override
    public String getUrl() {
        return url;
    }

    /**
     * Property: <a href="http://www.w3.org/TR/vcard-rdf/#d4e183">vcard:hasEmail</a>
     *
     * @return the email of the contact.
     */
    @Override
    public String getEmail() {
        return email;
    }

    /**
     * Property: <a href="http://www.w3.org/TR/vcard-rdf/#d4e563">vcard:hasTelephone</a>
     *
     * @return the telephone of the contact.
     */
    @Override
    public String getTelephone() {
        return telephone;
    }

    /**
     * Sets the full name of the contact.
     * Property: <a href="http://www.w3.org/TR/vcard-rdf/#d4e891">vcard:fn</a>
     *
     * @param fullName
     */
    @Override
    public void setFullName(String fullName) {
        this.fullName =fullName;
    }

    /**
     * Sets the url of the contact.
     * Property: <a href="http://www.w3.org/TR/vcard-rdf/#d4e605">vcard:hasURL</a>
     *
     * @param url
     */
    @Override
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Sets the email of the contact.
     * Property: <a href="http://www.w3.org/TR/vcard-rdf/#d4e183">vcard:hasEmail</a>
     *
     * @param email
     */
    @Override
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Sets the telephone of the contact.
     * Property: <a href="http://www.w3.org/TR/vcard-rdf/#d4e563">vcard:hasTelephone</a>
     *
     * @param telephone
     */
    @Override
    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
}
