package eu.irrationalcharm.mobilebff;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@Slf4j
@SpringBootApplication
//@ImportHttpServices(basePackageClasses = {UserServiceClient.class, PersistenceServiceClient.class})
public class MobileBffApplication {

    static void main(String[] args) {
        SpringApplication.run(MobileBffApplication.class, args);
    }

}
