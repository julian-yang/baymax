package edu.ucla.cs.cs144;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.MalformedURLException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.PrintWriter;

public class ProxyServlet extends HttpServlet implements Servlet {
       
    public ProxyServlet() {}

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// get parameters for basic search
		String q = preventNullString(request.getParameter("q"));
		
		// make sure parameters are not equal (Google returns an error for "", but not for " ")
		if(q.equals("")) {
			q = " ";
		}
		
		try {
			
			// set up URL and HttpURLConnection
			URL url = new URL("http://google.com/complete/search?output=toolbar&q=" + q);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("GET");
			
			// check that connection is established
			if(conn.getResponseCode() == HttpURLConnection.HTTP_OK) {

				// reconstruct XML data using BufferedReader (from InputStreamReader, from InputStream)
				BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				String message = "";
				String line = "";
				while((line = br.readLine()) != null) {
					message += line;
				}
				
                PrintWriter out = response.getWriter();
                out.println(message);
				// set XML data to suggestions on JSP
				//request.setAttribute("suggestions", message);
				
				// close reader and connection
				br.close();
			}
			
		} catch (MalformedURLException e) {
			// do nothing
		} catch (IOException e) {
			// do nothing
		}

		//request.getRequestDispatcher("/search.jsp").forward(request, response);
        response.setContentType("text/xml");
    }
	
	// given a string, return empty string if it is null; otherwise, return as is
	public String preventNullString(String str) {
		if(str==null) {
			return "";
		}
		return str;
	}
	
}
