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

import java.util.Arrays;

class ShellUtil {

	private static final String[] EMPTY_ARGS = new String[]{};

	private ShellUtil() {
	}

	static String[] split(String rawCommandLine) {
		return rawCommandLine.split("\\s");
	}

	static String extractCommandName(String[] commandLineParts) {
		return commandLineParts[0];
	}

	static String[] extractCommandArguments(String[] commandLineParts) {
		String[] commandArgs=ShellUtil.EMPTY_ARGS;
		if(commandLineParts.length>1) {
			commandArgs=Arrays.copyOfRange(commandLineParts,1,commandLineParts.length);
		}
		return commandArgs;
	}

	static CommandProcessor createProcessor(String name) {
		Command targetCmd = Command.fromString(name);
		CommandProcessor result=new NullCommandProcessor();
		if(targetCmd!=null) {
			result=targetCmd.createProcessor();
		}
		return result;
	}

}