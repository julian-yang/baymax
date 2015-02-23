package edu.ucla.cs.cs144;

import java.io.IOException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ItemServlet extends HttpServlet implements Servlet {
       
    public ItemServlet() {}

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // get parameters for basic search
		String id = preventNullString(request.getParameter("id"));
		
		// get XML data of item
		String itemXML = AuctionSearchClient.getXMLDataForItemId(id);
		
		request.setAttribute("result", itemXML);
		
		request.getRequestDispatcher("/item.jsp").forward(request, response);
    }
	
	// given a string, return empty string if it is null; otherwise, return as is
	public String preventNullString(String str) {
		if(str==null) {
			return "";
		}
		return str;
	}
}
