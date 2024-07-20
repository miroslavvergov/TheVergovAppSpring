package com.project.thevergov.cache;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * CacheConfig: Configuration class for setting up application cache.
 * This class uses Spring's @Configuration to define beans related to caching.
 */
@Configuration
public class CacheConfig {

    /**
     * Creates and configures a CacheStore bean for caching user login attempts.
     *
     * @return a CacheStore instance with String keys and Integer values, with an expiration time of 900 seconds.
     */
    @Bean //(name = {"userLoginCache"}) if we decide to implement another cache - give names
    public CacheStore<String, Integer> userCache() {
        // Create a CacheStore instance with a 15-minute expiration time
        return new CacheStore<>(900, TimeUnit.SECONDS);
    }
}
