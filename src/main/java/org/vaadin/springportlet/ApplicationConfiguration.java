package org.vaadin.springportlet;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.vaadin.spring.VaadinConfiguration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@Import(VaadinConfiguration.class)
@ComponentScan("org.vaadin.springportlet")
@EnableJpaRepositories
public class ApplicationConfiguration extends SpringBootServletInitializer {

	@Configuration
	@PropertySource("classpath:application.properties")
	static class ApplicationProperties {
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(ApplicationConfiguration.class);
	}

}
