package com.zc.prometheus;

import java.io.IOException;

import io.prometheus.client.Gauge;
import io.prometheus.client.exporter.HTTPServer;
import io.prometheus.client.hotspot.DefaultExports;

import java.util.Random;

public class TemparatureVisualizationRandomValues {
	
	static String appName = "CityTemparatureVisualization";

	static final Gauge temparatureGauge = Gauge.build()
		    .name(appName + "_temparature_seconds").help("Temparature for four cities visualization for every 2 seconds")
		    .labelNames("stage")
		    .register();
	
	public static String[] cities = {"Salem", "Chennai", "Coimbatore", "Namakkal"};
	static int delay = 2000;
	
	public static void main(String[] args) {
		DefaultExports.initialize();

		HTTPServer server = null;
		try {
			server = new HTTPServer(1234);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		int max = 1000;
		for(int i=0; i<max; i++) {
			temparatureGauge.labels("Total Cities").setToTime(() -> {
				for(int j=0; j<4 ;j++) {
					Cities(cities[j]);
				}
			});
			System.out.println(i);
			try {
				Thread.sleep(delay);
			} 
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		server.stop();
	}
	
	public static void Cities(String city) {
		float randTemparature = 27F + new Random().nextFloat() * (41F - 27F);
		temparatureGauge.labels(city).set(randTemparature);
	}
	
}
