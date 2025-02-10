package domain.api;

import java.util.List;

import domain.model.Hotel;
import domain.spi.HotelRepositoryPort;

public class HotelServiceImpl implements HotelService {

	private HotelRepositoryPort hotelRepositoryPort;
	
	public HotelServiceImpl(HotelRepositoryPort hotelRepositoryPort) {
		this.hotelRepositoryPort = hotelRepositoryPort;
	}
	
	@Override
	public List<Hotel> getAllHotels() {
		return hotelRepositoryPort.findAll();
	}

	@Override
	public Hotel findByName(String hotelName) {
		return hotelRepositoryPort.findByName(hotelName);
	}

}
