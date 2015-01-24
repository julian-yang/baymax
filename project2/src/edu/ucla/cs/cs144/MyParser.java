/* CS144
 *
 * Parser skeleton for processing item-???.xml files. Must be compiled in
 * JDK 1.5 or above.
 *
 * Instructions:
 *
 * This program processes all files passed on the command line (to parse
 * an entire diectory, type "java MyParser myFiles/*.xml" at the shell).
 *
 * At the point noted below, an individual XML file has been parsed into a
 * DOM Document node. You should fill in code to process the node. Java's
 * interface for the Document Object Model (DOM) is in package
 * org.w3c.dom. The documentation is available online at
 *
 * http://java.sun.com/j2se/1.5.0/docs/api/index.html
 *
 * A tutorial of Java's XML Parsing can be found at:
 *
 * http://java.sun.com/webservices/jaxp/
 *
 * Some auxiliary methods have been written for you. You may find them
 * useful.
 */

package edu.ucla.cs.cs144;

import java.io.*;
import java.text.*;
import java.util.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ErrorHandler;


class MyParser {
    
    static final String columnSeparator = "|*|";
    static DocumentBuilder builder;
    
    static final String[] typeName = {
	"none",
	"Element",
	"Attr",
	"Text",
	"CDATA",
	"EntityRef",
	"Entity",
	"ProcInstr",
	"Comment",
	"Document",
	"DocType",
	"DocFragment",
	"Notation",
    };
    
    static Hashtable<String, Boolean> userList;
    static FileWriter items;
    static FileWriter itemCategories;
    static FileWriter users;
    static FileWriter bids;


    static class MyErrorHandler implements ErrorHandler {
        
        public void warning(SAXParseException exception)
        throws SAXException {
            fatalError(exception);
        }
        
        public void error(SAXParseException exception)
        throws SAXException {
            fatalError(exception);
        }
        
        public void fatalError(SAXParseException exception)
        throws SAXException {
            exception.printStackTrace();
            System.out.println("There should be no errors " +
                               "in the supplied XML files.");
            System.exit(3);
        }
        
    }
    
    /* Non-recursive (NR) version of Node.getElementsByTagName(...)
     */
    static Element[] getElementsByTagNameNR(Element e, String tagName) {
        Vector< Element > elements = new Vector< Element >();
        Node child = e.getFirstChild();
        while (child != null) {
            if (child instanceof Element && child.getNodeName().equals(tagName))
            {
                elements.add( (Element)child );
            }
            child = child.getNextSibling();
        }
        Element[] result = new Element[elements.size()];
        elements.copyInto(result);
        return result;
    }
    
    /* Returns the first subelement of e matching the given tagName, or
     * null if one does not exist. NR means Non-Recursive.
     */
    static Element getElementByTagNameNR(Element e, String tagName) {
        Node child = e.getFirstChild();
        while (child != null) {
            if (child instanceof Element && child.getNodeName().equals(tagName))
                return (Element) child;
            child = child.getNextSibling();
        }
        return null;
    }
    
    /* Returns the text associated with the given element (which must have
     * type #PCDATA) as child, or "" if it contains no text.
     */
    static String getElementText(Element e) {
        if (e.getChildNodes().getLength() == 1) {
            Text elementText = (Text) e.getFirstChild();
            return elementText.getNodeValue();
        }
        else
            return "";
    }
    
    /* Returns the text (#PCDATA) associated with the first subelement X
     * of e with the given tagName. If no such X exists or X contains no
     * text, "" is returned. NR means Non-Recursive.
     */
    static String getElementTextByTagNameNR(Element e, String tagName) {
        Element elem = getElementByTagNameNR(e, tagName);
        if (elem != null)
            return getElementText(elem);
        else
            return "";
    }
    
