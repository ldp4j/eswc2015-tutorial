package org.ldp4j.tutorial.application.core;

import org.ldp4j.tutorial.application.api.Person;

final class MutablePerson implements Person {
	String account;
	String name;
	String location;
	String workplaceHomepage;

	@Override
	public void setWorkplaceHomepage(String workplacehomepage) {
		this.workplaceHomepage = workplacehomepage;

	}

	@Override
	public void setName(String name) {
		this.name = name;

	}

	@Override
	public void setLocation(String location) {
		this.location = location;

	}

	@Override
	public void setAccount(String account) {
		this.account = account;

	}

	@Override
	public String getWorkplaceHomepage() {
		return workplaceHomepage;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getLocation() {
		return location;
	}

	@Override
	public String getAccount() {
		return account;
	}
}