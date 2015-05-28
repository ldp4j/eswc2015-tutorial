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
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public final class ContactsShell {

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

		@Override
		public boolean hasOptions() {
			return this.helper.getOptions().length>0;
		}

		@Override
		public boolean hasContentType() {
			return this.helper.hasOption("e");
		}

		@Override
		public String contentType() {
			return this.helper.getOptionValue("ct");
		}

		@Override
		public boolean hasTarget() {
			return target()!=null;
		}

		@Override
		public String target() {
			List<String> argList = this.helper.getArgList();
			if(argList!=null && !argList.isEmpty()) {
				return argList.get(0);
			}
			return null;
		}

	}

	private final Options options;
	private final ShellConsole console;
	private final ResourceRepository repository;

	private ContactsShell(ShellConsole console, ResourceRepository repository) {
		this.console = console;
		this.repository = repository;
		this.options =
			new Options().
				addOption("et","entity-tag",    true, "use entity tag").
				addOption("lm","last-modified", true, "use last modified date").
				addOption("ct","content-type",  true, "use content type").
				addOption("e","entity", true, "use entity");
	}

	private CommandContext createCommandContext(String rawCommandLine) throws ParseException {
		String[] commandLineParts=ShellUtil.split(rawCommandLine);

		String command=ShellUtil.extractCommandName(commandLineParts);
		String[] commandArgs=ShellUtil.extractCommandArguments(commandLineParts);

		CommandLineParser parser = new DefaultParser();
		CommandLine commandLine = parser.parse(this.options,commandArgs);

		// debug(command, commandLine);

		return new DefaultCommandContext(command,rawCommandLine,commandLine);
	}

	protected void debug(String command, CommandLine commandLine) {
		this.console.message("- Command: ").metadata(command).message("%n");
		for(Option opt:options.getOptions()) {
			if(commandLine.hasOption(opt.getOpt())) {
				if(!opt.hasArg()) {
					this.console.metadata("  + %s%n",opt.getOpt());
				} else {
					if(!opt.hasArgs()) {
						this.console.metadata("  + %s: ",opt.getOpt()).data("%s%n",commandLine.getOptionValue(opt.getOpt()));
					} else {
						this.console.metadata("  + %s: ",opt.getOpt()).data("%s%n",Arrays.toString(commandLine.getOptionValues(opt.getOpt())));
					}
				}
			}
		}
		List<String> argList = commandLine.getArgList();
		if(!argList.isEmpty()) {
			this.console.metadata("  + Arguments:%n");
			for(String arg:argList) {
				this.console.metadata("    * ").data("%s%n",arg);
			}
		}
	}

	public void execute() {
		boolean continueExecution=true;
		while(continueExecution) {
			this.console.prompt("contacts> ");
			String commandLine = this.console.readLine();
			if(commandLine.trim().isEmpty()) {
				continue;
			}
			try {
				CommandContext context = createCommandContext(commandLine);
				CommandProcessor processor=ShellUtil.createProcessor(context.commandName());
				processor.setConsole(this.console);
				processor.setRepository(this.repository);
				if(processor.canExecute(context)) {
					continueExecution=processor.execute(context);
				}
			} catch (ParseException e) {
				this.console.error("ERROR: Could not process command (%s)%n",e.getMessage());
			}
		}
	}

	public static void main(String... args) {
		ShellConsole console = ShellUtil.console();
		String build=System.getProperty("shell.build","XXX");
		console.title("ESWC 2015 - LDP4j Tutorial - Contacts Application Shell ").message("v%s%n",build);
		ContactsShell shell=new ContactsShell(console,ResourceRepository.create());
		shell.execute();
		console.title("Bye!!!");
	}

}
