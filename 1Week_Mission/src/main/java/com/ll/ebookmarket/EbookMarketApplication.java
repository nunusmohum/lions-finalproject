package com.ll.ebookmarket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class EbookMarketApplication {

    public static void main(String[] args) {
        SpringApplication.run(EbookMarketApplication.class, args);
    }

}
