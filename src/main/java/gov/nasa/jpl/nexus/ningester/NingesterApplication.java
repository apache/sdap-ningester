package gov.nasa.jpl.nexus.ningester;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class NingesterApplication {

    public static void main(String[] args) {

        ApplicationContext context = SpringApplication.run(NingesterApplication.class, args);
        SpringApplication.exit(context);
    }
}
