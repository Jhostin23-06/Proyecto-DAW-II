package com.transporte.mstransportistas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MsTransportistasApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsTransportistasApplication.class, args);
    }

}
