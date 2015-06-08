# An example read-write Linked Data application with LDP4j

This repository includes the Contacts Application, a multi-tenant address book example application used for the hands-on session of the [LDP4j tutorial](http://www.ldp4j.org/tutorials/eswc2015/) held at the [ESWC 2015](http://2015.eswc-conferences.org/) conference.

The application is built to work with the latest version of the LDP4j framework. To follow the tutorial you will need to get the [release version](https://github.com/ldp4j/eswc2015-tutorial/releases/tag/eswc2015-tutorial) of the application prepared for the tutorial, which is built to work with the [pre-release version](https://github.com/ldp4j/ldp4j/releases/tag/eswc2015-tutorial) of the LDP4j framework prepared also for the tutorial.

Thus, to follow the tutorial you will first need to get the sources and build the pre-release version of LDP4j locally:

	git clone http://github.com/ldp4j/ldp4j
	cd ldp4j
	git checkout eswc2015-tutorial
	mvn clean install

Then you will need to get the application and build the version used for the tutorial:

	git clone http://github.com/ldp4j/eswc2015-tutorial
	cd eswc2015-tutorial
	git checkout eswc2015-tutorial
	mvn clean install

## Build Status

[![Build Status](https://travis-ci.org/ldp4j/eswc2015-tutorial.svg?branch=master)](https://travis-ci.org/ldp4j/eswc2015-tutorial)
