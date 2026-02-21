package com.transporte.mstransportistas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.transporte.mstransportistas.application.port.outservice.client")
public class MsTransportistasApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsTransportistasApplication.class, args);
    }

}
