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

import org.bson.types.ObjectId;

import org.springframework.data.domain.Limit;
import org.springframework.data.domain.ScrollPosition;
import org.springframework.data.domain.Window;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.Repository;

/**
 * Repository using scroll queries and the {@link Limit} parameter.
 *
 * @author Mark Paluch
 */
interface PersonRepository extends Repository<Person, ObjectId> {

	// @Query(sort = "{lastName: 1, firstName: 1}")
	Window<Person> findByCountryOrderByLastNameAscFirstNameAsc(String country, Limit limit, ScrollPosition scrollPosition);

	@Query(value = "{country: 'USA'}", sort = "{lastName: 1, firstName: 1}")
	Window<Person> findBy(Limit limit, ScrollPosition scrollPosition);

}
