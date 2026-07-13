package com.fission.backend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.fission.backend.mapper")
public class FissionSystemBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(FissionSystemBackendApplication.class, args);
	}

}
