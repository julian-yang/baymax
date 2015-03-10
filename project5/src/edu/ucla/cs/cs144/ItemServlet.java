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

public class ItemServlet extends HttpServlet implements Servlet {
       
    public ItemServlet() {}

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // get parameters for basic search
		String id = preventNullString(request.getParameter("id"));
		
		// get XML data of item
		String itemXMLString = AuctionSearchClient.getXMLDataForItemId(id);
        request.setAttribute("result", itemXMLString);
        
        // if string is empty, don't need to try to parse it.
        if(itemXMLString == null || itemXMLString.isEmpty()) {
            request.setAttribute("result", "");
            request.getRequestDispatcher("/item.jsp").forward(request, response);
            return;
        }
        ItemResult item = new ItemResult();
        try {
		    Element itemElem = loadXMLFromString(itemXMLString).getDocumentElement();
            item = MyParser.extractItemInfo(itemElem);
        }  catch (FactoryConfigurationError e) {
            System.out.println("unable to get a document builder factory");
            System.exit(2);
        } catch (ParserConfigurationException e) {
            System.out.println("parser was unable to be configured");
            System.exit(2);
        } catch (SAXException e) {
            System.out.println("sax exception");
            System.exit(2);
        } catch (IOException e ) {
            System.out.println("IO exception");
            System.exit(2);
        }

		//set up session info
        HttpSession session = request.getSession(true);
        session.setAttribute("ItemResult", item);
        
        String buyLink = "https://" + request.getServerName() + ":8443/eBay/purchase";
        request.setAttribute("ItemResult", item);
        request.setAttribute("buyLink", buyLink);
		request.getRequestDispatcher("/item.jsp").forward(request, response);
        
    }
	
	// given a string, return empty string if it is null; otherwise, return as is
	public String preventNullString(String str) {
		if(str==null) {
			return "";
		}
		return str;
	}

    public static Document loadXMLFromString(String xml) 
        throws FactoryConfigurationError, ParserConfigurationException, 
                          SAXException, IOException
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        if(xml == null)
            xml = "";

        InputSource is = new InputSource(new StringReader(xml));
        
        return builder.parse(is);
    }

    
}
