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

import java.io.Console;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class ContactsShell {

	private static final class DefaultCommandContext implements CommandContext {

		private final String commandName;
		private final String commandLine;
		private final CommandLine helper;

		private DefaultCommandContext(String commandName, String commandLine, CommandLine helper) {
			this.commandName = commandName;
			this.commandLine = commandLine;
			this.helper = helper;
		}

		@Override
		public String commandName() {
			return this.commandName;
		}

		@Override
		public String commandLine() {
			return this.commandLine;
		}

		@Override
		public boolean hasEntityTag() {
			return this.helper.hasOption("et");
		}

		@Override
		public String entityTag() {
			return this.helper.getOptionValue("et");
		}

		@Override
		public boolean hasLastModified() {
			return this.helper.hasOption("lm");
		}

		@Override
		public String lastModified() {
			return this.helper.getOptionValue("lm");
		}

		@Override
		public boolean hasEntity() {
			return this.helper.hasOption("e");
		}

		@Override
		public String entity() {
			return this.helper.getOptionValue("e");
		}

	}

	private final Options options;
	private final Console console;

	private ContactsShell(Console console) {
		this.console = console;
		this.options =
			new Options().
				addOption("et","entity-tag",    true, "use entity tag").
				addOption("lm","last-modified", true, "use last modified date").
				addOption("e","entity", true, "use entity");
	}

	private CommandContext createCommandContext(String rawCommandLine) throws ParseException {
		String[] commandLineParts=ShellUtil.split(rawCommandLine);

		String command=ShellUtil.extractCommandName(commandLineParts);
		String[] commandArgs=ShellUtil.extractCommandArguments(commandLineParts);

		CommandLineParser parser = new DefaultParser();
		CommandLine commandLine = parser.parse( options,commandArgs);

		debug(command, commandLine);

		return new DefaultCommandContext(command, rawCommandLine, commandLine);
	}

	private void debug(String command, CommandLine commandLine) {
		this.console.format("- Command: %s%n",command);
		for(Option opt:options.getOptions()) {
			if(commandLine.hasOption(opt.getOpt())) {
				if(!opt.hasArg()) {
					this.console.format("  + %s%n",opt.getOpt());
				} else {
					if(!opt.hasArgs()) {
						this.console.format("  + %s: %s%n",opt.getOpt(),commandLine.getOptionValue(opt.getOpt()));
					} else {
						this.console.format("  + %s: %s%n",opt.getOpt(),Arrays.toString(commandLine.getOptionValues(opt.getOpt())));
					}
				}
			}
		}
		List<String> argList = commandLine.getArgList();
		if(!argList.isEmpty()) {
			this.console.format("  + Arguments:%n");
			for(String arg:argList) {
				this.console.format("    * %s:%n",arg);
			}
		}
	}

	public void execute() {
		boolean continueExecution=true;
		while(continueExecution) {
			this.console.format("contacts> ");
			String commandLine = console.readLine();
			try {
				CommandContext context = createCommandContext(commandLine);
				CommandProcessor processor=ShellUtil.createProcessor(context.commandName());
				if(processor.canExecute(console,context)) {
					continueExecution=processor.execute(console, context);
				}
			} catch (ParseException e) {
				this.console.format("ERROR: Could not process command (%s)%n",e.getMessage());
			}
		}
	}

	public static void main(String... args) {
		ContactsShell shell=new ContactsShell(System.console());
		shell.execute();
	}

}
