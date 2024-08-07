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

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.bson.Document;

import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver;

import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import com.sample.modernspringdata.scrolling.Person;

/**
 * Fixture to setup collections and test data for {@link Person}.
 *
 * @author Mark Paluch
 */
class PersonFixture {

	/**
	 * Setup the fixture for {@link com.sample.modernspringdata.scrolling.Person} test data.
	 *
	 * @param jsonData
	 * @param ops
	 * @throws IOException
	 */
	static void setup(Resource jsonData, MongoOperations ops) throws IOException {

		ops.getCollectionNames().forEach(ops::dropCollection);

		ObjectMapper mapper = new ObjectMapper();
		List<Map<String, Object>> testData = mapper.readValue(jsonData.getURL(), List.class);

		testData.forEach(it -> ops.save(new Document(it), "person"));
	}

}
