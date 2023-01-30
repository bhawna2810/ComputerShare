package com.stock;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stock.Models.StockData;
import com.stock.Models.StockWeeklyData;

@Service
public class StockService {
	
	private final String alphaVantageApiKey;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    @Autowired
    public StockService(@Value("${alphaVantage.apiKey}") String alphaVantageApiKey, 
                        ObjectMapper objectMapper, 
                        RestTemplate restTemplate) {
        this.alphaVantageApiKey = alphaVantageApiKey;
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
    }
    
    public StockData getStockData(String stockSymbol) throws JsonProcessingException {
        String url = "https://www.alphavantage.co/query?function=TIME_SERIES_WEEKLY&symbol="+stockSymbol+"&apikey="+alphaVantageApiKey;
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            Map<String, Object> jsonMap = objectMapper.readValue(response.getBody(), new TypeReference<Map<String, Object>>() {});
            Map<String, Object> weeklyData = (Map<String, Object>) jsonMap.get("Weekly Time Series");
            Set<String> keys =  weeklyData.keySet();
            List<StockWeeklyData> weeklyDataList = new ArrayList();
            for (String date : keys) {
            	Map<String, Object> data = (Map<String, Object>)weeklyData.get(date);
                double max = Double.parseDouble((String) data.get("2. high"));
                double min = Double.parseDouble((String) data.get("3. low"));
                double avg = Double.parseDouble((String) data.get("4. close"));
            	StockWeeklyData wd = new StockWeeklyData(date, max, min, avg);
            	weeklyDataList.add(wd);                
                
            }
            
            StockData stockData = new StockData(stockSymbol, weeklyDataList);
            return stockData;
        } else {
            throw new RuntimeException("Failed to retrieve stock data from Alpha Vantage API: " + response.getStatusCode());
        }
    }
    
    
    public ResponseEntity<String> exportStockData(StockData stockData) throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(stockData);
        String fileName = stockData.getStockName() + ".json";
		/* 
		 * try(FileWriter fileWriter = new FileWriter(fileName)){
		 * fileWriter.write(json); } catch (IOException e) { throw new
		 * RuntimeException("Failed to export stock data to JSON file", e); }
		 */
        
        
        return ResponseEntity.ok()
        		.contentLength(json.length())
        		.contentType(MediaType.APPLICATION_JSON_UTF8)
        		.header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName)
        		.body(json);
        		
        		
    }
        	

}
