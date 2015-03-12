package edu.ucla.cs.cs144;

import java.io.IOException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import java.io.StringReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class ConfirmServlet extends HttpServlet implements Servlet {
    
    public ConfirmServlet() {}

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        Object item = session.getAttribute("ItemResult");
        request.setAttribute("ItemResult", item);
        request.setAttribute("isSecure", request.isSecure());
		
        //request.setAttribute("isSecure", request.isSecure());
        //String itemLink = "http://" + request.getServerName() + ":1448/eBay/item?id=";
        //request.setAttribute("itemLink", itemLink);
        
		// set credit card attribute
		String cc = request.getParameter("creditCard");
		request.setAttribute("creditCard", cc);
		
		// set current date and time attribute
		DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		Date date = new Date();
		request.setAttribute("transactionTime", dateFormat.format(date));
		
		// remove item from session; it's already purchased
		session.removeAttribute("ItemResult");
		
		// going back to home page uses unencrypted HTTP
		String homeLink = "http://" + request.getServerName() + ":1448/eBay";
		request.setAttribute("homeLink", homeLink);
		
        request.getRequestDispatcher("/confirm.jsp").forward(request, response);
    }
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
        response.sendRedirect("");
    }
	
}
