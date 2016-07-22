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
 *   Artifact    : org.ldp4j.tutorial.application:application-api:1.0.0
 *   Bundle      : application-api-1.0.0.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.ldp4j.tutorial.application.api;

public interface Contact {

	/**
	 * Property: <a href="http://www.w3.org/TR/vcard-rdf/#d4e891">vcard:fn</a>
	 *
	 * @return the formatted name of the contact.
	 */
	public String getFullName();

	/**
	 * Property:
	 * <a href="http://www.w3.org/TR/vcard-rdf/#d4e605">vcard:hasURL</a>
	 *
	 * @return the url (e.g., homepage) of the contact.
	 */
	public String getUrl();

	/**
	 * Property:
	 * <a href="http://www.w3.org/TR/vcard-rdf/#d4e183">vcard:hasEmail</a>
	 *
	 * @return the email of the contact.
	 */
	public String getEmail();

	/**
	 * Property:
	 * <a href="http://www.w3.org/TR/vcard-rdf/#d4e563">vcard:hasTelephone</a>
	 *
	 * @return the telephone of the contact.
	 */
	public String getTelephone();

	/**
	 * Sets the full name of the contact. Property:
	 * <a href="http://www.w3.org/TR/vcard-rdf/#d4e891">vcard:fn</a>
	 *
	 * @param fullName the new full name for the contact
	 */
	public void setFullName(String fullName);

	/**
	 * Sets the url of the contact. Property:
	 * <a href="http://www.w3.org/TR/vcard-rdf/#d4e605">vcard:hasURL</a>
	 *
	 * @param url the new url for the contact
	 */
	public void setUrl(String url);

	/**
	 * Sets the email of the contact. Property:
	 * <a href="http://www.w3.org/TR/vcard-rdf/#d4e183">vcard:hasEmail</a>
	 *
	 * @param email the new email for the contact
	 */
	public void setEmail(String email);

	/**
	 * Sets the telephone of the contact. Property:
	 * <a href="http://www.w3.org/TR/vcard-rdf/#d4e563">vcard:hasTelephone</a>
	 *
	 * @param telephone the new telephone for the contact
	 */
	public void setTelephone(String telephone);
}
