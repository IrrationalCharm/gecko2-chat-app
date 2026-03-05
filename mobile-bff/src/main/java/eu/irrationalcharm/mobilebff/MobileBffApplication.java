package eu.irrationalcharm.mobilebff;

import eu.irrationalcharm.mobilebff.client.PersistenceServiceClient;
import eu.irrationalcharm.mobilebff.client.UserServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.service.registry.ImportHttpServices;


@Slf4j
@SpringBootApplication
//@ImportHttpServices(basePackageClasses = {UserServiceClient.class, PersistenceServiceClient.class})
public class MobileBffApplication implements CommandLineRunner {

    void main(String[] args) {
        SpringApplication.run(MobileBffApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("version number 1.0 is running");
    }
}
