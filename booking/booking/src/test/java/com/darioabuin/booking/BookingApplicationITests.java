package com.darioabuin.booking;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.RequestHeadersUriSpec;
import org.springframework.web.client.RestClient.ResponseSpec;

import com.darioabuin.booking.application.controller.BookingController;
import com.darioabuin.booking.application.dto.HotelDto;
import com.darioabuin.booking.domain.api.BookingServiceImpl;
import com.darioabuin.booking.domain.model.Booking;
import com.darioabuin.booking.infrastructure.jdbc.BookingJdbcRepository;
import com.github.database.rider.core.api.connection.ConnectionHolder;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.connection.ConnectionHolderImpl;
import com.github.database.rider.junit5.DBUnitExtension;

import io.micrometer.common.util.StringUtils;

@ExtendWith(DBUnitExtension.class)
@DataSet(value = "bookings.yml")
class BookingApplicationITests {

	private static final String IBIS = "IBIS Málaga";
	private static final String RIU = "RIU PALACE";
	@SuppressWarnings("unused")
	private ConnectionHolder connectionHolder;
	private Connection conn;
	private BookingController bookingController;
	private RestClient restClient;

	@BeforeEach
	void setup() throws SQLException {
		conn = DriverManager.getConnection("jdbc:h2:mem:test;INIT=runscript from 'classpath:booking-schema.sql'", "sa",
				"");
		connectionHolder = new ConnectionHolderImpl(conn);
		restClient = mock(RestClient.class);
		bookingController = new BookingController(new BookingServiceImpl(new BookingJdbcRepository(conn)), restClient);
	}

	@Test
	void shouldGetBookings() {
		// GIVEN
		Booking expectedBooking = populateBooking();
		String hotelName = RIU;

		// WHEN
		setupMockRestClient(hotelName);
		ResponseEntity<?> response = bookingController.getAllBookings(hotelName);

		// THEN
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		@SuppressWarnings("unchecked")
		List<Booking> responseList = (List<Booking>) response.getBody();
		assertNotNull(responseList);
		Booking actualBooking = responseList.get(0);
		assertNotNull(actualBooking);
		assertEquals(expectedBooking.getIdReserva(), actualBooking.getIdReserva());
		assertEquals(expectedBooking.getCustomerName(), actualBooking.getCustomerName());
		assertEquals(expectedBooking.getDNI(), actualBooking.getDNI());
		assertEquals(expectedBooking.getIdHotel(), actualBooking.getIdHotel());
		assertEquals(expectedBooking.getIdVuelo(), actualBooking.getIdVuelo());
	}

	@Test
	void shouldNotGetBookings_noBookingsForGivenHotelName() {
		// GIVEN
		String hotelName = IBIS;

		// WHEN
		setupMockRestClient(hotelName);
		ResponseEntity<?> response = bookingController.getAllBookings(hotelName);

		// THEN
		assertNotNull(response);
		assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
		assertEquals("No bookings found for hotel " + hotelName, response.getBody());
	}

	@ParameterizedTest
	@ValueSource(strings = { "RIO PALACIO", "Ibis Málaga", "IBIS Malaga" })
	void shouldNotGetBookings_unexistingHotelName(String hotelName) {
		// GIVEN

		// WHEN
		setupMockRestClient(hotelName);
		ResponseEntity<?> response = bookingController.getAllBookings(hotelName);

		// THEN
		assertNotNull(response);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("No hotels found with the hotel name " + hotelName, response.getBody());
	}

	@ParameterizedTest
	@NullAndEmptySource
	void shouldNotGetBookings_nullAndEmptySource(String hotelName) {
		// GIVEN

		// WHEN
		setupMockRestClient(hotelName);
		ResponseEntity<?> response = bookingController.getAllBookings(hotelName);

		// THEN
		assertNotNull(response);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("Please, enter a hotel name to check.", response.getBody());
	}

	private Booking populateBooking() {
		return new Booking(Long.valueOf(1), "Elsa Polindo", "12345678A", Long.valueOf(1), Long.valueOf(1));
	}

	private void setupMockRestClient(String hotelName) {
		RequestHeadersUriSpec getRequest = mock(RequestHeadersUriSpec.class);
		ResponseSpec response = mock(ResponseSpec.class);
		when(restClient.get()).thenReturn(getRequest);
		when(getRequest.uri(anyString())).thenReturn(getRequest);
		when(getRequest.retrieve()).thenReturn(response);
		when(response.body(HotelDto.class)).thenReturn(getExpectedHotelDto(StringUtils.isBlank(hotelName) ? ""
				: hotelName.equals(RIU) ? RIU : hotelName.equals(IBIS) ? IBIS : ""));
	}

	private HotelDto getExpectedHotelDto(String hotelName) {
		HotelDto hotelDto = switch (hotelName) {
		case RIU -> new HotelDto(Long.valueOf(1), RIU, "5 stars", BigDecimal.valueOf(123.50), true);
		case IBIS -> new HotelDto(Long.valueOf(2), IBIS, "2 stars", BigDecimal.valueOf(49.50), true);
		default -> null;
		};
		return hotelDto;
	}
}
