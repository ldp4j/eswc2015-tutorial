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
import java.util.Scanner;
import java.util.SortedSet;
import java.util.concurrent.atomic.AtomicInteger;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.Ansi.Color;
import org.fusesource.jansi.AnsiConsole;

class ShellUtil {

	private static final String[] EMPTY_ARGS = new String[]{};

	private static final class DefaultShellConsole implements
			ShellConsole {
		private final Scanner scanner=new Scanner(System.in);

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

		@Override
		public String readLine() {
			return this.scanner.nextLine();
		}

		@Override
		public ShellConsole data(String fmt, Object... args) {
			try {
				System.out.printf(fmt,args);
			} catch (Exception e) {
				showFailure(e, fmt, args);
			}
			return this;
		}

		@Override
		public ShellConsole metadata(String fmt, Object... args) {
			return data(fmt,args);
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
	}

	private static final class NativeShellConsole implements ShellConsole {

		private final Console console;

		private NativeShellConsole(Console console) {
			this.console = console;
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

		@Override
		public String readLine() {
			return this.console.readLine();
		}

		@Override
		public ShellConsole data(String fmt, Object... args) {
			try {
				this.console.format(fmt,args);
			} catch (Exception e) {
				showFailure(e, fmt, args);
			}
			return this;
		}

		@Override
		public ShellConsole metadata(String fmt, Object... args) {
			return data(fmt,args);
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
			colorize(Color.DEFAULT, Color.DEFAULT, fmt, args);
			return this;
		}

		private void colorize(Color bg, Color fg, String fmt, Object... args) {
			try {
				Ansi ansi =
					Ansi.
						ansi().
							bg(bg).
							fg(fg).
							a(String.format(fmt,args)).
							reset();
				this.output.print(ansi);
			} catch (Exception e) {
				showFailure(e, fmt, args);
			}
		}

		@Override
		public ShellConsole metadata(String fmt, Object... args) {
			colorize(Color.DEFAULT, Color.YELLOW, fmt, args);
			return this;
		}

		@Override
		public ShellConsole message(String fmt, Object... args) {
			colorize(Color.DEFAULT, Color.GREEN, fmt, args);
			return this;
		}

		@Override
		public String readLine() {
			return this.console.readLine();
		}

		@Override
		public ShellConsole title(String fmt, Object... args) {
			try {
				Ansi ansi =
					Ansi.
						ansi().
							bold().
							bg(Color.DEFAULT).
							fg(Color.RED).
							a(String.format(fmt,args)).
							reset();
				this.output.print(ansi);
			} catch (Exception e) {
				showFailure(e, fmt, args);
			}
			return this;
		}

		@Override
		public ShellConsole prompt(String fmt, Object... args) {
			colorize(Color.DEFAULT, Color.CYAN, fmt, args);
			return this;
		}

		@Override
		public ShellConsole error(String fmt, Object... args) {
			colorize(Color.DEFAULT, Color.RED, fmt, args);
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

	private static void showFailure(Exception failure, String fmt, Object... args) {
		System.err.printf("[INTERNAL ERROR] Could not print message: %s%n- Offending message:%n", failure.getMessage());
		System.err.println("  + Format: "+fmt);
		if(args.length>0) {
			System.err.println("  + Arguments: ");
			for(Object arg:args) {
				StringBuilder builder=
					new StringBuilder().
						append("    * ").
						append(arg.getClass().getCanonicalName()).
						append(" : ").
						append(arg);
				System.err.println(builder);
			}
		}
	}

	private static void showField(ShellConsole console, String name, String value) {
		if(value!=null) {
			console.metadata("- %s : ",name).data("%s%n",value);
		}
	}

	static ShellConsole console() {
		String property = System.getProperty("shell.console","native");
		ShellConsole console=new DefaultShellConsole();
		Console nativeConsole = System.console();
		if(nativeConsole!=null) {
			console = new NativeShellConsole(nativeConsole);
			if(property.equalsIgnoreCase("ansi")) {
				console=new AnsiShellConsole(AnsiConsole.out(), console);
			}
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

	static void showResourceContent(ShellConsole console, Resource resource) {
		String entity = resource.entity();
		if(entity!=null) {
			console.metadata("- Content:%n").data(entity).data("%n");
		}
	}

	static void showResourceMetadata(ShellConsole console, Resource resource) {
		showField(console, "Content Type ", resource.contentType());
		showField(console, "Entity Tag   ", resource.entityTag());
		showField(console, "Last Modified", resource.lastModified());
	}

	static void showLinks(ShellConsole console, Links links) {
		SortedSet<Link> all = links.all();
		if(!all.isEmpty()) {
			console.metadata("- Links: %n");
			for(Link link:all) {
				console.
					metadata("  + %s : ",link.relation()).data("%s%n",link.value());
			}
		}
	}

	private static AtomicInteger counter=new AtomicInteger();

	static String nextResourceFile() {
		return String.format("resource.%04X.%X.dat",counter.incrementAndGet(),System.currentTimeMillis());
	}

}