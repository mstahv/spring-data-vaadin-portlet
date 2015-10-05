package org.vaadin.springportlet;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.vaadin.spring.VaadinConfiguration;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@Import(VaadinConfiguration.class)
@ComponentScan("org.vaadin.springportlet")
public class ApplicationConfiguration {
	
    @Configuration
    @PropertySource("classpath:application.properties")
    static class ApplicationProperties {}
    
}
