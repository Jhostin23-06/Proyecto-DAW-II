package core.cibertec.ms_servicios;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "core.cibertec.ms_servicios.application.port.outservice.client")
public class MsServiciosApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsServiciosApplication.class, args);
	}

}
