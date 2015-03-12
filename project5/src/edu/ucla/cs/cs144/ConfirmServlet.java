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

public class ConfirmServlet extends HttpServlet implements Servlet {
    
    public ConfirmServlet() {}

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        Object item = session.getAttribute("ItemResult");
        request.setAttribute("ItemResult", item);
        request.setAttribute("isSecure", request.isSecure());
        request.getRequestDispatcher("/confirm.jsp").forward(request, response);
    }
}
