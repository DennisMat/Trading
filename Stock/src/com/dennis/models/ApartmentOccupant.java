package com.dennis.models;

import java.time.LocalDate;

public class ApartmentOccupant {
	
	public long apartment_id;
	public long individual_id;
	public LocalDate  date_from;
	public LocalDate date_to;
	public ApartmentOccupant(long apartment_id,  long individual_id, LocalDate date_from,
			LocalDate date_to) {
		super();
		this.apartment_id = apartment_id;
		this.individual_id = individual_id;
		this.date_from = date_from;
		this.date_to = date_to;
	}
	

	
	
	
	

}
