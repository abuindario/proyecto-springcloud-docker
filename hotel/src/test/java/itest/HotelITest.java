package itest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.github.database.rider.core.api.connection.ConnectionHolder;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.connection.ConnectionHolderImpl;
import com.github.database.rider.junit5.DBUnitExtension;

import application.controller.HotelController;
import domain.api.HotelServiceImpl;
import domain.model.Hotel;
import infrastructure.jdbc.HotelJdbcRepository;

@ExtendWith(DBUnitExtension.class)
@DataSet("initialhotels.yml")
class HotelITest {
	
	@SuppressWarnings("unused")
	private ConnectionHolder connectionHolder;
	private Connection conn;
	private HotelController hotelController;

	@BeforeEach
	void setup() throws SQLException {
		conn = DriverManager.getConnection("jdbc:h2:mem:test;INIT=runscript from 'classpath:hotel-schema.sql'", "sa", "");
		connectionHolder = new ConnectionHolderImpl(conn);
		hotelController = new HotelController(new HotelServiceImpl(new HotelJdbcRepository(conn)));
	}
	
	@Test
	void shouldGetHotelDetails_givenHotelName() {
		// GIVEN
		Hotel expectedHotel = populateHotel(1);
		String hotelName = "RIU PALACE";
		
		// WHEN 
		ResponseEntity<Hotel> response = hotelController.findByName(hotelName);
		Hotel actualHotel = response.getBody();
		
		// THEN
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(actualHotel);
		assertEquals(expectedHotel.getIdHotel(), actualHotel.getIdHotel());
		assertEquals(expectedHotel.getName(), actualHotel.getName());
		assertEquals(expectedHotel.getCategory(), actualHotel.getCategory());
		assertEquals(0, expectedHotel.getPrice().compareTo(actualHotel.getPrice()));
		assertTrue(actualHotel.getAvailable());
	}
	
	@Test
	void shouldFailGettingHotelDetails_unexistingHotelName() {
		// GIVEN
		String hotelName = "RIO PALACIO";
		
		// WHEN 
		ResponseEntity<Hotel> response = hotelController.findByName(hotelName);
		
		// THEN
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertNull(response.getBody());
	}
	
	@ParameterizedTest
	@NullAndEmptySource
	void shouldFailGettingHotelDetails_unexistingHotelName(String hotelName) {
		// GIVEN
		// provided by annotation
		
		// WHEN 
		ResponseEntity<Hotel> response = hotelController.findByName(hotelName);
		
		// THEN
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertNull(response.getBody());
	}

	@Test
	void shouldGetHotelList() {
		// GIVEN
		Hotel h1 = populateHotel(1);
		Hotel h2 = populateHotel(2);
		
		// WHEN
		ResponseEntity<List<Hotel>> response = hotelController.getAllHotels();
		List<Hotel> hotelList = response.getBody();
		
		// THEN
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(hotelList);
		assertEquals(2, hotelList.size(), "Not the expected result list");
		assertEquals(h1.getIdHotel(), hotelList.get(0).getIdHotel());
		assertEquals(h1.getName(), hotelList.get(0).getName());
		assertEquals(h1.getCategory(), hotelList.get(0).getCategory());
		assertEquals(0, h1.getPrice().compareTo(hotelList.get(0).getPrice()));
		assertEquals(h1.getAvailable(), hotelList.get(0).getAvailable());
		assertEquals(h2.getIdHotel(), hotelList.get(1).getIdHotel());
		assertEquals(h2.getName(), hotelList.get(1).getName());
		assertEquals(h2.getCategory(), hotelList.get(1).getCategory());
		assertEquals(0, h2.getPrice().compareTo(hotelList.get(1).getPrice()));
		assertEquals(h2.getAvailable(), hotelList.get(1).getAvailable());
	}
	
	private Hotel populateHotel(int id) {
		if(id == 1) {
			return new Hotel(Long.valueOf(1), "RIU PALACE", "5 stars", BigDecimal.valueOf(123.50), true);
		} else if(id == 2) {
			return new Hotel(Long.valueOf(2), "IBIS Málaga", "2 stars", BigDecimal.valueOf(49.50), true);
		}
		return null;
	}
}
