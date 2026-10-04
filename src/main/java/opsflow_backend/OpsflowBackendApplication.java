package opsflow_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(
		exclude = UserDetailsServiceAutoConfiguration.class
)
public class OpsflowBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(
				OpsflowBackendApplication.class,
				args
		);
	}
}