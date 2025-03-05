package com.darioabuin.booking;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.http.HttpResponse;
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
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.reactive.server.WebTestClient.RequestHeadersSpec;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.RequestBodySpec;
import org.springframework.web.client.RestClient.RequestBodyUriSpec;
import org.springframework.web.client.RestClient.RequestHeadersSpec.ConvertibleClientHttpResponse;
import org.springframework.web.client.RestClient.RequestHeadersSpec.ExchangeFunction;
import org.springframework.web.client.RestClient.RequestHeadersUriSpec;
import org.springframework.web.client.RestClient.ResponseSpec;

import com.darioabuin.booking.application.controller.BookingController;
import com.darioabuin.booking.application.dto.BookingDto;
import com.darioabuin.booking.application.dto.HotelDto;
import com.darioabuin.booking.domain.api.BookingServiceImpl;
import com.darioabuin.booking.domain.model.Booking;
import com.darioabuin.booking.infrastructure.jdbc.BookingJdbcRepository;
import com.github.database.rider.core.api.connection.ConnectionHolder;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.core.connection.ConnectionHolderImpl;
import com.github.database.rider.junit5.DBUnitExtension;

import io.micrometer.common.util.StringUtils;

@ExtendWith(DBUnitExtension.class)
@DataSet(value = "bookings.yml")
class BookingApplicationITests {

	private static final String SERVICE_UNAVAILABLE = "SERVICE_UNAVAILABLE";
	private static final String PUT = "PUT";
	private static final String GET = "GET";
	private static final String NULL = "NULL";
	private static final String BAD_REQUEST = "BAD_REQUEST";
	private static final String IBIS = "IBIS Málaga";
	private static final String RIU = "RIU PALACE";
	@SuppressWarnings("unused")
	private ConnectionHolder connectionHolder;
	private Connection conn;
	private BookingController bookingController;
	private RestClient restClient;

	@BeforeEach
	void setup() throws SQLException {
		conn = DriverManager.getConnection("jdbc:h2:mem:test;INIT=runscript from 'classpath:booking-schema.sql'", "sa", "");
		connectionHolder = new ConnectionHolderImpl(conn);
		restClient = mock(RestClient.class);
		bookingController = new BookingController(new BookingServiceImpl(new BookingJdbcRepository(conn)), restClient);
	}
	
	@Test
	@ExpectedDataSet("expectedBookings_afterPost.yml")
	void shouldPostBooking() {
		// GIVEN
		BookingDto bookingDto = populateBookingDto(1);
		Booking expectedBooking = populateExpectedBooking(bookingDto);
		
		// WHEN
		setupMockRestClient_statusCodeOnly(PUT, "OK");
		ResponseEntity<?> response = bookingController.postBooking(bookingDto);
		
		// THEN	
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		Booking actualBooking = (Booking) response.getBody();
		assertEquals(expectedBooking.getIdReserva(), actualBooking.getIdReserva());
		assertEquals(expectedBooking.getCustomerName(), actualBooking.getCustomerName());
		assertEquals(expectedBooking.getDNI(), actualBooking.getDNI());
		assertEquals(expectedBooking.getIdHotel(), actualBooking.getIdHotel());
		assertEquals(expectedBooking.getIdVuelo(), actualBooking.getIdVuelo());
	}
	
	@Test
	@ExpectedDataSet("bookings.yml")
	void shouldFailPostBooking_invalidIdVuelo() {
		// GIVEN
		// populate booking dto with invalid idvuelo 2
		BookingDto bookingDto = populateBookingDto(2);

		// WHEN
		setupMockRestClient_statusCodeOnly(PUT, BAD_REQUEST);
		ResponseEntity<?> response = bookingController.postBooking(bookingDto);
		
		// THEN	
		assertNotNull(response);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("Invalid idVuelo or invalid number of seats to book.", response.getBody());
	}
	
	@Test
	void shouldFailPostBooking_repositoryThrowsSqlException() throws SQLException {
		// GIVEN
		BookingDto bookingDto = populateBookingDto(1);
		BookingServiceImpl mockBookingServiceImpl = mock(BookingServiceImpl.class);
		
		// WHEN
		setupMockRestClient_statusCodeOnly(PUT, "OK");
		when(mockBookingServiceImpl.postBooking(bookingDto)).thenThrow(new SQLException());
		ResponseEntity<?> response = new BookingController(mockBookingServiceImpl, restClient).postBooking(bookingDto);
		
		// THEN	
		assertNotNull(response);
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
	}

	private BookingDto populateBookingDto(int idVuelo) {
		return new BookingDto("Aitor Tilla", "11122233B", 6, Long.valueOf(1), Long.valueOf(idVuelo));
	}
	
	private Booking populateExpectedBooking(BookingDto bookingDto) {
		return new Booking(Long.valueOf(1), bookingDto.getCustomerName(), bookingDto.getDni(), bookingDto.getIdHotel(), bookingDto.getIdVuelo());
	}

	@Test
	void shouldFailGettingBookings_repositoryThrowsSqlException() throws SQLException {
		// GIVEN
		String hotelName = RIU;
		BookingServiceImpl mockBookingServiceImpl = mock(BookingServiceImpl.class);
		
		// WHEN
		setupMockRestClient_responseBody_booking(hotelName);
		when(mockBookingServiceImpl.getAllBookings(Long.valueOf(1))).thenThrow(new SQLException());
		ResponseEntity<?> response = new BookingController(mockBookingServiceImpl, restClient).getAllBookings(hotelName);
		
		// THEN
		assertNotNull(response);
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
	}

