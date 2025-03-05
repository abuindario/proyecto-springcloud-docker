package application.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import domain.api.HotelService;
import domain.model.Hotel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@CrossOrigin
public class HotelController {
	
	private HotelService hotelService;
	
	public HotelController(HotelService hotelService) {
		this.hotelService = hotelService;
	}

	@Operation(summary="Find all Hotels", description="This method returns a ResponseEntity object that includes in its body a list of all Hotels registered.")
	@GetMapping("/hotels")
	public ResponseEntity<List<Hotel>> getAllHotels() {
		return ResponseEntity.ok(hotelService.getAllHotels());
	}

	@Operation(summary="Find a Hotel filtering by its name",
			responses= {@ApiResponse(responseCode = "200", description="This method returns a ResponseEntity object that includes in its body all the details of a specific Hotel."),
						@ApiResponse(responseCode = "404", description="404 error will be returned if no Hotel was found with the given name.", content=@Content())})
	@GetMapping("/hotels/{name}")
	public ResponseEntity<Hotel> findByName(@PathVariable("name") String hotelName) {
			return ResponseEntity.ofNullable(hotelService.findByName(hotelName));
	}
}
