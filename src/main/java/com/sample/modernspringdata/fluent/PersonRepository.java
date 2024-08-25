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

import org.bson.types.ObjectId;

import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Component;

import com.querydsl.core.types.Predicate;

/**
 * Repository implementing {@link QueryByExampleExecutor} and {@link QuerydslPredicateExecutor}.
 *
 * @author Mark Paluch
 */
@Component("fluentPersonRepository")
interface PersonRepository
		extends Repository<Person, ObjectId>,
		QueryByExampleExecutor<Person>,
		QuerydslPredicateExecutor<Person> {

	/**
	 * Find and project a single result by {@code name} and return it as {@code T}.
	 *
	 * @param projection
	 * @param name
	 * @return
	 * @param <T>
	 */
	default <T> T projectByLastName(Class<T> projection, String name) {
		Predicate predicate = QPerson.person.lastName.eq(name)
				.and(QPerson.person.country.eq("Austria"));
		return findBy(predicate, it -> it.as(projection).oneValue());
	}
}
