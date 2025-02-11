package com.darioabuin.flights;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.darioabuin.flights.domain.api.FlightService;
import com.darioabuin.flights.domain.api.FlightServiceImpl;
import com.darioabuin.flights.domain.spi.FlightRepositoryPort;
import com.darioabuin.flights.infrastructure.jdbc.FlightJdbcRepository;

@SpringBootApplication
public class FlightsApplication {

	public static void main(String[] args) {
		SpringApplication.run(FlightsApplication.class, args);
	}
	
	@Bean
	FlightService createFlightService() throws SQLException {
		return new FlightServiceImpl(createFlightRepositoryPort());
	}
	
	@Bean
	FlightRepositoryPort createFlightRepositoryPort() throws SQLException {
		return new FlightJdbcRepository(createDatabaseConnection());
	}
	
	@Bean
	Connection createDatabaseConnection() throws SQLException {
		return DriverManager.getConnection("jdbc:h2:~/test;INIT=runscript from 'classpath:flights-schema.sql'", "sa", "");
	}

}
