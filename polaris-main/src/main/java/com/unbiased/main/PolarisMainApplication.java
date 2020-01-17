package com.unbiased.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.transaction.annotation.EnableTransactionManagement;


/**
 * @author longjiang
 */
@EnableTransactionManagement
@SpringBootApplication(exclude = {HibernateJpaAutoConfiguration.class},scanBasePackages = {"com.unbiased.main", "com.unbiased.auth", "com.unbiased.common","com.unbiased.admin"})
public class PolarisMainApplication {

	public static void main(String[] args) {
		SpringApplication.run(PolarisMainApplication.class, args);
	}

}
