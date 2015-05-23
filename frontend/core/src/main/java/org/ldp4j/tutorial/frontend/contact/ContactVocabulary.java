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
package org.ldp4j.tutorial.frontend.contact;

import org.ldp4j.application.domain.RDF;

interface ContactVocabulary {

	static final String TYPE       = RDF.TYPE.qualifiedEntityName();

	static final String INDIVIDUAL = "http://www.w3.org/2006/vcard/ns#Individual";
	static final String HOME       = "http://www.w3.org/2006/vcard/ns#Home";
	static final String VOICE      = "http://www.w3.org/2006/vcard/ns#Voice";

	static final String URL        = "http://www.w3.org/2006/vcard/ns#hasURL";
	static final String EMAIL      = "http://www.w3.org/2006/vcard/ns#hasEmail";
	static final String FULL_NAME  = "http://www.w3.org/2006/vcard/ns#fn";
	static final String TELEPHONE  = "http://www.w3.org/2006/vcard/ns#hasTelephone";
	static final String NUMBER     = "http://www.w3.org/2006/vcard/ns#hasValue";


}
