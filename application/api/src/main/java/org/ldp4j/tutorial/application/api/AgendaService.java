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
 *   Artifact    : org.ldp4j.tutorial.application:application-api:1.0.0-SNAPSHOT
 *   Bundle      : application-api-1.0.0-SNAPSHOT.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.ldp4j.tutorial.application.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.ReflectPermission;
import java.util.Collection;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AgendaService {

	private static final String INSTANTIATE_ACTION = "instantiate";

	private static final Logger LOGGER=LoggerFactory.getLogger(AgendaService.class);

	public static final String APP_MANAGER_SPI_RUNTIMEINSTANCE_FINDER = "org.ldp4j.tutorial.application.api.AppManager.finder";

	/**
	 * Name of the configuration file where the
	 * {@link AgendaService#APP_MANAGER_SPI_PROPERTY} property that
	 * identifies the {@link AgendaService} implementation to be returned from
	 * {@link AgendaService#getInstance()} can be defined.
	 */
	public static final String APP_MANAGER_SPI_CFG = "ldp4j-application.properties";

	/**
	 * Name of the property identifying the {@link AgendaService} implementation
	 * to be returned from {@link AgendaService#getInstance()}.
	 */
	public static final String APP_MANAGER_SPI_PROPERTY = "org.ldp4j.tutorial.application.api.AppManager";

	private static final AtomicReference<AgendaService> CACHED_DELEGATE=new AtomicReference<AgendaService>();

	private static ReflectPermission suppressAccessChecksPermission = new ReflectPermission("suppressAccessChecks");

	/**
	 * Allows custom implementations to extend the {@code RuntimeInstance} class.
	 */
	protected AgendaService() {
	}

	/**
	 * Obtain a {@code RuntimeInstance} instance using the method described in
	 * {@link #getInstance}.
	 *
	 * @return an instance of {@code RuntimeInstance}.
	 */
	private static AgendaService findDelegate() {
		try {
			AgendaService result=createRuntimeInstanceFromSPI();
			if(result==null) {
				result=createRuntimeInstanceFromConfigurationFile();
			}

			if(result==null) {
				String delegateClassName = System.getProperty(APP_MANAGER_SPI_PROPERTY);
				if(delegateClassName!=null) {
					result=createRuntimeInstanceForClassName(delegateClassName);
				}
			}

			if(result==null) {
				result=new DefaultAgendaService();
			}

			return result;
		} catch (Exception ex) {
			throw new IllegalStateException("Could not find runtime delegate",ex);
		}
	}

	private static AgendaService createRuntimeInstanceFromConfigurationFile() {
		AgendaService result=null;
		File configFile = getConfigurationFile();
		if(configFile.canRead()) {
			InputStream is=null;
			try {
				is=new FileInputStream(configFile);
				Properties configProperties=new Properties();
				configProperties.load(is);
				String delegateClassName=configProperties.getProperty(APP_MANAGER_SPI_PROPERTY);
				if(delegateClassName!=null) {
					result=createRuntimeInstanceForClassName(delegateClassName);
				}
				if(delegateClassName==null && LOGGER.isWarnEnabled()) {
					LOGGER.warn("Configuration file '"+configFile.getAbsolutePath()+"' does not define a delegate class name");
				}
			} catch(FileNotFoundException e) {
				if(LOGGER.isDebugEnabled()) {
					LOGGER.debug("Could not find runtime instance configuration file '"+configFile.getAbsolutePath()+"'",e);
				}
			} catch(IOException e) {
				if(LOGGER.isWarnEnabled()) {
					LOGGER.warn("Could not load runtime instance configuration file '"+configFile.getAbsolutePath()+"'",e);
				}
			} finally {
				closeQuietly(is, "Could not close configuration properties");
			}
		}
		return result;
	}

	/**
	 * Get the configuration file for the Runtime Instance: a file named
	 * {@link AgendaService#APP_MANAGER_SPI_CFG} in the <code>lib</code> directory of
	 * current JAVA_HOME.
	 *
	 * @return The configuration file for the runtime instance.
	 */
	private static File getConfigurationFile() {
		return new File(new File(System.getProperty("java.home")),"lib"+File.separator+APP_MANAGER_SPI_CFG);
	}

	/**
	 * Close an input stream logging possible failures.
	 * @param is The input stream that is to be closed.
	 * @param message The message to log in case of failure.
	 */
	private static void closeQuietly(InputStream is, String message) {
		if(is!=null) {
		try {
			is.close();
		} catch (Exception e) {
			if(LOGGER.isWarnEnabled()) {
				LOGGER.warn(message,e);
			}
		}
		}
	}

	private static AgendaService createRuntimeInstanceFromSPI() {
		if(!"disable".equalsIgnoreCase(System.getProperty(APP_MANAGER_SPI_RUNTIMEINSTANCE_FINDER))) {
			for (AgendaService delegate : ServiceLoader.load(AgendaService.class)) {
				return delegate;
			}
		}
		return null;
	}

	private static AgendaService createRuntimeInstanceForClassName(String delegateClassName) {
		AgendaService result = null;
		try {
			Class<?> delegateClass = Class.forName(delegateClassName);
			if(AgendaService.class.isAssignableFrom(delegateClass)) {
				Object impl = delegateClass.newInstance();
				result = AgendaService.class.cast(impl);
			}
		} catch (ClassNotFoundException e) {
			handleFailure(delegateClassName, "find", e);
		} catch (InstantiationException e) {
			handleFailure(delegateClassName, INSTANTIATE_ACTION, e);
		} catch (IllegalAccessException e) {
			handleFailure(delegateClassName, INSTANTIATE_ACTION, e);
		}
		return result;
	}

	/**
	 * @param delegateClassName
	 * @param action
	 * @param failure
	 */
	private static void handleFailure(String delegateClassName, String action, Exception failure) {
		if(LOGGER.isWarnEnabled()) {
			LOGGER.warn("Could not "+action+" delegate class "+delegateClassName,failure);
		}
	}

	/**
	 * Obtain a {@code RuntimeInstance} instance. If an instance had not already
	 * been created and set via {@link #setInstance(AgendaService)}, the first
	 * invocation will create an instance which will then be cached for future
	 * use.
	 *
	 * <p>
	 * The algorithm used to locate the RuntimeInstance subclass to use consists
	 * of the following steps:
	 * </p>
	 * <ul>
	 * <li>
	 * If a resource with the name of
	 * {@code META-INF/services/org.centeropenmiddleware.almistack.poc.clients.spi.RuntimeInstance} exists, then
	 * its first line, if present, is used as the UTF-8 encoded name of the
	 * implementation class.</li>
	 * <li>
	 * If the $java.home/lib/poc-business-logic.properties file exists and it is readable by
	 * the {@code java.util.Properties.load(InputStream)} method and it contains
	 * an entry whose key is {@code org.centeropenmiddleware.almistack.poc.clients.spi.RuntimeInstance}, then the
	 * value of that entry is used as the name of the implementation class.</li>
	 * <li>
	 * If a system property with the name
	 * {@code org.centeropenmiddleware.almistack.poc.clients.spi.RuntimeInstance} is defined, then its value is
	 * used as the name of the implementation class.</li>
	 * <li>
	 * Finally, a default implementation class name is used.</li>
	 * </ul>
	 *
	 * @return an instance of {@code RuntimeInstance}.
	 */
	public static AgendaService getInstance() {
		AgendaService result = AgendaService.CACHED_DELEGATE.get();
		if (result != null) {
			return result;
		}
		synchronized(AgendaService.CACHED_DELEGATE) {
			result=AgendaService.CACHED_DELEGATE.get();
			if(result==null) {
				AgendaService delegate = findDelegate();
				AgendaService.CACHED_DELEGATE.set(delegate);
				result=AgendaService.CACHED_DELEGATE.get();
			}
			return result;
		}
	}

	/**
	 * Set the runtime delegate that will be used by Client Business Logic API
	 * classes. If this method is not called prior to {@link #getInstance} then
	 * an implementation will be sought as described in {@link #getInstance}.
	 *
	 * @param delegate
	 *            the {@code RuntimeInstance} runtime delegate instance.
	 * @throws SecurityException
	 *             if there is a security manager and the permission
	 *             ReflectPermission("suppressAccessChecks") has not been
	 *             granted.
	 */
	public static void setInstance(final AgendaService delegate) {
		SecurityManager security = System.getSecurityManager();
		if (security != null) {
			security.checkPermission(suppressAccessChecksPermission);
		}
		AgendaService.CACHED_DELEGATE.set(delegate);
	}

	private static class DefaultAgendaService extends AgendaService {

		private static final String ERROR_MESSAGE = String.format("No implementation for class '%s' could be found",AgendaService.class);

		@Override
		public Person createPerson(String account, String name,
				String location, String workplaceHomepage) {
			throw new AssertionError(ERROR_MESSAGE);
		}


		@Override
		public Contact addContactToPerson(String personId, String fullName, String url,
				String email, String telephone) {
			throw new AssertionError(ERROR_MESSAGE);
		}


		@Override
		public Person getPerson(String account) {
			throw new AssertionError(ERROR_MESSAGE);
		}


		@Override
		public Collection<Person> listPersons() {
			throw new AssertionError(ERROR_MESSAGE);
		}

		@Override
		public boolean deletePerson(String account) {
			throw new AssertionError(ERROR_MESSAGE);
		}

		@Override
		public Contact getPersonContact(String account, String email) {
			throw new AssertionError(ERROR_MESSAGE);
		}

		@Override
		public Collection<Contact> listPersonContacts(String account) {
			throw new AssertionError(ERROR_MESSAGE);
		}

		@Override
		public boolean deletePersonContact(String account, String email) {
			throw new AssertionError(ERROR_MESSAGE);
		}

	}

    /**
     * Adds a new Person to the application. This person can manage the her
     * contacts using the application. If the App changes any of the values from newPerson
     * it will be reflected in the returned person object.
     * @param newPerson a new person to be added to the app
     * @return Person object that reflects the state of the newly created person
     */
    public abstract Person createPerson(String account, String name, String location, String workplaceHomepage);

    /***
	 * Returns the person with the given account id.
	 * @param account id of the person
	 * @return Person with the given id if she exists, null otherwise
	 */
	public abstract Person getPerson(String account);

	/***
	 * Returns the current list of persons managed by the application.
	 * @return a collection of Persons
	 */
	public abstract Collection<Person> listPersons();

	/***
	 * Deletes the person with the given account id.
	 * @param account id of the person
	 * @return true if the person was found and deleted, false otherwise
	 */
	public abstract boolean deletePerson(String account);

	/**
     * Adds a contact to this person's contact list
     * @param contact
     */
    public abstract Contact addContactToPerson(String personId, String fullName, String url, String email, String telephone);


    /**
	 * Gets a contact identified by the email address
	 * @param email
	 * @return
	 */
	public abstract Contact getPersonContact(String account, String email);

	/**
	 * Returns the contacts of this person
	 * @return a collection of contacts
	 */
	public abstract Collection<Contact> listPersonContacts(String account);

	/**
     * Delete a contact from the person's contact list
     * @param email identifier for the contact
     * @return true if the contact was present and deleted, false otherwise
     */
    public abstract boolean deletePersonContact(String account, String email);

}
