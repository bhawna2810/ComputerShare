package com.stock;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.stock.Models.StockData;

@RestController
@RequestMapping("/api/stock")
public class StockController {
	
	private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }
	
	@GetMapping("/{stockSymbol}")
    public StockData getStockData(@PathVariable String stockSymbol) throws JsonProcessingException {
		return stockService.getStockData(stockSymbol);
    }
	
	@GetMapping("/export/{stockSymbol}")
	public ResponseEntity<String> exportStockData(@PathVariable String stockSymbol) throws JsonProcessingException {
	    StockData stockData = stockService.getStockData(stockSymbol);
	    return stockService.exportStockData(stockData);
	}

}
