package boot;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import domain.api.HotelService;
import domain.api.HotelServiceImpl;
import domain.spi.HotelRepositoryPort;
import infrastructure.jdbc.HotelJdbcRepository;

@SpringBootApplication
@ComponentScan(basePackages= "domain.model,domain.spi,domain.api,application.controller")
public class HotelApplication {
 
	public static void main(String[] args) {
		SpringApplication.run(HotelApplication.class, args);
	}

	@Bean
	private static HotelService createHotelService() throws SQLException {
		return new HotelServiceImpl( getHotelRepository() );
	}
	
	private static HotelRepositoryPort getHotelRepository() throws SQLException {
		return new HotelJdbcRepository( getJdbcConnection() ); 
	}

	private static Connection getJdbcConnection() throws SQLException {
//		return DriverManager.getConnection("jdbc:h2:~/test;INIT=runscript from 'classpath:hotel-schema.sql'", "sa", "");
		return DriverManager.getConnection("jdbc:mysql://192.168.1.83:3306/springbootspringcloud", "root", "root");
	}
}
