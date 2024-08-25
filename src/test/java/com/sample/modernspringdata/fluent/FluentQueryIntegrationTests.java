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
package com.sample.modernspringdata.fluent;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.data.mongodb.core.query.Criteria.*;
import static org.springframework.data.mongodb.core.query.Query.*;
import static org.springframework.data.mongodb.core.query.Update.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import com.sample.modernspringdata.MongoDBContainers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.UntypedExampleMatcher;
import org.springframework.data.mongodb.core.query.Update;

import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Integration tests showing fluent API usage in action.
 */
@DataMongoTest
@Testcontainers
class FluentQueryIntegrationTests {

	@Container
	@ServiceConnection static MongoDBContainer container = MongoDBContainers.createContainer();

	@Autowired MongoOperations ops;

	@Autowired PersonRepository repository;

	@Value("classpath:/person-test-data.json") Resource personTestData;

	@BeforeEach
	void setUp() throws IOException {
		PersonFixture.setup(personTestData, ops);
	}

	@Test
	void findByQuerydsl() {

		interface NameOnly {

			String getFirstName();

			String getLastName();
		}

		NameOnly williams = repository.projectByLastName(NameOnly.class, "Williams");

		assertThat(williams.getFirstName()).isEqualTo("Olivia");
		assertThat(williams.getLastName()).isEqualTo("Williams");
	}

	@Test
	void findByFluentTemplateApi() {

		interface NameOnly {

			String getFirstName();

			String getLastName();
		}

		NameOnly nameOnly = ops.query(Person.class)
				.as(NameOnly.class)
				.matching(query(where("country").is("Austria")
						.and("lastName").is("Williams")))
				.firstValue();

		assertThat(nameOnly.getFirstName()).isEqualTo("Olivia");
		assertThat(nameOnly.getLastName()).isEqualTo("Williams");
	}

	@Test
	void insertByFluentTemplateApi() {

		Person saved = ops.insert(Person.class)
				.inCollection("other_collection")
				.one(new Person("USA", "Walter", "White"));

		assertThat(saved.getId()).isNotNull();
	}

	@Test
	void findAndModifyByFluentTemplateApi() {

		Optional<Person> modified = ops.update(Person.class)
				.matching(query(where("country").is("Austria").and("lastName")
						.is("Williams")))
				.apply(update("firstName", "Serena"))
				.withOptions(FindAndModifyOptions.options().returnNew(true))
				.findAndModify();

		assertThat(modified)
				.isNotEmpty()
				.hasValueSatisfying(actual -> {
			assertThat(actual.getFirstName()).isEqualTo("Serena");
		});
	}

	@Test
	void findByFluentRepositoryApi() {

		record NameDto(String firstName, String lastName) {
		}

		Person probe = new Person("Austria", "Williams", null);

		List<NameDto> result = repository.findBy(Example.of(probe, UntypedExampleMatcher.matching()), query ->
												query.as(NameDto.class)
														.sortBy(Sort.by("lastName"))
														.limit(1)
														.all());

		assertThat(result)
				.hasSize(1)
				.extracting(NameDto::firstName)
				.containsOnly("Olivia");
	}

}
