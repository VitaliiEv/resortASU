package vitaliiev.resortASU;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ResortASUApplication {

    public static void main(String[] args) {
        SpringApplication.run(ResortASUApplication.class, args);
    }

}
