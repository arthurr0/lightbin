package dev.shitzuu.lightbin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class LightbinApplication {

    public static void main(String[] args) {
        SpringApplication.run(LightbinApplication.class, args);
    }
}
