package collabFiltering;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;
import java.util.Set;


public class Things {
	
	private Hashtable<String, Integer> countTable = new Hashtable<String, Integer>();
	private Hashtable<String, String> itemGenreTable = new Hashtable<String, String>();
	private Hashtable<String, Set> genreItemSet = new Hashtable<String, Set>();
	private ResultSet things;
	private Connection cxn = null;
	private String tableName;
	
	
	public Things(String tableName){
		
		this.tableName = tableName;
		
		/**
		 * Make countTable:
		 */
		
    	try {
			cxn = DriverManager.getConnection(
					"jdbc:postgresql://127.0.0.1:5432/postgres", "postgres",
					"yougov");
			//System.out.println("connection worked");
			
		} catch (SQLException e) {
			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
		
		}
    	try {
			 
			Class.forName("org.postgresql.Driver");

		} catch (ClassNotFoundException e) {

			System.out.println("Where is your PostgreSQL JDBC Driver? "
					+ "Include in your library path!");
			e.printStackTrace();
			return;
		}
    	
    	
    	
    	try {
    		Statement stmt = cxn.createStatement();
    		things = stmt.executeQuery("SELECT thing_uuid, COUNT(thing_uuid) FROM " + tableName +" GROUP BY thing_uuid");
    		while(things.next()){
    			String thing_uuid = things.getString("thing_uuid");
    			int count = things.getInt("count");
    			countTable.put(thing_uuid, count);
    		}
			
    	} catch (SQLException e) {
    		System.out.println("things error");
			e.printStackTrace();
		}	
	}
	
	 
	/*
	 * TODO Make genre table
	 */
	



	public Hashtable<String, Integer> getCountTable(){
		return countTable;
	}
	
	public Hashtable<String, String> getItemGenreTable(){
		return itemGenreTable;
	}
	
	public Hashtable<String, Set> getGenreItemSet(){
		return genreItemSet;
	}
	
	public int getCount(String thing_uuid){
		return countTable.get(thing_uuid);
	}
	
	public String getGenre(String thing_uuid){
		return itemGenreTable.get(thing_uuid);
	}
	
	public void closeCon(){
		try {
			things.close();
			cxn.close();
		} catch (SQLException e) {
			System.out.println("couldnt close things");
			e.printStackTrace();
		}
		
	}
}

