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
		
		//SELECT ItemID, MBRContains(GeomFromText('Polygon((33.774 -118.63, 33.774 -117.38, 34.201 -117.38, 34.201 -118.63, 33.774 -118.63))'), Position) AS isContained FROM Locations WHERE ItemID IN (SELECT ItemID FROM Items WHERE Description LIKE '%camera%' AND ItemID<1496912345);

		int index = 0; // tracks search result returned from first running basic search
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
		// TODO: Your code here!
		return "";
	}
		
	public Document getDocument(int docId) throws IOException {
        return searcher.doc(docId);
    }
	
	public String getPolygon(double lx, double ly, double rx, double ry) {
		return "GeomFromText('Polygon((" + lx + " " + ly + ", " + lx + " " + ry + ", " + rx + " " + ry + ", " + rx + " " + ly + ", " + lx + " " + ly +  "))')";
	}
	
	public String echo(String message) {
		return message;
	}

}
