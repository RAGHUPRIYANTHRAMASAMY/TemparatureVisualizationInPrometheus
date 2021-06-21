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

public class ChennaiTemperature {
	
	static String task = "CityTemparatureVisualization";

	static final Gauge temparatureGauge = Gauge.build()
		    .name(task + "_temparature_chennai").help("Temparature visualization for chennai city from 1995 to 2019")
		    .labelNames("Month","Year","City")
		    .register();
  
	public static void main(String[] args) {
		DefaultExports.initialize();

		HTTPServer server = null;
		try {
			server = new HTTPServer(1237);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		String[][] darr = new String[9000][4];
		int i=-1, j=-1;
		float df;
		
		try {
	        String file = "F:\\temperature\\Chennai.csv";
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
			for(j=0; j<4; j++) {
				
				if(j > 2 && darr[i][j] != null) {
					df = Float.parseFloat(darr[i][j]);
					System.out.println(df);
					Cities(darr[i][0], darr[i][2], "Chennai", df);
				}
			}
			
			try {
				Thread.sleep(1000);
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
	
	public static void Cities(String month ,String year ,String city, float temparature) {
		temparatureGauge.labels(month, year, city).set(temparature);
	}
	
}
