package com.example.developer.pharmacy.service.dto;


/**
 * A DTO representing an pharmacyAlert using in kafka producer.
 */
public class PharmacyAlertDTO {

	private String pharmacyName;

	private String pharmacyStatus;

	public PharmacyAlertDTO(PharmacyDTO pharmacyDTO) {
		this.pharmacyName = pharmacyDTO.getName();
		this.pharmacyStatus = pharmacyDTO.getStatus().name();
	}

	public String getPharmacyName() {
		return pharmacyName;
	}

	public void setPharmacyName(String pharmacyName) {
		this.pharmacyName = pharmacyName;
	}

	public String getPharmacyStatus() {
		return pharmacyStatus;
	}

	public void setPharmacyStatus(String pharmacyStatus) {
		this.pharmacyStatus = pharmacyStatus;
	}

	@Override
	public String toString() {
		return "PharmacyAlertDTO [pharmacyName=" + pharmacyName + ", pharmacyStatus=" + pharmacyStatus + "]";
	}

	
}
