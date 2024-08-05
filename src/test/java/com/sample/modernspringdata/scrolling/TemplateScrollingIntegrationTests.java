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
import static org.springframework.data.mongodb.core.query.Criteria.*;
import static org.springframework.data.mongodb.core.query.Query.*;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.KeysetScrollPosition;
import org.springframework.data.domain.OffsetScrollPosition;
import org.springframework.data.domain.ScrollPosition;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Window;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;

import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import com.mongodb.client.MongoClient;

/**
 * Integration tests showing scrolling queries using {@link MongoOperations}.
 */
@DataMongoTest
@Testcontainers
class TemplateScrollingIntegrationTests {

	@Container
	@ServiceConnection static MongoDBContainer container = new MongoDBContainer(
			DockerImageName.parse("mongo:8.0.0-rc15").asCompatibleSubstituteFor("mongo")).withReuse(true);

	@Autowired MongoClient client;
	@Autowired MongoOperations ops;

	@Value("classpath:/person-test-data.json") Resource personTestData;

	@BeforeEach
	void setUp() throws IOException {
		PersonFixture.setup(personTestData, ops);
	}

	@Test
	void scrollWithOffset() {

		Query query = query(where("country").is("Austria")).with(ScrollPosition.offset())
				.with(Sort.by("lastName", "firstName")).limit(2);

		Window<Person> scroll = ops.scroll(query, Person.class);

		assertThat(scroll).hasSize(2).extracting(Person::getLastName).contains("Anderson", "Brown");
		assertThat(scroll.positionAt(1)).isInstanceOf(OffsetScrollPosition.class);

		query.with(scroll.positionAt(1));
		scroll = ops.scroll(query, Person.class);

		assertThat(scroll).hasSize(2).extracting(Person::getLastName).contains("Gonzalez", "Harris");
		assertThat(scroll.positionAt(1)).isInstanceOf(OffsetScrollPosition.class);
	}

	@Test
	void scrollWithKeyset() {

		Query query = query(where("country").is("Austria")).with(ScrollPosition.keyset())
				.with(Sort.by("lastName", "firstName")).limit(2);

		Window<Person> scroll = ops.scroll(query, Person.class);

		assertThat(scroll).hasSize(2).extracting(Person::getLastName).contains("Anderson", "Brown");
		assertThat(scroll.positionAt(1)).isInstanceOf(KeysetScrollPosition.class);

		query.with(scroll.positionAt(1));
		scroll = ops.scroll(query, Person.class);

		assertThat(scroll).hasSize(2).extracting(Person::getLastName).contains("Gonzalez", "Harris");
		assertThat(scroll.positionAt(1)).isInstanceOf(KeysetScrollPosition.class);
	}

}
