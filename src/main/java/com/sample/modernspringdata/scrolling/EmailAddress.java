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

import java.util.function.Predicate;
import java.util.regex.Pattern;

import org.springframework.util.ObjectUtils;

/**
 * Value object representing an email address. Instances can be created through the {@link #of(String)} factory method.
 *
 * @author Mark Paluch
 */
public class EmailAddress {

	private final static Pattern PATTERN = Pattern.compile(
			"[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\n");
	private final static Predicate<String> PREDICATE = PATTERN.asMatchPredicate();

	private final String email;

	private EmailAddress(String email) {
		this.email = email;
	}

	/**
	 * Factory method to create a new EmailAddress.
	 *
	 * @param email the email address to use.
	 * @return a new EmailAddress object for {@code email}.
	 * @throws IllegalArgumentException if {@code email} is not a valid email address.
	 */
	// @PersistenceCreator
	public static EmailAddress of(String email) {

		if (ObjectUtils.isEmpty(email) || !PREDICATE.test(email)) {
			throw new IllegalArgumentException("Invalid email address: %s".formatted(email));
		}

		return new EmailAddress(email);
	}

	public String getEmail() {
		return email;
	}

	@Override
	public String toString() {
		return email;
	}

}
