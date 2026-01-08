package eu.irrationalcharm.mobilebff;

import eu.irrationalcharm.mobilebff.client.PersistenceServiceClient;
import eu.irrationalcharm.mobilebff.client.UserServiceClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.service.registry.ImportHttpServices;


@SpringBootApplication
//@ImportHttpServices(basePackageClasses = {UserServiceClient.class, PersistenceServiceClient.class})
public class MobileBffApplication {

    void main(String[] args) {
        SpringApplication.run(MobileBffApplication.class, args);
    }

}
