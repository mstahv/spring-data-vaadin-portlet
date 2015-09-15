package com.example.plugins;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.vaadin.spring.VaadinConfiguration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Import(VaadinConfiguration.class)
@ComponentScan(basePackages = {"com.example.plugins" /*, ...other packages with Spring beans... */})
public class YourApplicationConfiguration {
    //...
    //Your condiguration of DataSource, SessionFactory, TransactionManager, etc.
    //...
    @Configuration
    @PropertySource("classpath:application.properties")
    static class ApplicationProperties {}
    
	
}