package com.cloudera.flink.examples;

import java.io.Serializable;

public class CovidFilteredData implements Serializable{

	
	
	String country_code;
	double longitude;
	double latitude;
	String location;
	long dead;
	
	
	
	long recovered;
	String country_name;
	public String getCountry_code() {
		return country_code;
	}
	public void setCountry_code(String country_code) {
		this.country_code = country_code;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public long getDead() {
		return dead;
	}
	public void setDead(long dead) {
		this.dead = dead;
	}
	public long getRecovered() {
		return recovered;
	}
	public void setRecovered(long recovered) {
		this.recovered = recovered;
	}
	public String getCountry_name() {
		return country_name;
	}
	public void setCountry_name(String country_name) {
		this.country_name = country_name;
	}
	@Override
	public String toString() {
		return "CovidFilteredData [country_code=" + country_code + ", longitude=" + longitude + ", latitude=" + latitude
				+ ", location=" + location + ", dead=" + dead + ", recovered=" + recovered + ", country_name="
				+ country_name + "]";
	}
	


	

}
