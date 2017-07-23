package com.mizuho.trade.instrument;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCaching
@EnableScheduling
public class MizuhoPriceServiceApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder().sources(MizuhoPriceServiceApplication.class)
                .profiles("app").run(args);

    }
}
