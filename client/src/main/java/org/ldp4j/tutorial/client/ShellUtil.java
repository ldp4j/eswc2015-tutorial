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
import java.io.PrintStream;
import java.util.Arrays;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.Ansi.Color;
import org.fusesource.jansi.AnsiConsole;

class ShellUtil {

	private static final String[] EMPTY_ARGS = new String[]{};

	private static final class NativeShellConsole implements ShellConsole {

		private final Console console;

		private NativeShellConsole(Console console) {
			this.console = console;
		}

		@Override
		public ShellConsole data(String fmt, Object... args) {
			this.console.format(fmt,args);
			return this;
		}

		@Override
		public ShellConsole metadata(String fmt, Object... args) {
			return data(fmt,args);
		}

		@Override
		public String readLine() {
			return this.console.readLine();
		}

		@Override
		public ShellConsole message(String fmt, Object... args) {
			return data(fmt,args);
		}

		@Override
		public ShellConsole title(String fmt, Object... args) {
			return data(fmt,args);
		}

		@Override
		public ShellConsole prompt(String fmt, Object... args) {
			return data(fmt,args);
		}

		@Override
		public ShellConsole error(String fmt, Object... args) {
			return data(fmt,args);
		}

		@Override
		public boolean isClearable() {
			return false;
		}

		@Override
		public ShellConsole clear() {
			throw new UnsupportedOperationException("Console is not clearable");
		}

		@Override
		public boolean isReadable() {
			return true;
		}

	}

	private static final class AnsiShellConsole implements ShellConsole {

		private final PrintStream output;
		private ShellConsole console;

		private AnsiShellConsole(PrintStream out, ShellConsole console) {
			this.output = out;
			this.console = console;
		}

		@Override
		public ShellConsole data(String fmt, Object... args) {
			Ansi ansi =
				Ansi.
					ansi().
						bg(Color.DEFAULT).
						fg(Color.DEFAULT).
						a(String.format(fmt,args)).
						reset();
			this.output.print(ansi);
			return this;
		}

		@Override
		public ShellConsole metadata(String fmt, Object... args) {
			Ansi ansi =
				Ansi.
					ansi().
						bg(Color.DEFAULT).
						fg(Color.YELLOW).
						a(String.format(fmt,args)).
						reset();
			this.output.print(ansi);
			return this;
		}

		@Override
		public ShellConsole message(String fmt, Object... args) {
			Ansi ansi =
				Ansi.
					ansi().
						bg(Color.DEFAULT).
						fg(Color.GREEN).
						a(String.format(fmt,args)).
						reset();
			this.output.print(ansi);
			return this;
		}

		@Override
		public String readLine() {
			return this.console.readLine();
		}

		@Override
		public ShellConsole title(String fmt, Object... args) {
			Ansi ansi =
				Ansi.
					ansi().
						bold().
						bg(Color.DEFAULT).
						fg(Color.RED).
						a(String.format(fmt,args)).
						reset();
			this.output.print(ansi);
			return this;
		}

		@Override
		public ShellConsole prompt(String fmt, Object... args) {
			Ansi ansi =
				Ansi.
					ansi().
						bold().
						bg(Color.DEFAULT).
						fg(Color.CYAN).
						a(String.format(fmt,args)).
						reset();
			this.output.print(ansi);
			return this;
		}

		@Override
		public ShellConsole error(String fmt, Object... args) {
			Ansi ansi =
				Ansi.
					ansi().
						bg(Color.DEFAULT).
						fg(Color.RED).
						a(String.format(fmt,args)).
						reset();
			this.output.print(ansi);
			return this;
		}

		@Override
		public boolean isClearable() {
			return true;
		}

		@Override
		public ShellConsole clear() {
			Ansi ansi=
				Ansi.
					ansi().
						eraseScreen().
						cursor(1,1);
			this.output.print(ansi);
			return this;
		}

		@Override
		public boolean isReadable() {
			return this.console.isReadable();
		}

	}

	private ShellUtil() {
	}

	static ShellConsole console() {
		String property = System.getProperty("shell.console","native");
		ShellConsole console = new NativeShellConsole(System.console());

		if(property.equalsIgnoreCase("ansi")) {
			console=new AnsiShellConsole(AnsiConsole.out(), console);
		}
		return console;
	}

	static String extractCommandName(String[] commandLineParts) {
		return commandLineParts[0];
	}

	static String[] split(String rawCommandLine) {
		return rawCommandLine.split("\\s");
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