	@Test
	void shouldGetBookings() {
		// GIVEN
		Booking expectedBooking = populateBooking();
		String hotelName = RIU;

		// WHEN
		setupMockRestClient_responseBody_booking(hotelName);
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
		setupMockRestClient_responseBody_booking(hotelName);
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
		setupMockRestClient_statusCodeOnly(GET, hotelName);
		ResponseEntity<?> response = bookingController.getAllBookings(hotelName);

		// THEN
		assertNotNull(response);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertEquals("No hotels found with the hotel name " + hotelName, response.getBody());
	}

	@ParameterizedTest
	@NullAndEmptySource
	void shouldNotGetBookings_nullAndEmptySource(String hotelName) {
		// GIVEN

		// WHEN
		setupMockRestClient_responseBody_booking(hotelName);
		ResponseEntity<?> response = bookingController.getAllBookings(hotelName);

		// THEN
		assertNotNull(response);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("Please, enter a hotel name to check.", response.getBody());	
	}
	
	@Test
	void shouldFailGetBookings_errorFromRestClient_hotelServiceUnavailable() {
		// GIVEN
		String hotelName = "something";
		
		// WHEN
		setupMockRestClient_statusCodeOnly(GET, SERVICE_UNAVAILABLE);
		ResponseEntity<?> response = bookingController.getAllBookings(hotelName);
		
		// THEN
		assertNotNull(response);
		assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
		assertEquals("Hotel service unavailable.", response.getBody());	
	}

	@ParameterizedTest
	@ValueSource(strings= {GET,PUT})
	void shouldFailGetBookings_errorFromRestClient_restClientNullGetResponse(String requestMethod) {
		// GIVEN
		String hotelName = "something";
		BookingDto bookingDto = populateBookingDto(1);
		ResponseEntity<?> response = null;
		String service = "";
		
		// WHEN
		setupMockRestClient_statusCodeOnly(requestMethod, NULL);
		if(requestMethod.equals(GET)) {
			response = bookingController.getAllBookings(hotelName);
			service = "Hotel";
		} else {
			response = bookingController.postBooking(bookingDto);
			service = "Flight";
		}
		
		// THEN
		assertNotNull(response);
		assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
		assertEquals(service + " service unavailable.", response.getBody());	
	}
	
	private Booking populateBooking() {
		return new Booking(Long.valueOf(10), "Elsa Polindo", "12345678A", Long.valueOf(1), Long.valueOf(1));
	}

	private void setupMockRestClient_responseBody_booking(String hotelName) {
		RequestHeadersUriSpec getRequest = mock(RequestHeadersUriSpec.class);
		ResponseSpec response = mock(ResponseSpec.class);
		when(restClient.get()).thenReturn(getRequest);
		when(getRequest.uri(anyString())).thenReturn(getRequest);
		when(getRequest.retrieve()).thenReturn(response);
		when(response.body(HotelDto.class)).thenReturn(
				StringUtils.isBlank(hotelName) ? getExpectedHotelDto("")
						: hotelName.equals(RIU) ? getExpectedHotelDto(RIU)
						: hotelName.equals(IBIS) ? getExpectedHotelDto(IBIS) 
													: getExpectedHotelDto(""));
	}

	private HotelDto getExpectedHotelDto(String hotelName) {
		HotelDto hotelDto = switch (hotelName) {
		case RIU -> new HotelDto(Long.valueOf(1), RIU, "5 stars", BigDecimal.valueOf(123.50), true);
		case IBIS -> new HotelDto(Long.valueOf(2), IBIS, "2 stars", BigDecimal.valueOf(49.50), true);
		default -> null;
		};
		return hotelDto;
	}
	
	private void setupMockRestClient_statusCodeOnly(String requestMethod, String situation) {
		if(requestMethod.equals(GET)) {
			RequestHeadersUriSpec getRequest = mock(RequestHeadersUriSpec.class);
			if(situation.equals(NULL))
				when(restClient.get()).thenThrow(new RuntimeException());
			else {
				when(restClient.get()).thenReturn(getRequest);
				when(getRequest.uri(anyString())).thenReturn(getRequest);
				when(getRequest.retrieve()).thenThrow(situation.equals(SERVICE_UNAVAILABLE) ? new HttpServerErrorException(HttpStatus.SERVICE_UNAVAILABLE) : new HttpClientErrorException(HttpStatus.NOT_FOUND));	
			}
		} else if(requestMethod.equals(PUT)) {
			RequestBodyUriSpec putRequest = mock(RequestBodyUriSpec.class);
			if(situation.equals(NULL))
				when(restClient.put()).thenThrow(new RuntimeException());
			else {
				when(restClient.put()).thenReturn(putRequest);
				when(putRequest.uri(anyString())).thenReturn(putRequest);
				when(putRequest.exchange(ArgumentMatchers.any())).thenAnswer(invocation -> {
					return situation.equals(BAD_REQUEST) ? HttpStatus.BAD_REQUEST : HttpStatus.OK;
				});
			}
		}
	}

}
