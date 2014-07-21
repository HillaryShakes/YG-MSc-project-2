package collabFiltering;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;


public class Things {
	
	private Hashtable<String, Integer> countTable = new Hashtable<String, Integer>();
	private Hashtable<String, Set> itemGenreTable = new Hashtable<String, Set>();
	private Hashtable<String, Set> genreItemSet = new Hashtable<String, Set>();
	private ResultSet things;
	private Connection cxn = null;
	private String tableName;
	
	/**
	 * N.B. need to include genres in the 'table' you give as tablename
	 */
	
	
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
    		things = stmt.executeQuery("SELECT thing_uuid, genre_uuid, COUNT(thing_uuid) FROM " + tableName +" GROUP BY thing_uuid, genre_uuid");
    		while(things.next()){
    			String thing_uuid = things.getString("thing_uuid");
    			int count = things.getInt("count");
    			if (countTable.keySet().contains(thing_uuid)){
    				int oldCount = countTable.get(thing_uuid);
    				countTable.put(thing_uuid, count + oldCount);
    			}else{
    			countTable.put(thing_uuid, count);
    			}
    			/*
    	    	 * TODO Make genre table
    	    	 */
    			String genre = things.getString("genre_uuid");
    			if (itemGenreTable.keySet().contains(thing_uuid)){
    				Set<String> itemGenres = itemGenreTable.get(thing_uuid);
    				itemGenres.add(genre);
    				itemGenreTable.put(thing_uuid, itemGenres);
    			}
    			else{
    				Set<String> itemGenres = new HashSet<String>();
    				itemGenres.add(genre);
    				itemGenreTable.put(thing_uuid, itemGenres);
    			}
    			
    			
    		}
			
    	} catch (SQLException e) {
    		System.out.println("things error");
			e.printStackTrace();
		}	
    	
    	
    	/*
    	 * TODO Make genreItemSet
    	 */
	}
	
	 
	
	



	public Hashtable<String, Integer> getCountTable(){
		return countTable;
	}
	
	public Hashtable<String, Set> getItemGenreTable(){
		return itemGenreTable;
	}
	
	public Hashtable<String, Set> getGenreItemSet(){
		return genreItemSet;
	}
	
	public int getCount(String thing_uuid){
		return countTable.get(thing_uuid);
	}
	
	public Set<String> getGenres(String thing_uuid){
		return itemGenreTable.get(thing_uuid);
	}
	
	public Set getItems(String genre){
		return genreItemSet.get(genre);
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

