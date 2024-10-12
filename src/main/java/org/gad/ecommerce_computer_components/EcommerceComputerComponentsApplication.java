package org.gad.ecommerce_computer_components;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "org.gad.ecommerce_computer_components.persistence.repository")
public class EcommerceComputerComponentsApplication {

	public static void main(String[] args) {
		SpringApplication.run(EcommerceComputerComponentsApplication.class, args);
	}

}
