package com.cloudera.flink.examples;

import java.io.Serializable;

public class CovidData implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	long eventTimeLong;
	long confirmed;
	String country_code;
	long dead;
	double latitude;
	String location;
	double longitude;
	long recovered;
	long velocity_confirmed;
	long velocity_dead;
	long velocity_recovered;
	String  updated_date;
	


	public long getEventTimeLong() {
		return eventTimeLong;
	}

	public void setEventTimeLong(long eventTimeLong) {
		this.eventTimeLong = eventTimeLong;
	}

	public long getConfirmed() {
		return confirmed;
	}

	public void setConfirmed(long confirmed) {
		this.confirmed = confirmed;
	}

	public String getCountry_code() {
		return country_code;
	}

	public void setCountry_code(String country_code) {
		this.country_code = country_code;
	}

	public long getDead() {
		return dead;
	}

	public void setDead(long dead) {
		this.dead = dead;
	}



	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}



	public long getRecovered() {
		return recovered;
	}

	public void setRecovered(long recovered) {
		this.recovered = recovered;
	}

	public long getVelocity_confirmed() {
		return velocity_confirmed;
	}

	public void setVelocity_confirmed(long velocity_confirmed) {
		this.velocity_confirmed = velocity_confirmed;
	}

	public long getVelocity_dead() {
		return velocity_dead;
	}

	public void setVelocity_dead(long velocity_dead) {
		this.velocity_dead = velocity_dead;
	}

	public long getVelocity_recovered() {
		return velocity_recovered;
	}

	public void setVelocity_recovered(long velocity_recovered) {
		this.velocity_recovered = velocity_recovered;
	}

	public String getUpdated_date() {
		return updated_date;
	}

	public void setUpdated_date(String updated_date) {
		this.updated_date = updated_date;
	}

	@Override
	public String toString() {
		return "CovidData [eventTimeLong=" + eventTimeLong + ", confirmed=" + confirmed + ", country_code="
				+ country_code + ", dead=" + dead + ", latitude=" + latitude + ", location=" + location + ", longitude="
				+ longitude + ", recovered=" + recovered + ", velocity_confirmed=" + velocity_confirmed
				+ ", velocity_dead=" + velocity_dead + ", velocity_recovered=" + velocity_recovered + ", updated_date="
				+ updated_date + "]";
	}
	
	
//	public static CovidData fromString(String eventStr) {
//        String[] split = eventStr.split(",");
//        return new KafkaEvent(split[0], Integer.valueOf(split[1]), Long.valueOf(split[2]));
//    }
}
