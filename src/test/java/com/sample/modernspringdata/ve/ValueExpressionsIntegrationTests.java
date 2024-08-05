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
package com.sample.modernspringdata.ve;

import static org.assertj.core.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.mongodb.core.MongoOperations;

import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@DataMongoTest
@Testcontainers
class ValueExpressionsIntegrationTests {

	@Container
	@ServiceConnection static MongoDBContainer container = new MongoDBContainer(
			DockerImageName.parse("mongo:8.0.0-rc15").asCompatibleSubstituteFor("mongo")).withReuse(true);

	@Autowired MongoOperations ops;

	@BeforeEach
	void setUp() {
		ops.getCollectionNames().forEach(ops::dropCollection);
	}

	@Test
	void configurationShouldProvideCollectionName() {

		ops.save(new ConfigurableContact());

		assertThat(ops.getCollectionNames()).containsExactly("fallback_contact");
	}

}
