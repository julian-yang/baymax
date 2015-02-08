package edu.ucla.cs.cs144;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.util.Date;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.lucene.document.Document;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import edu.ucla.cs.cs144.DbManager;
import edu.ucla.cs.cs144.SearchRegion;
import edu.ucla.cs.cs144.SearchResult;

public class AuctionSearch implements IAuctionSearch {

	/* 
         * You will probably have to use JDBC to access MySQL data
         * Lucene IndexSearcher class to lookup Lucene index.
         * Read the corresponding tutorial to learn about how to use these.
         *
	 * You may create helper functions or classes to simplify writing these
	 * methods. Make sure that your helper functions are not public,
         * so that they are not exposed to outside of this class.
         *
         * Any new classes that you create should be part of
         * edu.ucla.cs.cs144 package and their source files should be
         * placed at src/edu/ucla/cs/cs144.
         *
         */
	
	private IndexSearcher searcher = null;
    private QueryParser parser = null;
	
    public AuctionSearch() {
		try {
			searcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(new File("/var/lib/lucene/index1"))));
			parser = new QueryParser("content", new StandardAnalyzer());
		} catch (IOException ex) {
			System.out.println(ex);
		}
    }
	
	public SearchResult[] basicSearch(String query, int numResultsToSkip, 
			int numResultsToReturn) {
		
		try {
			// parse the query
			Query queryObj = parser.parse(query);
			
			// obtain document results from query using index searcher; find all documents up to the last requested one
			TopDocs results = searcher.search(queryObj, numResultsToSkip + numResultsToReturn);
			
			// use TopDocs to find total hits found and the array of ScoreDoc objects
			int totalHits = results.totalHits;
			ScoreDoc[] docs = results.scoreDocs;
			
			// if results to return tries to reach beyond total hits available, set a cap
			// numResultsToReturn should be either the remaining available documents above numResultsToSkip
			// or in the case where numResultsToSkip exceeds totalHits, it should return nothing
			if(totalHits < (numResultsToSkip + numResultsToReturn))
				numResultsToReturn = Math.max(0, totalHits - numResultsToSkip);
			
			// initialize SearchResult array to have expected number of results
			SearchResult[] searchResults = new SearchResult[numResultsToReturn];
			
			// populate the array with corresponding SearchResult objects
			for(int i=0; i<searchResults.length; i++) {
				Document temp = getDocument(docs[numResultsToSkip + i].doc);
				searchResults[i] = new SearchResult(temp.get("ItemID"), temp.get("Name"));
			}
			
			// return results
			return searchResults;
			
		} catch (ParseException ex) {
			System.out.println(ex);
		} catch (IOException ex) {
			System.out.println(ex);
		}
		
		// otherwise, return no results
		return new SearchResult[0];
	}
	
	public SearchResult[] spatialSearch(String query, SearchRegion region,
			int numResultsToSkip, int numResultsToReturn) {
		
		// Sample Query:
		// SELECT ItemID, MBRContains(GeomFromText('Polygon((33.774 -118.63, 33.774 -117.38, 34.201 -117.38, 34.201 -118.63, 33.774 -118.63))'), Position) AS isContained FROM Locations WHERE ItemID IN (SELECT ItemID FROM Items WHERE Description LIKE '%camera%' AND ItemID<1496912345);

		int index = 0; // tracks search results returned from first running basic search
		int skipped = 0; // tracks number of basic search results in region that were skipped
		int added = 0; // tracks number of basic search results in region that were added to array list
		
		ArrayList<SearchResult> resultsList = new ArrayList<SearchResult>();
		
		// keep fetching results that satisfy the basic query search
		SearchResult[] temp = basicSearch(query, index, numResultsToReturn);

		// establish a connection with database
		Connection conn = null;
		
		try {
			// start database connection
			conn = DbManager.getConnection(true);
			
			// create the string for a MySQL geometric polygon for parameter region
			String polygon = getPolygon(region.getLx(), region.getLy(), region.getRx(), region.getRy());
			
			// prepared statement to test a specific item (by ItemID) for spatial containment in polygon region
			PreparedStatement checkContains = conn.prepareStatement("SELECT MBRContains(" + polygon + ",Position) AS isContained FROM Locations WHERE ItemID = ?");
			
			// as long as we haven't added the numResultsToReturn and basic search still returns results
			while(added < numResultsToReturn && temp.length > 0) {

				//System.out.println("index/temp.length/added/skipped: " + index + "/" + temp.length + "/" + added + "/" + skipped);
				
				for(int i=0; i<temp.length; i++) {
					
					// get SearchResult's ItemId, plug it into prepared statement, and check if the item is spatially found in region
					String itemId = temp[i].getItemId();
					checkContains.setString(1, itemId);
					ResultSet containsRS = checkContains.executeQuery();
										
					// if specific item is found in Locations table and is contained in region
					if(containsRS.next() && containsRS.getBoolean("isContained")) {
						if(added >= numResultsToReturn) {
							// enough results have been added to array list, so break out of loop
							break;
						}
						if(skipped >= numResultsToSkip) {
							// we've skipped enough results, so add SearchResult to array list
							added++;
							resultsList.add(temp[i]);
						}
						else {
							// skip and don't add to array list, but increment the skipped count
							skipped++;
						}
					}
					
					// close containsRS
					containsRS.close();
				}
				
				// lookup the next numResultsToReturn amount of basic search results to continue comparing
				index += numResultsToReturn;
				temp = basicSearch(query, index, numResultsToReturn);
			}
			
			// close statements and database connection
			checkContains.close();
			conn.close();
			
		} catch (SQLException ex) {
			System.out.println(ex);
		}
		
		// move results from dynamic array list over to array
		// SearchResult[] searchResults = resultsList.toArray(new SearchResult[added]);
		SearchResult[] searchResults = new SearchResult[added];
		for(int i=0; i<added; i++) {
			searchResults[i] = resultsList.get(i);
		}
		
		return searchResults;
	}

	public String getXMLDataForItemId(String itemId) {
		
		// establish a connection with database
		Connection conn = null;
		
		try {
			// start database connection
			conn = DbManager.getConnection(true);

			// statement for querying, such as grabbing an item's details using ItemID
			Statement itemStatement = conn.createStatement();
			Statement sellerStatement = conn.createStatement();
			Statement categoryStatement = conn.createStatement();
			Statement bidStatement = conn.createStatement();
			Statement userStatement = conn.createStatement();
			
			ResultSet itemRS = itemStatement.executeQuery("SELECT * FROM Items WHERE ItemID = " + itemId);
			
			// if such an item exists, get its information
			if(itemRS.next()) {
				
				// get all basic information from Items table
				String name = escapeString(itemRS.getString("Name"));
				String buyPrice = escapeString(itemRS.getString("BuyPrice"));
				String firstBid = escapeString(itemRS.getString("FirstBid"));
				String started = escapeString(formatDate(itemRS.getString("Started")));
				String ends = escapeString(formatDate(itemRS.getString("Ends")));
				String latitude = escapeString(itemRS.getString("Latitude"));
				String longitude = escapeString(itemRS.getString("Longitude"));
				String location = escapeString(itemRS.getString("Location"));
				String country = escapeString(itemRS.getString("Country"));
				String description = escapeString(itemRS.getString("Description"));
				String sellerId = escapeString(itemRS.getString("SellerID"));

				// initialize current bid price to first bid
				String currently = escapeString(firstBid);
				
				// initialize seller's rating, then do a query on Users to get the actual value based on sellerId
				String sellerRating = "";
				ResultSet sellerRS = sellerStatement.executeQuery("SELECT * FROM Users WHERE UserID = '" + sellerId + "'");
				if(sellerRS.next()) {
					sellerRating = escapeString(sellerRS.getString("sellerRating"));
				}
				sellerRS.close();
				
				// add all associated categories to array list
				ArrayList<String> categories = new ArrayList<String>();
				ResultSet categoryRS = categoryStatement.executeQuery("SELECT * FROM ItemCategories WHERE ItemID = " + itemId);
				while(categoryRS.next()) {
					categories.add(escapeString(categoryRS.getString("Category")));
				}
				categoryRS.close();

				
				// add all bids on item to array list, ordered from earliest to latest
				ArrayList<String> bids = new ArrayList<String>();
				ResultSet bidRS = bidStatement.executeQuery("SELECT * FROM Bids WHERE ItemID = " + itemId + " ORDER BY Time ASC");
				while(bidRS.next()) {
					String bidderId = escapeString(bidRS.getString("BidderID"));
					String time = escapeString(formatDate(bidRS.getString("Time")));
					String amount = escapeString(bidRS.getString("Amount"));
					
					// get bidder information from Users table using their user id
					// pre-wrap each "bid" in tags, encompassed by <Bid>...</Bid>
					ResultSet userRS = userStatement.executeQuery("SELECT * FROM Users WHERE UserID = '" + bidderId + "'");

					if(userRS.next()) {
						String bidderRating = escapeString(userRS.getString("BuyerRating"));
						String bidderLocation = escapeString(userRS.getString("Location"));
						String bidderCountry = escapeString(userRS.getString("Country"));
						
						String bid = "<Bid>\n";
						
						// add bidder information
						bid += "<Bidder Rating=\"" + bidderRating + "\" UserID=\"" + bidderId + "\">\n";
						bid += "<Location>" + bidderLocation + "</Location>\n";						
						bid += "<Country>" + bidderCountry + "</Country>\n";
						bid += "</Bidder>\n";
						
						// add the rest of the bid information
						bid += "<Time>" + time + "</Time>\n";
						bid += "<Amount>$" + amount + "</Amount>\n";
						
						bid += "</Bid>\n";
						
						bids.add(bid);

						// update current price to latest bid amount (since the last bid is the largest in value)
						currently = amount;
						
					}
					userRS.close();
				}
				
				bidRS.close();

				// close result sets, statements and database connection
				itemRS.close();
				
				itemStatement.close();
				sellerStatement.close();
				categoryStatement.close();
				bidStatement.close();
				userStatement.close();
				
				conn.close();
				
				// pass all escaped values into buildXML and return the string result
				return buildXML(itemId, name, categories, buyPrice, currently, firstBid, bids, latitude, longitude, location, country, started, ends, sellerRating, sellerId, description);
			}
			else {
				
				// close result sets, statements and database connection
				itemRS.close();
				itemStatement.close();
				conn.close();
				
				// no such ItemID found, so return empty string
				return "";
			}
		} catch (SQLException ex) {
			System.out.println(ex);
		}
		
		// if any errors, return empty string
		return "";
	}
	
	// given document ID, return corresponding document from IndexSearcher
	public Document getDocument(int docId) throws IOException {
        return searcher.doc(docId);
    }
	
	// given lx, ly, rx, ry (bottom-left coordinates and top-right coordinates, respectively), generate a MySQL polygon region
	public String getPolygon(double lx, double ly, double rx, double ry) {
		return "GeomFromText('Polygon((" + lx + " " + ly + ", " + lx + " " + ry + ", " + rx + " " + ry + ", " + rx + " " + ly + ", " + lx + " " + ly +  "))')";
	}
	
	// given a MySQL Timestamp as a string, reformat it into original XML date format
	public String formatDate(String dateString) {
		
		// if date is null, don't try to format it
		if(dateString==null || dateString.isEmpty()) {
			return dateString;
		}
				
		// set up date formats
		SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat outputFormat = new SimpleDateFormat("MMM-dd-yy HH:mm:ss");	
		
		// parse the date string if possible; leave it unchanged otherwise
		try {
			// parse in string using input format to create a Date object
			Date date = inputFormat.parse(dateString);
			
			// output Date object using output format and store as string
			dateString = outputFormat.format(date);
		} catch(Exception ex) {
			System.out.println(ex);
		}
		
		return dateString;
	}
	
	// replace all occurrences of '&', '"', ''', '<', and '>' with their &_; counterparts
	public String escapeString(String input) {
		
		// if input is null, don't try to escape it
		if(input==null || input.isEmpty()) {
			return input;
		}
		
		return input.replaceAll("&", "&amp;").replaceAll("\"", "&quot;").replaceAll("'", "&apos;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
	}
	
	// reconstruct XML using only given parameters
	public String buildXML(String itemId, String name, ArrayList<String> categories, String buyPrice, String currently, String firstBid, ArrayList<String> bids,
		String latitude, String longitude, String location, String country, String started, String ends, String sellerRating, String sellerId, String description) {
		
		String xml = "<Item ItemID=\"" + itemId + "\">\n";
		
		xml += "<Name>" + name + "</Name>\n";
		
		// create category tags for every single category
		for(String category : categories) {
			xml += "<Category>" + category + "</Category>\n";
		}
		
		xml += "<Currently>$" + currently + "</Currently>\n";

		// if buy price is provided, add tags
		if(buyPrice != null && !buyPrice.isEmpty()) {
			xml += "<Buy_Price>$" + buyPrice + "</Buy_Price>\n";
		}

		xml += "<First_Bid>$" + firstBid + "</First_Bid>\n";

		xml += "<Number_of_Bids>" + bids.size() + "</Number_of_Bids>\n";

		// if no bids, return empty bids tag; otherwise, add all pre-generated bid XML to <Bids>...</Bids>
		if(bids.size()==0) {
			xml += "<Bids />\n";
		}
		else {
			xml += "<Bids>\n";
			
			for(String bid : bids) {
				xml += bid;
			}
			
			xml += "</Bids>\n";
		}
		
		// if latitude and longitude are provided, add them as attributes to the item's location
		if(latitude != null && !latitude.isEmpty() && longitude != null && !longitude.isEmpty()) {
			xml += "<Location Latitude=\"" + latitude + "\" Longitude=\"" + longitude + "\">" + location + "</Location>\n";
		}
		else {
			xml += "<Location>" + location + "</Location>\n";
		}

		xml += "<Country>" + country + "</Country>\n";
		
		xml += "<Started>" + started + "</Started>\n";
		
		xml += "<Ends>" + ends + "</Ends>\n";
		
		xml += "<Seller Rating=\"" + sellerRating + "\" UserID=\"" + sellerId + "\" />\n";
		
		// if description is provided, add them between tags; otherwise, return empty tag
		if(description != null & !description.isEmpty()) {
			xml += "<Description>" + description + "</Description>\n";
		}
		else {
			xml += "<Description />\n";
		}
		
		xml += "</Item>";
		
		return xml;
	}
	
	public String echo(String message) {
		return message;
	}

}
