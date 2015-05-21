package org.ldp4j.tutorial.frontend;

import java.net.URI;

import org.ldp4j.application.data.DataSet;
import org.ldp4j.application.data.DataSetFactory;
import org.ldp4j.application.data.DataSetUtils;
import org.ldp4j.application.data.ExternalIndividual;
import org.ldp4j.application.data.Literal;
import org.ldp4j.application.data.ManagedIndividual;
import org.ldp4j.application.data.ManagedIndividualId;
import org.ldp4j.application.data.Name;
import org.ldp4j.application.data.NamingScheme;
import org.ldp4j.tutorial.application.api.Person;

public class PersonMapper {

	private PersonMapper() {
	}

	private static void addDatatypePropertyValue(DataSet dataSet, Name<String> name, String propertyURI, Object rawValue) {
		ManagedIndividualId individualId = ManagedIndividualId.createId(name, PersonHandler.ID);
		ManagedIndividual individual = dataSet.individual(individualId, ManagedIndividual.class);
		URI propertyId = URI.create(propertyURI);
		Literal<Object> value = DataSetUtils.newLiteral(rawValue);
		individual.addValue(propertyId,value);
	}

	private static void addObjectPropertyValue(DataSet dataSet, Name<String> name, String propertyURI, String uri) {
		ManagedIndividualId individualId = ManagedIndividualId.createId(name, PersonHandler.ID);
		ManagedIndividual individual = dataSet.individual(individualId, ManagedIndividual.class);
		URI propertyId = URI.create(propertyURI);
		ExternalIndividual external = dataSet.individual(URI.create(uri),ExternalIndividual.class);
		individual.addValue(propertyId,external);
	}

	public static Name<String> personName(Person person) {
		return NamingScheme.getDefault().name(person.getAccount());
	}

	public static Name<String> contactsName(Person person) {
		return NamingScheme.getDefault().name(person.getAccount(),"contacts");
	}

	public static DataSet toDataSet(Person person) {
		Name<String> personName=personName(person);

		DataSet dataSet = DataSetFactory.createDataSet(personName);

		addDatatypePropertyValue(dataSet,personName,"http://xmlns.com/foaf/0.1/account", person.getAccount());
		addDatatypePropertyValue(dataSet,personName, "http://xmlns.com/foaf/0.1/name", person.getName());
		addObjectPropertyValue(dataSet,personName,"http://xmlns.com/foaf/0.1/based_near", person.getLocation());
		addObjectPropertyValue(dataSet,personName,"http://xmlns.com/foaf/0.1/workplaceHomepage", person.getWorkplaceHomepage());

		return dataSet;

	}
}