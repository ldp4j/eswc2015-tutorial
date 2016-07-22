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
 *   Artifact    : org.ldp4j.tutorial.client:eswc-2015-client:1.0.0
 *   Bundle      : eswc-2015-client-1.0.0.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.ldp4j.tutorial.client;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

final class CachedRepresentationManager {

	private final class ContentEntry {

		private String resource;
		private File file;

		String resource() {
			return this.resource;
		}

		File file() {
			return this.file;
		}

		ContentEntry withResource(String resource) {
			this.resource=resource;
			return this;
		}

		ContentEntry withFile(File file) {
			this.file=file;
			return this;
		}

	}

	private static Logger LOGGER=LoggerFactory.getLogger(CachedRepresentationManager.class);

	private final Map<String,ContentEntry> loadedResources;
	private File cacheDirectory;

	private boolean created;

	private CachedRepresentationManager(File cacheDirectory) {
		this.cacheDirectory=cacheDirectory;
		this.loadedResources=Maps.newLinkedHashMap();
	}

	private void init() {
		if(!this.cacheDirectory.exists()) {
			this.created=this.cacheDirectory.mkdirs();
		}

		if(!this.created && !this.cacheDirectory.isDirectory()) {
			LOGGER.debug("Path {} cannot be used as cache directory. Resorting to default temp directory {}",this.cacheDirectory.getAbsolutePath(),FileUtils.getTempDirectoryPath());
			this.cacheDirectory=FileUtils.getTempDirectory();
		}

		if(this.created){
			LOGGER.debug("Created cache directory {}",this.cacheDirectory.getAbsolutePath());
		}
	}

	private ContentEntry getOrCreateEntry(String resource) {
		ContentEntry entry = this.loadedResources.get(resource);
		if(entry==null) {
			entry=
				new ContentEntry().
					withResource(resource).
					withFile(createFile(resource));
			this.loadedResources.put(resource, entry);
		}
		return entry;
	}

	private File createFile(String resource) {
		URI uri = URI.create(resource);
		StringBuilder builder=new StringBuilder();
		builder.append(uri.getScheme()).append("_");
		String userInfo = uri.getUserInfo();
		if(userInfo!=null) {
			builder.append(userInfo).append("@");
		}
		builder.append(uri.getHost());
		if(uri.getPort()>=0) {
			builder.append("_").append(uri.getPort());
		}
		if(uri.getPath()!=null) {
			builder.append(uri.getRawPath().replace("/","_"));
		}
		if(uri.getQuery()!=null) {
			builder.append("?").append(uri.getRawQuery());
		}
		if(uri.getFragment()!=null) {
			builder.append("#").append(uri.getRawFragment());
		}
		builder.append(".dat");
		File file = new File(this.cacheDirectory,builder.toString());
		return file;
	}

	void persist(String resource, String contents) throws IOException {
		ContentEntry entry = getOrCreateEntry(resource);
		LOGGER.debug("Persisting resource {} contents to {}...",entry.resource(),entry.file());
		FileUtils.write(entry.file(),contents);
	}

	File file(String resource) {
		File result=null;
		ContentEntry entry = this.loadedResources.get(resource);
		if(entry!=null) {
			result=entry.file();
		}
		return result;
	}

	String get(String resource) throws IOException {
		String result=null;
		File file = file(resource);
		if(file!=null) {
			 result = FileUtils.readFileToString(file);
		}
		return result;
	}

	void dispose() {
		LOGGER.debug("Disposing representations cached at {}...",this.cacheDirectory);
		for(ContentEntry entry:this.loadedResources.values()) {
			if(entry.file().exists()) {
				LOGGER.debug("- Deleting representation for resource {} ({})...",entry.resource(),entry.file());
				if(!entry.file().delete()) {
					entry.file().deleteOnExit();
					LOGGER.debug("  + Could not delete {}. Scheduling for deletion on exit.",entry.resource());
				}
			}
		}
		if(this.created) {
			LOGGER.debug("- Deleting cache directory...");
			try {
				FileUtils.deleteDirectory(this.cacheDirectory);
				LOGGER.debug("  + Cache directory deleted.");
			} catch (IOException e) {
				LOGGER.debug("  + Could not delete cache directory: {}",e.getMessage());
			}
		}
		LOGGER.debug("Representations cached at {} deleted.",this.cacheDirectory);
	}

	static CachedRepresentationManager create(File cacheDirectory) {
		CachedRepresentationManager manager = new CachedRepresentationManager(cacheDirectory);
		manager.init();
		return manager;
	}

}
