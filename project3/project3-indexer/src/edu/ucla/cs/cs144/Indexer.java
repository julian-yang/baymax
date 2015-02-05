package edu.ucla.cs.cs144;

import java.io.IOException;
import java.io.StringReader;
import java.io.File;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class Indexer {
    
   	private IndexWriter indexWriter = null;
 
	// Create a new instance of Indexer
	public Indexer() {
	}
 
	public IndexWriter getIndexWriter(boolean create) throws IOException {
		
        if (indexWriter == null) {
            Directory indexDir = FSDirectory.open(new File("/var/lib/lucene/index1"));
            IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_10_2, new StandardAnalyzer());
			
			// if boolean "create" is true, set OpenMode to CREATE (this overwrites previous index with a new one)
			// otherwise, set OpenMode to APPEND (this simply adds to the previous index)
			config.setOpenMode(create ? IndexWriterConfig.OpenMode.CREATE : IndexWriterConfig.OpenMode.APPEND);
			
            indexWriter = new IndexWriter(indexDir, config);
        }
        return indexWriter;
	}
	
	public void indexItem(int itemId, String name, String description, String categories) throws IOException {
		//System.out.println("Indexing: " + itemId + " -> " + name);
		
		// get existing IndexWriter in APPEND mode
		IndexWriter writer = getIndexWriter(false);
		
		// create new Document
		Document doc = new Document();
		
		// store ItemID and Name fields
		doc.add(new IntField("ItemID", itemId, Field.Store.YES));
		doc.add(new StringField("Name", name, Field.Store.YES));
		
		// concatenate ItemID, Name, Description, and all Category fields into an unstored field
		String searchContent = itemId + " " + name + " " +  description + " " + categories;
		doc.add(new TextField("content", searchContent, Field.Store.NO));
		
		// add Document to index
		writer.addDocument(doc);
    }   
	
	public void closeIndexWriter() throws IOException {
        if (indexWriter != null) {
            indexWriter.close();
        }
	}
 
    public void rebuildIndexes() {

		// initialize connection variable
		Connection conn = null;

		// create a connection to the database to retrieve Items from MySQL
		try {
			conn = DbManager.getConnection(true);
		} catch (SQLException ex) {
			System.out.println(ex);
		}

		try {
			// overwrite existing index
			getIndexWriter(true);
			
			// set up variables for ItemID, Name, Description, and Category (a concatenated string)
			int itemId;
			String name, description, categories;
			
			// create a regular statement to be executed once to fetch all items; store query results in itemRS
			Statement s = conn.createStatement();
			ResultSet itemRS = s.executeQuery("SELECT ItemID, Name, Description FROM Items");

			// create a prepared statement to be executed multiple times (once per item) to fetch categories
			PreparedStatement getItemCategories = conn.prepareStatement("SELECT Category FROM ItemCategories WHERE ItemID = ?");

			// index each item
			while( itemRS.next() ){
				// reset and fetch indexng variables
				categories = "";
				itemId = itemRS.getInt("ItemID");
				name = itemRS.getString("Name");
				description = itemRS.getString("Description");
				
				// store query results from prepared statement in categoryRS
				getItemCategories.setInt(1, itemId);
				ResultSet categoryRS = getItemCategories.executeQuery();
				
				// concatenate all categories listed for item
				while( categoryRS.next() ) {
					categories += categoryRS.getString("Category") + " ";
				}
				
				// index the item
				indexItem(itemId, name, description, categories);
				
				// close categoryRS
				categoryRS.close();
			}
			
			// close index writer, itemRS, statement, and database connection
			closeIndexWriter();
			itemRS.close();
			s.close();
			conn.close();
			
		} catch (SQLException ex) {
			System.out.println(ex);
		} catch (IOException ex) {
			System.out.println(ex);
		}

    }    

    public static void main(String args[]) {
        Indexer idx = new Indexer();
        idx.rebuildIndexes();
    }   
}
