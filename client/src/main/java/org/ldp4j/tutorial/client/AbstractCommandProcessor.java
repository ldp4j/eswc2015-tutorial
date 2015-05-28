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
 *   Artifact    : org.ldp4j.tutorial.client:eswc-2015-client:1.0.0-SNAPSHOT
 *   Bundle      : eswc-2015-client-1.0.0-SNAPSHOT.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.ldp4j.tutorial.client;

import java.net.URI;
import java.net.URISyntaxException;

public abstract class AbstractCommandProcessor implements CommandProcessor {


	private static final class NullShellConsole implements ShellConsole {

		@Override
		public String readLine() {
			throw new UnsupportedOperationException("Console is not readable");
		}

		@Override
		public ShellConsole title(String fmt, Object... args) {
			return this;
		}

		@Override
		public ShellConsole prompt(String fmt, Object... args) {
			return this;
		}

		@Override
		public ShellConsole error(String fmt, Object... args) {
			return this;
		}

		@Override
		public ShellConsole message(String fmt, Object... args) {
			return this;
		}

		@Override
		public ShellConsole metadata(String fmt, Object... args) {
			return this;
		}

		@Override
		public ShellConsole data(String fmt, Object... args) {
			return this;
		}

		@Override
		public boolean isClearable() {
			return false;
		}

		@Override
		public ShellConsole clear() {
			throw new UnsupportedOperationException("Console cannot be cleared");
		}

		@Override
		public boolean isReadable() {
			return false;
		}
	}

	private ShellConsole console;
	private ResourceRepository repository;

	protected AbstractCommandProcessor() {
	}

	protected final ShellConsole console() {
		ShellConsole result=this.console;
		if(result==null) {
			result=new NullShellConsole();
		}
		return result;
	}

	protected final ResourceRepository repository() {
		return this.repository;
	}

	@Override
	public final void setConsole(ShellConsole console) {
		this.console = console;
	}

	@Override
	public final void setRepository(ResourceRepository repository) {
		this.repository = repository;
	}

	protected final String requireTargetResource(CommandContext context) {
		if(!context.hasTarget()) {
			throw new CommandRequirementException("No target specified");
		}
		try {
			URI uri = new URI(context.target());
			if(!(uri.isAbsolute() && uri.getScheme().equalsIgnoreCase("http"))) {
				throw new CommandRequirementException("Invalid target resource (not an absolute HTTP url)");
			}
			return context.target();
		} catch (URISyntaxException e) {
			throw new CommandRequirementException("Invalid target resource ("+e.getMessage()+")",e);
		}
	}

	protected final String requireEntityTag(CommandContext context) {
		String entityTag=entityTag(context);
		if(entityTag==null) {
			throw new CommandRequirementException("ERROR: No entity tag available");
		}
		return entityTag;
	}

	protected final String entityTag(CommandContext options) {
		String rawEntityTag=options.entityTag();
		if(rawEntityTag==null) {
			Resource resource = repository().resolveResource(options.target());
			if(resource!=null) {
				rawEntityTag=resource.entityTag();
			}
		}
		return rawEntityTag;
	}
}
