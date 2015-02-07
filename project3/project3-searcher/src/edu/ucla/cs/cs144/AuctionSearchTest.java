package edu.ucla.cs.cs144;

import java.util.Calendar;
import java.util.Date;

import edu.ucla.cs.cs144.AuctionSearch;
import edu.ucla.cs.cs144.SearchRegion;
import edu.ucla.cs.cs144.SearchResult;

public class AuctionSearchTest {
	public static void main(String[] args1)
	{
		AuctionSearch as = new AuctionSearch();

		String message = "Test message";
		String reply = as.echo(message);
		System.out.println("Reply: " + reply);
		
		String query = "superman";
		SearchResult[] basicResults = as.basicSearch(query, 0, 20);
		System.out.println("Basic Search Query: " + query);
		System.out.println("Received " + basicResults.length + " results");
		for(SearchResult result : basicResults) {
			System.out.println(result.getItemId() + ": " + result.getName());
		}
		
		// superman, number of basic results
		basicResults = as.basicSearch("superman", 0, 9999);
		System.out.println("Received " + basicResults.length + " results for superman");
		
		// kitchenware, number of basic results
		basicResults = as.basicSearch("kitchenware", 0, 9999);
		System.out.println("Received " + basicResults.length + " results for kitchenware");

		// star trek, number of basic results
		basicResults = as.basicSearch("star trek", 0, 9999);
		System.out.println("Received " + basicResults.length + " results for star trek");

		// camera, number of basic results
		basicResults = as.basicSearch("camera", 0, 9999);
		System.out.println("Received " + basicResults.length + " camera");
		
		// camera, print first 3 results
		basicResults = as.basicSearch("camera", 0, 3);
		System.out.println("Received " + basicResults.length + " camera");
		for(SearchResult result : basicResults) {
			System.out.println(result.getItemId() + ": " + result.getName());
		}
		
		// camera, print 3 results, skipping first 2
		basicResults = as.basicSearch("camera", 2, 3);
		System.out.println("Received " + basicResults.length + " camera");
		for(SearchResult result : basicResults) {
			System.out.println(result.getItemId() + ": " + result.getName());
		}
		
		// spatial search on camera
		SearchRegion region =
		    new SearchRegion(33.774, -118.63, 34.201, -117.38); 
		SearchResult[] spatialResults = as.spatialSearch("camera", region, 0, 20);
		System.out.println("Spatial Search");
		System.out.println("Received " + spatialResults.length + " results");
		for(SearchResult result : spatialResults) {
			System.out.println(result.getItemId() + ": " + result.getName());
		}
		
		// xml test
		String itemId = "1497595357";
		String item = as.getXMLDataForItemId(itemId);
		System.out.println("XML data for ItemId: " + itemId);
		System.out.println(item);

		// Add your own test here
	}
}
