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

enum Command {

	EXIT("exit","Terminate the client shell",ExitCommandProcessor.class),
	CLEAR("clear","Clear the console",ClearCommandProcessor.class),
	HELP("help","Show the client shell help",HelpCommandProcessor.class),
	LIST_REPRESENTATIONS("list-cached-representations","Show the list of cached representations",ListCachedRepresentationsCommandProcessor.class),
	SHOW_REPRESENTATION("show-cached-representation","Show the cached representation of a given resource",ShowCachedRepresentationCommandProcessor.class),
	RETRIEVE("retrieve-resource","Retrieve a LDP resource",RetrieveCommandProcessor.class),
	DELETE("delete-resource","Delete a LDP resource",DeleteCommandProcessor.class),
	MODIFY("modify-resource","Modify a LDP resource",ModifyCommandProcessor.class),
	CREATE("create-resource","Create a LDP resource",CreateCommandProcessor.class),
	;

	private String commandName;
	private String commandDescription;
	private Class<? extends CommandProcessor> clazz;

	Command(String name, String description, Class<? extends CommandProcessor> clazz) {
		this.commandName = name;
		this.commandDescription = description;
		this.clazz = clazz;
	}

	static Command fromString(String name) {
		Command targetCmd=null;
		for(Command cmd:values()) {
			if(cmd.commandName.equalsIgnoreCase(name)) {
				targetCmd=cmd;
				break;
			}
		}
		return targetCmd;
	}

	CommandProcessor createProcessor() {
		try {
			return clazz.newInstance();
		} catch (Exception e) {
			throw new IllegalStateException("Could not prepare processor for command '"+this.commandName+"' ("+this.clazz.getCanonicalName()+")",e);
		}
	}

	public String commandDescription() {
		return this.commandDescription;
	}

	public String commandName() {
		return this.commandName;
	}

}