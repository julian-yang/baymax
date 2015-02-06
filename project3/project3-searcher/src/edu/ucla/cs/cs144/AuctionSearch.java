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
	
	public Document getDocument(int docId) throws IOException {
        return searcher.doc(docId);
    }
	
	public SearchResult[] spatialSearch(String query, SearchRegion region,
			int numResultsToSkip, int numResultsToReturn) {
		// TODO: Your code here!
		return new SearchResult[0];
	}

	public String getXMLDataForItemId(String itemId) {
		// TODO: Your code here!
		return "";
	}
	
	public String echo(String message) {
		return message;
	}

}