    /* Returns the amount (in XXXXX.xx format) denoted by a money-string
     * like $3,453.23. Returns the input if the input is an empty string.
     */
    static String strip(String money) {
        if (money.equals(""))
            return money;
        else {
            double am = 0.0;
            NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US);
            try { am = nf.parse(money).doubleValue(); }
            catch (ParseException e) {
                System.out.println("This method should work for all " +
                                   "money values you find in our data.");
                System.exit(20);
            }
            nf.setGroupingUsed(false);
            return nf.format(am).substring(1);
        }
    }
    
    /* Process one items-???.xml file.
     */
    static void processFile(File xmlFile) {
        Document doc = null;
        try {
            doc = builder.parse(xmlFile);
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(3);
        }
        catch (SAXException e) {
            System.out.println("Parsing error on file " + xmlFile);
            System.out.println("  (not supposed to happen with supplied XML files)");
            e.printStackTrace();
            System.exit(3);
        }
        
        /* At this point 'doc' contains a DOM representation of an 'Items' XML
         * file. Use doc.getDocumentElement() to get the root Element. */
        System.out.println("Successfully parsed - " + xmlFile);
        
        /* Fill in code here (you will probably need to write auxiliary
            methods). */
        
        //open each CSV file (creates it if it doesn't exist)
        System.out.println("creating csv file");
        items = createCSVFile("Items.csv");
        itemCategories = createCSVFile("ItemCategories.csv");
        users = createCSVFile("Users.csv");
        bids = createCSVFile("Bids.csv");
        
        Element itemsElem = doc.getDocumentElement();
        Element[] itemList = getElementsByTagNameNR(itemsElem, "Item");
        System.out.println("There are " + itemList.length + " <item> tags");
        
        for (Element curItem : itemList) {
            processItem(curItem);
        }

        //close the csv files
        try {
            items.flush();
            items.close();

            itemCategories.flush();
            itemCategories.close();
            
            users.flush();
            users.close();
            
            bids.flush();
            bids.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
        /**************************************************************/
        
    }

    static void processItem(Element curItem) {
        String itemRow = "";

        //get the itemID
        String itemID = getAttributeText(curItem, "ItemID");
        itemRow += wrapQuotations(itemID) + ", ";
        
        //get the name of item
        String name = getElementTextByTagNameNR(curItem, "Name");
        itemRow += wrapQuotations(name) + ", ";

        //get Buy_Price, just put in the "" if it doesn't exist.
        String buyPrice = getElementTextByTagNameNR(curItem, "Buy_Price");
        itemRow += wrapQuotations(buyPrice) + ", ";

        //get 
        System.out.println("itemid: " + itemID);
        System.out.println("name: " + name);

        System.out.println("SQL line: " + itemRow);
        //for each itemCategory

        //check if we need to insert new user

        //for each bid

        
    }

    //wraps quotations around the String text
    static String wrapQuotations(String text) {
        return "\"" + text + "\"";
    }

    //gets the attribute text from a given Element e
    //it returns null if the attribute does not exist
    static String getAttributeText(Element e, String attrName) {
        Node attr = e.getAttributes().getNamedItem(attrName);
        if(attr != null) 
            return attr.getNodeValue();
        else
            return null;
    }

    //write a single line in a FileWriter writer for given String line
    static void writeLine(FileWriter writer, String line) {
        try {
            writer.append(line + '\n');
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    //creates/opens the csv file of name fileName
    static FileWriter createCSVFile(String fileName) {
        FileWriter temp = null;
        try {
            temp = new FileWriter(fileName, true);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(3);
        }
        return temp;
    }
    
    public static void main (String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java MyParser [file] [file] ...");
            System.exit(1);
        }
        
        /* Initialize parser. */
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            factory.setIgnoringElementContentWhitespace(true);      
            builder = factory.newDocumentBuilder();
            builder.setErrorHandler(new MyErrorHandler());
            userList = new Hashtable<String, Boolean>();
        }
        catch (FactoryConfigurationError e) {
            System.out.println("unable to get a document builder factory");
            System.exit(2);
        } 
        catch (ParserConfigurationException e) {
            System.out.println("parser was unable to be configured");
            System.exit(2);
        }
        
        /* Process all files listed on command line. */
        for (int i = 0; i < args.length; i++) {
            File currentFile = new File(args[i]);
            processFile(currentFile);
        }
    }
}
