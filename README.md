# An example read-write Linked Data application with LDP4j

This repository includes the Contacts Application, a multi-tenant address book example application used for the hands-on session of the [LDP4j tutorial](http://www.ldp4j.org/tutorials/eswc2015/) held at the [ESWC 2015](http://2015.eswc-conferences.org/) conference.

The application is built to work with the pre-release version of the LDP4j framework prepared for the [tutorial](https://github.com/ldp4j/ldp4j/releases/tag/eswc2015-tutorial). So before build the example application you need to get the sources and build the version locally:

	git clone http://github.com/ldp4j/ldp4j
	git checkout eswc2015-tutorial
	mvn clean install

## Build Status

[![Build Status](https://travis-ci.org/ldp4j/eswc2015-tutorial.svg?branch=master)](https://travis-ci.org/ldp4j/eswc2015-tutorial)