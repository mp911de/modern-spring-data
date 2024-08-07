/*
 * Copyright 2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sample.modernspringdata.scrolling;

import static org.assertj.core.api.Assertions.*;

import java.io.IOException;

import com.sample.modernspringdata.MongoDBContainers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.OffsetScrollPosition;
import org.springframework.data.domain.ScrollPosition;
import org.springframework.data.domain.Window;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.support.WindowIterator;

import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Integration tests showing scrolling queries using Mongo repositories.
 */
@DataMongoTest
@Testcontainers
class RepositoryScrollingIntegrationTests {

	@Container
	@ServiceConnection static MongoDBContainer container = MongoDBContainers.createContainer();

	@Autowired MongoOperations ops;

	@Autowired PersonRepository personRepository;

	@Value("classpath:/person-test-data.json") Resource personTestData;

	@BeforeEach
	void setUp() throws IOException {
		PersonFixture.setup(personTestData, ops);
	}

	@Test
	void scrollWithOffset() {

		Window<Person> scroll = personRepository.findByCountryOrderByLastNameAscFirstNameAsc("Austria", Limit.of(2), ScrollPosition.offset());

		assertThat(scroll).hasSize(2).extracting(Person::getLastName).contains("Anderson", "Brown");
		assertThat(scroll.positionAt(1)).isInstanceOf(OffsetScrollPosition.class);

		scroll = personRepository.findByCountryOrderByLastNameAscFirstNameAsc("Austria", Limit.of(2), scroll.positionAt(1));

		assertThat(scroll).hasSize(2).extracting(Person::getLastName).contains("Gonzalez", "Harris");
		assertThat(scroll.positionAt(1)).isInstanceOf(OffsetScrollPosition.class);
	}

	@Test
	void scrollWithKeyset() {

		WindowIterator<Person> iterator = WindowIterator
				.of(position -> personRepository.findByCountryOrderByLastNameAscFirstNameAsc("Austria", Limit.of(2), position))
				.startingAt(ScrollPosition.keyset());

		assertThat(iterator.next()).extracting(Person::getLastName).isEqualTo("Anderson");
		assertThat(iterator.next()).extracting(Person::getLastName).isEqualTo("Brown");
		assertThat(iterator.next()).extracting(Person::getLastName).isEqualTo("Gonzalez");
		assertThat(iterator.next()).extracting(Person::getLastName).isEqualTo("Harris");
	}

	@Test
	void scrollWithStringQuery() {

		WindowIterator<Person> iterator = WindowIterator.of(position -> personRepository.findBy(Limit.of(2), position))
				.startingAt(ScrollPosition.keyset());

		assertThat(iterator.next()).extracting(Person::getLastName).isEqualTo("Davis");
		assertThat(iterator.next()).extracting(Person::getLastName).isEqualTo("Garcia");
		assertThat(iterator.next()).extracting(Person::getLastName).isEqualTo("Hernandez");
		assertThat(iterator.next()).extracting(Person::getLastName).isEqualTo("Johnson");
	}

}
