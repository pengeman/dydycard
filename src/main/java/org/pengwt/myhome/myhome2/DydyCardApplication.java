package org.pengwt.myhome.myhome2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class DydyCardApplication {
	private final static Integer ver = 1; // 当前版本
	@Autowired
	Environment environment;


	public String postrun(){
		return environment.getProperty("server.port");
	}
	public static void main(String[] args) {

		System.out.println("before appliation...");
		SpringApplication.run(DydyCardApplication.class, args);

	}

}
