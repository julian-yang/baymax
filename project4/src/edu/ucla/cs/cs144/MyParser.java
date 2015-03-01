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
    static Vector<String> bothSellerAndBuyer;
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
    
    static Hashtable<String, String []> userList;
    static FileWriter itemWriter;
    static FileWriter itemCategoriesWriter;
    static FileWriter userWriter;
    static FileWriter bidWriter;


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
        System.out.println("creating dat file");
        itemWriter = createCSVFile("Items.dat");
        itemCategoriesWriter = createCSVFile("ItemCategories.dat");
        bidWriter = createCSVFile("Bids.dat");
        
        Element itemsElem = doc.getDocumentElement();
        Element[] itemList = getElementsByTagNameNR(itemsElem, "Item");
        System.out.println("There are " + itemList.length + " <item> tags");
        
        for (Element curItem : itemList) {
            processItem(curItem);
        }

        
        //close the dat files
        try {
            itemWriter.flush();
            itemWriter.close();

            itemCategoriesWriter.flush();
            itemCategoriesWriter.close();
            
            bidWriter.flush();
            bidWriter.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
        /**************************************************************/
        
    }

    static void processItem(Element curItem) {
        String itemRow = "";

        //get the item columns
        String itemID = getAttributeText(curItem, "ItemID");
        itemRow += itemID + ",";
        
        String name = getElementTextByTagNameNR(curItem, "Name");
        itemRow += wrapQuotations(name) + ",";

        //get Buy_Price, just put in the "" if it doesn't exist.
        String buyPrice = strip(getElementTextByTagNameNR(curItem, "Buy_Price"));
        if(!buyPrice.equals(""))
            itemRow += buyPrice + ",";
        else
            itemRow += "NULL,";

        //get First_Bid, just put in the "" if it doesn't exist.
        String firstBid = strip(getElementTextByTagNameNR(curItem, "First_Bid"));
        itemRow += firstBid + ",";

        String started = convertToSQLTime(getElementTextByTagNameNR(curItem, "Started"));
        itemRow += wrapQuotations(started) + ",";

        String ends = convertToSQLTime(getElementTextByTagNameNR(curItem, "Ends"));
        itemRow += wrapQuotations(ends) + ",";

        Element locationElem = getElementByTagNameNR(curItem, "Location");

        String latitude = getAttributeText(locationElem, "Latitude");
        itemRow += wrapQuotations(latitude) + ",";

        String longitude = getAttributeText(locationElem, "Longitude");
        itemRow += wrapQuotations(longitude) + ",";

        String location = getElementText(locationElem);
        itemRow += wrapQuotations(location) + ",";

        String country = getElementTextByTagNameNR(curItem, "Country");
        itemRow += wrapQuotations(country) + ",";

        String description = getElementTextByTagNameNR(curItem, "Description");
        description = description.substring(0, 
                Math.min(description.length(), 4000));
        itemRow += wrapQuotations(description) + ",";

        Element sellerElem = getElementByTagNameNR(curItem, "Seller");
        String sellerID = getAttributeText(sellerElem, "UserID");
        itemRow += wrapQuotations(sellerID);

        String sellerRating = getAttributeText(sellerElem, "Rating");
        
        //check if this seller already exists in our hashtable
        String sellerRow [] = userList.get(sellerID);
        if(sellerRow == null) {
            sellerRow = new String[5];
            sellerRow[0] = sellerID;
            sellerRow[1] = "NULL";
            sellerRow[2] = sellerRating;
            sellerRow[3] = "NULL";
            sellerRow[4] = "NULL";
            userList.put(sellerID, sellerRow);
        } else {
            sellerRow[2] = sellerRating;
            //if(sellerRow[1] != null)
            //    bothSellerAndBuyer.add(sellerID);
        }
        //String sellerRow = wrapQuotations(seller) + "," + wrapQuotations(sellerRating);
        
        //System.out.println("itemid: " + itemID);
        //System.out.println("name: " + name);
        //System.out.println("buy_Price: " + buyPrice);
        //System.out.println("started: " + started);
        //System.out.println("ends: " + ends);
        //System.out.println("location: " + location);
        //System.out.println("latitude: " + latitude);
        //System.out.println("longitude: " + longitude);
        //System.out.println("country: " + country);
        //System.out.println("description: " + description);
        //System.out.println("seller ID: " + seller);
        //System.out.println("SQL line: " + itemRow);

        //System.out.println("seller rating: " + sellerRating);
        
        //for each itemCategory
        Element [] categories = getElementsByTagNameNR(curItem, "Category");
        for(Element category : categories) {
            String categoryName = getElementText(category);
            String categoryRow = wrapQuotations(itemID) + "," 
                + wrapQuotations(categoryName);
            writeLine(itemCategoriesWriter, categoryRow);
        }

        //for each bid
        Element bidsElem = getElementByTagNameNR(curItem, "Bids");
        Element [] bids = getElementsByTagNameNR(bidsElem, "Bid");
        for(Element bid : bids) {
            //process the user (bidder) first
            Element bidderElem = getElementByTagNameNR(bid, "Bidder");
            String bidderID = getAttributeText(bidderElem, "UserID");
            String bidderRating = getAttributeText(bidderElem, "Rating");
            String bidderLocation = 
                getElementTextByTagNameNR(bidderElem, "Location");
            String bidderCountry = 
                getElementTextByTagNameNR(bidderElem, "Country");

            
            String bidderRow [] = userList.get(bidderID);
            if(bidderRow == null) {
                bidderRow = new String[5];
                bidderRow[0] = bidderID;
                bidderRow[1] = bidderRating;
                bidderRow[2] = "NULL";
                bidderRow[3] = bidderLocation;
                bidderRow[4] = bidderCountry;
                userList.put(bidderID, bidderRow);
            } else {
                bidderRow[1] = bidderRating;
                bidderRow[3] = bidderLocation;
                bidderRow[4] = bidderCountry;
                //if(!bidderRow[2].equals("NULL"))
                //    bothSellerAndBuyer.add(bidderID);
            }
            
            String bidTime = 
                convertToSQLTime(getElementTextByTagNameNR(bid, "Time"));
            String amount = strip(getElementTextByTagNameNR(bid, "Amount"));
            String bidRow = wrapQuotations(bidderID) + "," 
                + itemID + "," + wrapQuotations(bidTime) + ","
                + wrapQuotations(amount);

            writeLine(bidWriter, bidRow);
        }

        writeLine(itemWriter, itemRow);
        //System.out.println("--------END ITEM PROCESS---------");
    }


    static ItemResult extractItemInfo(Element curItem) {
        ItemResult item = new ItemResult();

        //get the item columns
        String itemId = getAttributeText(curItem, "ItemID");
        
        String name = getElementTextByTagNameNR(curItem, "Name");
        
        String currently = getElementTextByTagNameNR(curItem, "Currently");

        //get Buy_Price, just put in the "" if it doesn't exist.
        String buyPrice = getElementTextByTagNameNR(curItem, "Buy_Price");
        
        //get First_Bid, just put in the "" if it doesn't exist.
        String firstBid = getElementTextByTagNameNR(curItem, "First_Bid");
        
        String numberOfBids = getElementTextByTagNameNR(curItem, "Number_of_Bids");

        String started = convertToSQLTime(getElementTextByTagNameNR(curItem, "Started"));

        String ends = convertToSQLTime(getElementTextByTagNameNR(curItem, "Ends"));

        Element locationElem = getElementByTagNameNR(curItem, "Location");

        String latitude = getAttributeText(locationElem, "Latitude");

        String longitude = getAttributeText(locationElem, "Longitude");

        String location = getElementText(locationElem);

        String country = getElementTextByTagNameNR(curItem, "Country");

        String description = getElementTextByTagNameNR(curItem, "Description");
        description = description.substring(0, 
                Math.min(description.length(), 4000));

        Element sellerElem = getElementByTagNameNR(curItem, "Seller");
        String sellerId = getAttributeText(sellerElem, "UserID");

        String sellerRating = getAttributeText(sellerElem, "Rating");
        
               
        //for each itemCategory
        Element [] categoriesElem = getElementsByTagNameNR(curItem, "Category");
        ArrayList<String> categoriesArrayList = new ArrayList<String>();
        for(Element category : categoriesElem) {
            categoriesArrayList.add(getElementText(category));
        }
        String[] categories = new String[categoriesArrayList.size()];
        categories = categoriesArrayList.toArray(categories);

        //for each bid
        Element bidsElem = getElementByTagNameNR(curItem, "Bids");
        Element [] bidElemList = getElementsByTagNameNR(bidsElem, "Bid");
        ArrayList<BidResult> bidsArrayList = new ArrayList<BidResult>();
        for(Element bid : bidElemList) {
            //process the user (bidder) first
            Element bidderElem = getElementByTagNameNR(bid, "Bidder");
            String bidderId = getAttributeText(bidderElem, "UserID");
            String bidderRating = getAttributeText(bidderElem, "Rating");
            String bidderLocation = 
                getElementTextByTagNameNR(bidderElem, "Location");
            String bidderCountry = 
                getElementTextByTagNameNR(bidderElem, "Country");

            
            String bidTime = getElementTextByTagNameNR(bid, "Time");
            String amount = getElementTextByTagNameNR(bid, "Amount");
            
            BidResult bidResult = new BidResult(bidderId, bidderRating, 
                    bidderLocation, bidderCountry, amount, bidTime);
            
            bidsArrayList.add(bidResult);
        }
        BidResult[] bidResults = new BidResult[bidsArrayList.size()];
        bidResults = bidsArrayList.toArray(bidResults);

        return new ItemResult(itemId, name, categories, currently, buyPrice,
                firstBid, numberOfBids, bidResults, longitude, latitude, 
                location, country, started, ends, sellerId, sellerRating, 
                description);
    }
    
    static void writeUsersToFile() {
        userWriter = createCSVFile("Users.dat");
        //write the list of users to file
        System.out.println("Users that are both seller and buyer BUT different ratings: ");
        Enumeration<String> keys = userList.keys();
        while(keys.hasMoreElements()) {
            String curUserID = keys.nextElement();
            String curUser [] = userList.get(curUserID);
            String userRow = wrapQuotations(curUser[0]) + "," 
                + curUser[1] + "," + curUser[2]
                + "," + wrapQuotations(curUser[3]) + "," 
                + wrapQuotations(curUser[4]);
            writeLine(userWriter, userRow);
            //if(curUser[1] != null && curUser[2] != null && !curUser[1].equals(curUser[2]))
            //    System.out.println(curUser[0]);
        }
        System.out.println("----end different ratings----");
        
        //System.out.println("The users that are both seller and buyer: ");
        //Enumeration<String> both = bothSellerAndBuyer.elements();
        //while(both.hasMoreElements()) {
        //    System.out.println(both.nextElement());
        //}
        //System.out.println("----end both----");

        try {
            userWriter.flush();
            userWriter.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    //converts the XML date format to the SQL TIMESTAMP format
    //"Dec-03-01 18:38:23" => "0000-00-00 00:00:00"
    static String convertToSQLTime(String xmlTime) {
        SimpleDateFormat xmlFormat = new SimpleDateFormat("MMM-dd-yy HH:mm:ss");
        SimpleDateFormat sqlFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date inputDate = null;
        try {
            inputDate = xmlFormat.parse(xmlTime);
        } catch(ParseException pe) {
            System.out.println("ERROR: could not parse \"" + xmlTime + "\"");
            System.exit(3);
        }
        
        return sqlFormat.format(inputDate);
    }

    //wraps quotations around the String text
    static String wrapQuotations(String text) {
        //use '\t' because that seems to be the character that doesn't appear in
        //the text of descriptions...
        if(text != null) {
            return "\t" + text + "\t";
        }
        else
            return "NULL";
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

    //creates/opens the dat file of name fileName
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
            userList = new Hashtable<String, String []>();
            bothSellerAndBuyer = new Vector<String>();
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
        writeUsersToFile();
    }
}
