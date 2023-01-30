package com.stock;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;

import org.assertj.core.util.Arrays;
import org.junit.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.stock.Models.StockData;
import com.stock.Models.StockWeeklyData;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StockServiceTest {
	
	@Autowired
    private StockService stockService;

    private MockWebServer mockWebServer;

    @BeforeAll
    public void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        //mockWebServer.start(8082);
    }

    @AfterAll
    public void tearDown() throws IOException {
        mockWebServer.shutdown();
    }
    
    
    @Test
    public void getStockData_whenApiReturnsSuccess_shouldReturnStockData() throws Exception {
        String jsonResponse = "{\"Meta Data\":{\"1. Information\":\"Weekly Prices (open, high, low, close) and Volumes\",\"2. Symbol\":\"VOD\",\"3. Last Refreshed\":\"2022-11-28\",\"4. Time Zone\":\"US/Eastern\"},\"Weekly Time Series\":{\"2022-11-28\":{\"1. open\":\"30.0000\",\"2. high\":\"35.0000\",\"3. low\":\"25.0000\",\"4. close\":\"28.0000\",\"5. volume\":\"10000\"}}}";

        mockWebServer.enqueue(new MockResponse().setBody(jsonResponse));

        StockData stockData = stockService.getStockData("VOD");
        StockWeeklyData weeklyData = new StockWeeklyData("2022-11-28", 35.0,25.0,28.0);

        assertEquals("VOD", stockData.getStockName());
        assertEquals(weeklyData.getDate(), stockData.getData().get(0).getDate());
        assertEquals(weeklyData.getMaximumPrice(), stockData.getData().get(0).getMaximumPrice());
        assertEquals(weeklyData.getMinimumPrice(), stockData.getData().get(0).getMinimumPrice());
        assertEquals(weeklyData.getAveragePrice(), stockData.getData().get(0).getAveragePrice());
        
    }
    
    @Test
    public void exportStockData_whenStockDataIsNotNull_shouldCreateJsonFile() throws Exception {
        
        StockWeeklyData weeklyData = new StockWeeklyData("2022-11-28", 35.0,25.0,28.0);
        StockData stockData = new StockData("VOD", new ArrayList(Arrays.asList(weeklyData))); 
        String json = "{\"stockName\":\"VOD\"}";
        
        ResponseEntity<String> response = stockService.exportStockData(stockData);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(json.length(), response.getHeaders().getContentLength());
        assertEquals(MediaType.APPLICATION_JSON_UTF8, response.getHeaders().getContentType());
        assertEquals("attachment;filename=VOD.json", response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION));
        assertEquals(json, response.getBody());

        
    }

}
