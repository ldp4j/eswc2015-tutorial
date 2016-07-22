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
 *   Artifact    : org.ldp4j.tutorial.frontend:frontend-core:1.0.0
 *   Bundle      : frontend-core-1.0.0.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.ldp4j.tutorial.frontend.util;

import java.net.URI;
import java.util.Set;

import org.ldp4j.application.data.DataSetUtils;
import org.ldp4j.application.data.ExternalIndividual;
import org.ldp4j.application.data.Individual;
import org.ldp4j.application.data.IndividualHelper;

import com.google.common.base.Optional;

public final class Mapper {

	private final IndividualHelper helper;

	private Mapper(IndividualHelper helper) {
		this.helper = helper;
	}

	private Mapper(Individual<?,?> individual) {
		this(DataSetUtils.newHelper(individual));
	}

	public Set<URI> types() {
		return this.helper.types();
	}

	public <T> T literal(String propertyURI, Class<? extends T> aClazz) {
		return
			this.helper.
				property(propertyURI).
					firstValue(aClazz);
	}

	public Optional<URI> individual(String propertyURI) {
		return
			Optional.
				fromNullable(
					this.helper.
						property(propertyURI).
							firstIndividual(ExternalIndividual.class));
	}

	public Mapper individualMapper(String propertyURI) {
		return new Mapper(this.helper.property(propertyURI).firstIndividual());
	}

	public static String toStringOrNull(Optional<URI> individual) {
		return individual.isPresent()?individual.get().toString():null;
	}

	public static Mapper create(Individual<?,?> individual) {
		return new Mapper(individual);
	}

}