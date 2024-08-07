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
package com.sample.modernspringdata;

import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * @author Mark Paluch
 */
public class MongoDBContainers {

	private static final DockerImageName imageName = DockerImageName.parse("mongo:8.0.0-rc15")
			.asCompatibleSubstituteFor("mongo");

	/**
	 * Creates a new {@link MongoDBContainer}.
	 *
	 * @return
	 */
	public static MongoDBContainer createContainer() {
		return new MongoDBContainer(imageName).withReuse(true);

	}
}
