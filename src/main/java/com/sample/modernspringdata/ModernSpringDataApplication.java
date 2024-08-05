package com.sample.modernspringdata;

import org.bson.BsonDocument;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer;
import org.springframework.context.annotation.Bean;

import com.mongodb.event.CommandListener;
import com.mongodb.event.CommandStartedEvent;

@SpringBootApplication
public class ModernSpringDataApplication {

	public static void main(String[] args) {
		SpringApplication.run(ModernSpringDataApplication.class, args);
	}

	@Bean
	MongoClientSettingsBuilderCustomizer addCommandLogger() {
		return builder -> builder.addCommandListener(new LoggingCommandLister());
	}

	static class LoggingCommandLister implements CommandListener {

		@Override
		public void commandStarted(CommandStartedEvent event) {

			BsonDocument command = new BsonDocument();
			command.putAll(event.getCommand());
			if (command.containsKey("find")) {

				// cleaner logging
				command.remove("$clusterTime");
				command.remove("$readPreference");
				command.remove("lsid");

				LoggerFactory.getLogger(getClass()).info("Command: " + command.toJson());
			}
		}
	}

}
