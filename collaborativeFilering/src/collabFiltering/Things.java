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
	
	/* number of ratings each item got */
	private Hashtable<String, Integer> countTable = new Hashtable<String, Integer>();
	/* the set of genres each item belongs to */
	private Hashtable<String, Set> itemGenreTable = new Hashtable<String, Set>();
	/* the set of items that belong to each genre */
	private Hashtable<String, Set> genreItemSet = new Hashtable<String, Set>();
	private Hashtable<String, Integer> genreCountTable = new Hashtable<String, Integer>();
	/* all the genres with the types of relationship. Eg. 'movies' has type 'type', 
	 * a specific director would have type 'directed by' etc */
	private Hashtable<String, String> genreTypeTable = new Hashtable<String, String>();
	private ResultSet things;
	private ResultSet relations;
	private ResultSet genreCounts;
	private Connection cxn = null;
	private String tableName;
	
	/**
	 * N.B. need to include genres in the 'table' you give as tablename
	 */
	
	
	public Things(String tableName, Connection cxn){
		
		this.tableName = tableName;
		this.cxn = cxn;
		
		/**
		 * Make countTable:
		 */
    	
    	
    	try {
    		Statement stmt = cxn.createStatement();
    		things = stmt.executeQuery("SELECT thing_uuid, COUNT(thing_uuid) FROM " + tableName +" GROUP BY thing_uuid");
    		while(things.next()){
    			String thing_uuid = things.getString("thing_uuid");
    			int count = things.getInt("count");
    			if (countTable.keySet().contains(thing_uuid)){
    				int oldCount = countTable.get(thing_uuid);
    				countTable.put(thing_uuid, count + oldCount);
    			}else{
    			countTable.put(thing_uuid, count);
    			}
    		}
    		things.close();
    		
    		/*
    		 * make genre type table
    		 */
    		ResultSet genreRelations = stmt.executeQuery("SELECT genre_uuid, relationship FROM yougov.relationships");
    		while(genreRelations.next()){
    			String genre = genreRelations.getString("genre_uuid");
    			String relation = genreRelations.getString("relationship");
    			genreTypeTable.put(genre, relation);
    		}
    		genreRelations.close();
    			/*
    	    	 * Make genre genreCountTable, genreItemSet, and itemGenreTable
    	    	 */
    		//need to get counts of all scores of all genres and total//
    		relations = stmt.executeQuery("SELECT thing_uuid, genre_uuid FROM yougov.relationships"); 
    		String total = "total";
    		genreCountTable.put(total, 0);
    		while(relations.next()){
    			String thing_uuid = relations.getString("thing_uuid");
    			String genre = relations.getString("genre_uuid");
    			if(countTable.keySet().contains(thing_uuid)){
    				/* put in itemGenreTable */
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
    				/* put in genreCountTable */
    				if (genreCountTable.keySet().contains(genre)){
    					int thingCount = countTable.get(thing_uuid);
    					int countSoFar = genreCountTable.get(genre);
    					genreCountTable.put(genre, countSoFar + thingCount);
    					int tot = genreCountTable.get(total);
    					genreCountTable.put(total, tot + thingCount);
    				}else{
    					int thingCount = countTable.get(thing_uuid);
    					genreCountTable.put(genre, thingCount);
    					int tot = genreCountTable.get(total);
    					genreCountTable.put(total, tot + thingCount);
    				}
    			}
    			
    			
    		}
    	relations.close();
    			
    	for (String item : countTable.keySet()){
    		if (itemGenreTable.containsKey(item)){
    			
    		}else{
    			Set<String> itemGenres = new HashSet<String>();
    			itemGenres.add("noGenre");
    			itemGenreTable.put(item, itemGenres);
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
	
	public int getGenreCount(String genre){
		return genreCountTable.get(genre);
	}
	
	public String getGenreType(String genre){
		return genreTypeTable.get(genre);
	}
	
}

