package com.emc.eNas.cloudfoundry.broker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;


import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ComponentScan
@EnableAutoConfiguration

@EnableJpaRepositories

public class eNasApplication {

    
    public static void main(String[] args) {
        SpringApplication.run(eNasApplication.class, args);
    }

}
