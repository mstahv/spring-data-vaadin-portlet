package org.vaadin.springportlet;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.vaadin.spring.VaadinConfiguration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Import(VaadinConfiguration.class)
@ComponentScan(basePackages = {"org.vaadin.springportlet"})
public class ApplicationConfiguration {
	
    @Configuration
    @PropertySource("classpath:application.properties")
    static class ApplicationProperties {}
    
}