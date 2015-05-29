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

	EXIT("exit",ExitCommandProcessor.class),
	CLEAR("clear",ClearCommandProcessor.class),
	LIST_RESOURCES("list-resources",ListResourcesCommandProcessor.class),
	SHOW_RESOURCE("show-resource",ShowResourceCommandProcessor.class),
	GET("get-resource",GetCommandProcessor.class),
	DELETE("delete-resource",DeleteCommandProcessor.class),
	MODIFY("modify-resource",ModifyCommandProcessor.class),
	CREATE("create-resource",CreateCommandProcessor.class),
	;

	private String name;
	private Class<? extends CommandProcessor> clazz;

	Command(String name,Class<? extends CommandProcessor> clazz) {
		this.name = name;
		this.clazz = clazz;
	}

	static Command fromString(String name) {
		Command targetCmd=null;
		for(Command cmd:values()) {
			if(cmd.name.equalsIgnoreCase(name)) {
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
			throw new IllegalStateException("Could not prepare processor for command '"+this.name+"' ("+this.clazz.getCanonicalName()+")",e);
		}
	}

}