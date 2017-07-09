package it.polito.tdp.formulaone.model;

import java.time.Year;

public class Season {

	private Year year ;
	private String url ;
	
	public Season(Year year, String url) {
		super();
		this.year = year;
		this.url = url;
	}
	public Year getYear() {
		return year;
	}
	public void setYear(Year year) {
		this.year = year;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	@Override
	public String toString() {
		return "Stagione " + year;
	}
	
}
