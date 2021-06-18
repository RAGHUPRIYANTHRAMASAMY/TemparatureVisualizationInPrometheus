package com.zc.prometheus;

import java.io.FileReader;
import java.io.IOException;

import io.prometheus.client.Gauge;
import io.prometheus.client.exporter.HTTPServer;
import io.prometheus.client.hotspot.DefaultExports;

import java.util.*;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

public class TemparatureVisualizaionPerDay {

	static String task = "CityTemparatureVisualization";

	static final Gauge temparatureGauge = Gauge.build()
		    .name(task + "_temparature_perday").help("Temparature visualization for four cities per day")
		    .labelNames("stage")
		    .register();
	
	public static String[] cities = {"Chennai", "Kolkata", "Delhi", "Mumbai"};
	static Long delay = (long) (24*60*60);
	
	public static void main(String[] args) {
		DefaultExports.initialize();

		HTTPServer server = null;
		try {
			server = new HTTPServer(1235);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		String[][] darr = new String[9000][7];
		int i=-1, j=-1;
		float df;
		
		try {
	        String file = "F:\\temperature\\DifferentCities.csv";
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
				
				if(j>2 && darr[i][j] != null) {
					df = Float.parseFloat(darr[i][j]);
					System.out.println(df);
					Cities(cities[j-3], df);
				}
			}
			
			try {
				Thread.sleep(delay);
			} 
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		server.stop();
	}
	
	public static void Cities(String city, float temparature) {
		temparatureGauge.labels(city).set(temparature);
	}
	
}
