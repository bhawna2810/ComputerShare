package com.stock.Models;

public class StockWeeklyData {
	private String date;
	private double maximumPrice;
	private double minimumPrice;
	private double averagePrice;
	
	public StockWeeklyData(String date, double maximumPrice, double minimumPrice, double averagePrice) {
		super();
		this.date = date;
		this.maximumPrice = maximumPrice;
		this.minimumPrice = minimumPrice;
		this.averagePrice = averagePrice;
	}
	
	public String getDate() {
		return date;
	}
	public double getMaximumPrice() {
		return maximumPrice;
	}
	public double getMinimumPrice() {
		return minimumPrice;
	}
	public double getAveragePrice() {
		return averagePrice;
	}
	

}
