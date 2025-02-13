package com.darioabuin.booking;

import java.sql.DriverManager;
import java.sql.SQLException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;

import com.darioabuin.booking.domain.api.BookingService;
import com.darioabuin.booking.domain.api.BookingServiceImpl;
import com.darioabuin.booking.infrastructure.jdbc.BookingJdbcRepository;

@SpringBootApplication
public class BookingApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookingApplication.class, args);
	}
	
	@Bean
	BookingService createBookingService() throws SQLException {
		return new BookingServiceImpl(createBookingJdbcRepository());
	}
	
	@Bean
	BookingJdbcRepository createBookingJdbcRepository() throws SQLException {
//		return new BookingJdbcRepository(DriverManager.getConnection("jdbc:h2:~/test;INIT=runscript from 'classpath:booking-schema.sql'", "sa", ""));
		return new BookingJdbcRepository(DriverManager.getConnection("jdbc:mysql://localhost:3306/springbootspringcloud", "root", "root"));
	}
	
	@Bean
	RestClient.Builder getBuilder() {
		return RestClient.builder();
	}
	
	@Bean
	RestClient getClient(RestClient.Builder builder) {
		return builder.build();
	}
}
