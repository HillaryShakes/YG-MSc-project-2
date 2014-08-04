package genreBasedRecommendation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import collabFiltering.Things;
import collabFiltering.User;

public class UserGenres implements User{
	private int rating;
	private String tableName;
	private ThingsSerializable things;
	private int pmxid;
	private String thing_uuid;
	private ResultSet userRatings;
	private Connection cxn = null;
	private Hashtable<String, Integer> ratings = new Hashtable<String, Integer>();
	private Hashtable<String, Double> genres = new Hashtable<String, Double>();
	private Hashtable<String, Set<String>> genreItems = new Hashtable<String, Set<String>>();
	
	public UserGenres(int pmxid, String tableName, ThingsSerializable things)  {
    	this.tableName = tableName;
    	this.pmxid = pmxid;
    	this.things = things;
    	try {
			cxn = DriverManager.getConnection(
					"jdbc:postgresql://127.0.0.1:5432/postgres", "postgres",
					"yougov");
			
	
    	try {
			 
			Class.forName("org.postgresql.Driver");

		} catch (ClassNotFoundException e) {

			System.out.println("Where is your PostgreSQL JDBC Driver? "
					+ "Include in your library path!");
			e.printStackTrace();
			return;
		}

    	
    	
    	/**
    	 * populate table of ratings
    	 */
    	
    	try {
			Statement stmt = cxn.createStatement();
			
			String query = "SELECT thing_uuid, rating FROM " + tableName +" WHERE users = " + pmxid;
			userRatings = stmt.executeQuery(query);
			while (userRatings.next()){
				rating = userRatings.getInt("rating");
				thing_uuid = userRatings.getString("thing_uuid");
				if (ratings.contains(thing_uuid)){
					
				}else{
					//TODO use just positive ratings?
					if(rating >= -2){
				ratings.put(thing_uuid, rating);
					}
				}
			//
			}
			userRatings.close();
    	} catch (SQLException e) {
    		System.out.println("user");
			e.printStackTrace();
			
		}	
    	
    	/**
    	 * populate table of genres and their scores
    	 */
    	String tot = "total";
    	genres.put(tot, 0.0);
    	
    	for (String ratedItem : ratings.keySet()){
    		//do any have mutiple genres?? Then you'd have to deal with that
    		Set<String> itemGenres = things.getGenres(ratedItem);
    		
    		
    		for ( String genre : itemGenres){
    			//System.out.println("genre type: " + things.getGenreType(genre));
    			String relation = things.getGenreType(genre);
    			String type = "type";
    			//System.out.println("relation: " + relation + " type: " + type);
    		if (relation.equals(type)){
    			genres.put(genre, 0.0);
    			//System.out.println("type");
    				
    			}else{
    		if (genres.containsKey(genre)){
    			double current = genres.get(genre);
    			////raise count for that object by 1
    			//genres.put(genre, current + 1);
    			//int total = genres.get(tot);
    			//genres.put(tot, total + 1);
    			
    			// instead raise score by 1/genre popularity
    			//so we get big genres, e.g. 'movies' decreased
    			double genreScore = 1 + 1.0/things.getGenreCount(genre);
    			genres.put(genre, current + genreScore);
    			double total = genres.get(tot);
    			genres.put(tot, total + genreScore);
    		}
    		else{
    			//genres.put(genre, 1);
    			//int total = genres.get(tot);
    			//genres.put(tot, total + 1);
    			double genreScore = 1; //+ 1.0/things.getGenreCount(genre);
    			genres.put(genre, genreScore);
    			double total = genres.get(tot);
    			genres.put(tot, total + genreScore);
    		}
    		}
    		}
    	}
    	
    	/** populate each users items by genre */
    	
    	for (String thing : ratings.keySet()){
    		Set<String> thingGenres = things.getGenres(thing);
    		for (String genre : thingGenres){
    			if (genreItems.containsKey(genre)){
    				Set<String> itemsSoFar = genreItems.get(genre);
    				itemsSoFar.add(thing);
    				genreItems.put(genre, itemsSoFar);
    			}else{
    				Set<String> itemsSoFar = new HashSet<String>();
    				itemsSoFar.add(thing);
    				genreItems.put(genre, itemsSoFar);
    			}
    		}
    	}
    	cxn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	//System.out.println(genreItems);
    	
    }
	
	public Hashtable<String, Integer> getRatingsTable(){
		return ratings;
	}
	
	public Hashtable<String, Double> getGenresTable(){
		return genres;
	}
	
	public int getpmxid(){
		return pmxid;
	}
	
	public Integer getRating(String thing_uuid){
		return ratings.get(thing_uuid);
	}
	
	public Double getGenreRating(String genre){
		return genres.get(genre);
	}
	
	public int length(){
		return ratings.size();
	}
	
	public Set<String> getItemsFromGenre(String genre){
		return genreItems.get(genre);
	}
	
	public Hashtable<String, Set<String>> getGenreItems(){
		return genreItems;
	}
	

}
