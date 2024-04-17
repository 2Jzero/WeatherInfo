package com.project.weatherinfo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = { "com.project.weather.controller", "com.project.weather.model", "com.project.weather.config" } )
@MapperScan(basePackages = {"com.project.weather.config"})
public class WeatherInfoApplication {

	public static void main(String[] args) {
		SpringApplication.run(WeatherInfoApplication.class, args);
	}
}
