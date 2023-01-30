package com.stock.Models;

import java.util.List;

public class StockData {
	
	private String stockName;
	private List<StockWeeklyData> data;
	
	public StockData() {
	}
	
	

	public StockData(String stockName, List<StockWeeklyData> data) {
		super();
		this.stockName = stockName;
		this.data = data;
	}



	public String getStockName() {
		return stockName;
	}
	
	public List<StockWeeklyData> getData() {
		return data;
	}



	

}
