package com.zc.prometheus;

import java.io.FileReader;
import java.io.IOException;

import io.prometheus.client.Gauge;
import io.prometheus.client.exporter.HTTPServer;
import io.prometheus.client.hotspot.DefaultExports;

import java.util.*;
import java.util.concurrent.TimeUnit;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

public class CitiesTemperatureVisualization {
	
	static final Gauge temparatureGauge = Gauge.build()
		    .name("Cities_temparature_info").help("Temparature visualization for four city"
		    		+ "[\"Chennai\", \"Kolkata\", \"Delhi\", \"Mumbai\"] from 1995 to 2019")
		    .labelNames("City", "Date", "Month", "Year")
		    .register();
	
	public static String[] cities = {"Chennai", "Kolkata", "Delhi", "Mumbai"};
	
	public static void main(String[] args) {
		DefaultExports.initialize();

		HTTPServer server = null;
		try {
			server = new HTTPServer(1230);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		String[][] darr = new String[9200][7];
		int i=-1, j=-1;
		float df;
		
		try {
	        String file = "F:\\temperature\\Cities.csv";
	        FileReader filereader = new FileReader(file );
	  
	        CSVReader csvReader = new CSVReaderBuilder(filereader)
	                                  .withSkipLines(1)
	                                  .build();
	        List<String[]> allData = csvReader.readAll();
	  
	        for (String[] row : allData) {
	        	++i;
	        	j =-1;
	            for (String cell : row) {
	            	if(cell !=null)
	            		darr[i][++j] = cell.trim();
	            }
	        }
	    }
	    catch (Exception e) {
	        e.printStackTrace();
	    }
		
		for(i=0; i<darr.length; ++i) {
			for(j=0; j<7; j++) {
				
				if(j > 2 && darr[i][j] != null) {
					df = Float.parseFloat(darr[i][j]);
					System.out.println(df);
					Cities(darr[i][0], darr[i][1], darr[i][2], cities[j-3], df);
				}
			}
			
			try {
				Thread.sleep(1);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("Completed");
		try {
			TimeUnit.DAYS.sleep(1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		server.stop();
	}
	
	public static void Cities(String month, String date, String year, String city, float temparature) {
		temparatureGauge.labels(city, date, month, year).set(temparature);
	}
	
}
