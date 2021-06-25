package com.zc.prometheus;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Scanner;

import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class ListTemperatureGivenRange {

	static String parameterValue="Cities_temparature_info";
	
	static CloseableHttpClient httpClient=HttpClients.createDefault();
	static URIBuilder uri=null;
	static HttpGet get =null;
	static CloseableHttpResponse response=null;
	
	static JSONObject jsonObject=null;
	static JSONObject metric = null;
	static JSONArray value = null;
	static JSONArray result = null;
	
	static int[] monthTotalDate = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
	static int startYear, startMonth, startDate, endYear, endMonth, endDate, eM=12, tempEndDate; 
	static String City;
	
	public static void alert() {
		System.out.println("<------------------*------------------>");
		System.out.println("Give any one of the City \"Name\" from the below list");
		System.out.println("1. Chennai\n2. Kolkata\n3. Delhi\n4. Mumbai\n");
		
		System.out.println("NOTE: You only given range of year from:");
		System.out.println("1995 to 2019");
		System.out.println("NOTE: Dont give month as Jan Dec etc.. Month are only represented by\n1,2,3,....12");
		System.out.println("Leap year also included given range according to that\n");
		System.out.println("<----------------###--------------------->\n");
	}
	
	public static void reset() {
		int[] tempMonthTotalDate = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
		monthTotalDate = tempMonthTotalDate;
		eM=12;
	}
	
	public static void isLeapYear(int year) {
		if( ((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0) ){
			if(tempEndDate != monthTotalDate[1])
	             monthTotalDate[1] = 29; 
	    }else {
	    	monthTotalDate[1] = 28;
	    }
		
	}
	
	public static void ListTemperature() {
		
		try {
			do {
				
				isLeapYear(startYear); 
				
				for(int j = startMonth; j<=eM; ++j) { // month loop
					for(int k=startDate; k<=monthTotalDate[j-1]; ++k) { // date loop
						
						parameterValue = "Cities_temparature_info{City='" + City + "',Date='"+k
								+ "',Month='"+j+ "',Year='"+startYear+"'}";
						
						uri=new URIBuilder("http://127.0.0.1:9090//api/v1/query");
			            uri.addParameter("query",parameterValue);
			            get=new HttpGet(uri.build());
			            
			            response = httpClient.execute(get);
			            String resStr= EntityUtils.toString(response.getEntity(),"UTF-8");
			            jsonObject=JSONObject.parseObject(resStr);
			            result=jsonObject.getJSONObject("data").getJSONArray("result");
			            
			            for(int l=0;l<result.size();l++){
			                
			            	JSONObject metric = result.getJSONObject(l).getJSONObject("metric");
			                value = result.getJSONObject(l).getJSONArray("value");
			                
			                System.out.println(metric.get("Date")+"\t"+metric.get("Month")+"\t"
			                +metric.get("Year")+"\t"+metric.get("City")+"\t"+value.getString(1));
			                
			            }
					}
					startDate = 1;
				}
				
				startMonth = 1;
				int t = ++startYear;
				
				if(startYear < endYear)
					startYear = t;
				else 
					startYear = 0;
				
				tempEndDate = 0;
			} while (startYear != 0);
      
		}
		catch (URISyntaxException | ParseException | IOException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("resource")
	public static void main(String[] args) throws InterruptedException {
    
		String[] errorArr = {"Year must in the range 1995 to 2019 and end year not less than start year",
				"Month must in the range 1 to 12", 
				"Date must be in the range of 1 to 31 or 1 to 29 for leap year"};
    
		boolean flag = false;
		String repeat = "N";
		int tempStartYear = 0;
    
		Scanner scan = new Scanner(System.in);
		alert();
		
		do {
			flag = false;
			System.out.println("Remember \"City name\" is \"Case Sensitive\" "
					+ "Must start with \"UpperCase\" followded by \"LowerCase\" letters\n");
      
			System.out.println("Enter City Name:");
			City = scan.next();
      
			if(City.equals("Chennai") || City.equals("Kolkata")|| City.equals("Mumbai") || City.equals("Delhi")) {
				System.out.println("Enter Starting year: ");
				startYear = scan.nextInt();
				
				if(startYear < 1995 || startYear> 2019) {
					System.out.println(errorArr[0]);
				}
				else {
					isLeapYear(startYear);
					tempStartYear = startYear;
					System.out.println("Enter starting month");
					startMonth = scan.nextInt();
					
					if(startMonth < 1 || startMonth > 12)
						System.out.println(errorArr[1]);
					else {
						System.out.println("Enter starting date");
						startDate = scan.nextInt();
						
						if( startDate < 1 || startDate > monthTotalDate[startMonth-1])
							System.out.println(errorArr[2]);
						else {
							System.out.println("Enter End Year");
							endYear = scan.nextInt();
							
							if(endYear < 1995 || endYear> 2019 || endYear < startYear) 
								System.out.println(errorArr[0]);
							else {
								System.out.println("Enter End Month");
								isLeapYear(endYear);
								endMonth = scan.nextInt();
								
								if(endMonth < 1 || endMonth > 12)
									System.out.println(errorArr[1]);
								else {
									System.out.println("Enter End date");
									endDate = scan.nextInt();
									
									if(endDate < 1 || endDate > monthTotalDate[endMonth-1] )
										System.out.println(errorArr[2]);
									else {
										if(startYear == endYear && startMonth == endMonth && endDate < startDate)
											System.out.println("\nYou entered same month and year\nThen end date cannot be less than start date\n");
										else
											flag = true;
									}
								}
							}
						}
					}
				}
				
				if(flag) {
					System.out.println(City+": "+startDate+"/"+startMonth+"/"+startYear+" to "
							+endDate+"/"+endMonth+"/"+endYear);
					
					Thread.sleep(1000);
					
					System.out.println("\nDate"+"\t"+"Month"+"\t"+"Year"+"\t"+"City"+"\t"+"Temperature");
					
					if(endYear == startYear) {
						eM = endMonth;
						monthTotalDate[eM-1] = endDate;
						tempEndDate = endDate;
					}
					
					ListTemperature();
					reset();
					
					if(endYear != tempStartYear) {
						startMonth = 1;
						startDate = 1;
						eM = endMonth;
						monthTotalDate[eM-1] = endDate;
						tempEndDate = endDate;
						startYear = endYear;
						ListTemperature();
					}
					reset();
				}
        
			}
			else
				System.out.println("Entered City, not in the list. Also check, \"City Name\" is Case Sensitive");
			
			System.out.println("<--------------------*---------------->");
			System.out.println("Enter 'Y' to Continue or 'N' to Terminate");
			
			repeat = scan.next();
      
		} while (repeat.toUpperCase().equals("Y"));
	}
}
