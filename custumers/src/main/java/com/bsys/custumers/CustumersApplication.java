package com.bsys.custumers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class CustumersApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustumersApplication.class, args);
    }

}
