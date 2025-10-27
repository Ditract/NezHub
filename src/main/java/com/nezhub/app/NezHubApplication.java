package com.nezhub.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableCaching
@EnableMongoRepositories(basePackages = "com.nezhub.app.domain.repository")
public class NezHubApplication {

	public static void main(String[] args) {
		SpringApplication.run(NezHubApplication.class, args);

        System.out.println("\n" +
                "========================================\n" +
                "      NezHub API iniciada con éxito    \n" +
                "========================================\n" +
                " URL: http://localhost:8080\n" +
                " GraphiQL: http://localhost:8080/graphiql\n" +
                " MongoDB: localhost:27017/nezhub\n" +
                " Redis: localhost:6379\n" +
                "========================================\n");
	}

}
