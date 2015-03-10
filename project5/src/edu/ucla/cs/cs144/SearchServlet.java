package edu.ucla.cs.cs144;

import java.io.IOException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SearchServlet extends HttpServlet implements Servlet {
       
    public SearchServlet() {}

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// get parameters for basic search
		String q = preventNullString(request.getParameter("q"));
		String strNumResultsToSkip = preventNullString(request.getParameter("numResultsToSkip"));
		String strNumResultsToReturn = preventNullString(request.getParameter("numResultsToReturn"));
		
		// initialize number of results to skip and return to 0
		int numResultsToSkip = 0;
		int numResultsToReturn = 0;

		// try parsing string input to int, setting number of results to 0 if string is not a number
		numResultsToSkip = stringToInt(strNumResultsToSkip);
		numResultsToReturn = stringToInt(strNumResultsToReturn);
	
		// if any int parameters are negative, set back to 0
		if(numResultsToSkip < 0) {
			numResultsToSkip = 0;
		}
		if(numResultsToReturn < 0) {
			numResultsToReturn = 0;
		}
		
		SearchResult[] results = AuctionSearchClient.basicSearch(q, numResultsToSkip, numResultsToReturn);
		request.setAttribute("results", results);
		
		// pass down request data
		request.setAttribute("q", q);
		request.setAttribute("numResultsToSkip", numResultsToSkip);
		request.setAttribute("numResultsToReturn", numResultsToReturn);
		request.setAttribute("numResultsReturned", results.length);
		// set boolean attribute to true if there are more unseen items
		request.setAttribute("hasMore", AuctionSearchClient.basicSearch(q, numResultsToSkip+numResultsToReturn, 1).length==1);
		
		request.getRequestDispatcher("/search.jsp").forward(request, response);
		
    }
	
	// given a string, convert to int, defaulting to 0 if exception caught
	public int stringToInt(String str) {
		int result = 0;
		try {
			result = Integer.parseInt(str);
		} catch (NumberFormatException e) {
			result = 0;
		}
		return result;
	}
	
	// given a string, return empty string if it is null; otherwise, return as is
	public String preventNullString(String str) {
		if(str==null) {
			return "";
		}
		return str;
	}
}
