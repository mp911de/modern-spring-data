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

import java.io.IOException;

import com.sample.modernspringdata.MongoDBContainers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.data.mongodb.core.query.Update.*;

/**
 * Integration tests showing {@link org.springframework.data.annotation.PersistenceCreator @PersistenceCreator} in
 * action.
 */
@DataMongoTest
@Testcontainers
class PersistenceCreatorIntegrationTests {

	@Container
	@ServiceConnection static MongoDBContainer container = MongoDBContainers.createContainer();

	@Autowired MongoOperations ops;

	@Value("classpath:/person-test-data.json") Resource personTestData;

	@BeforeEach
	void setUp() throws IOException {
		PersonFixture.setup(personTestData, ops);
		ops.updateMulti(new Query(), update("email", "foo"), Person.class);
	}

	@Test
	void loadShouldFail() {
		ops.findAll(Person.class);
	}

}
