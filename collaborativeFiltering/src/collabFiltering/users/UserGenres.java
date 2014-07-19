package collabFiltering.users;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;

import collabFiltering.Things;

public class UserGenres implements User{
	private int rating;
	private String tableName;
	private Things things;
	private int pmxid;
	private String thing_uuid;
	private ResultSet userRatings;
	private Connection cxn = null;
	private Hashtable<String, Integer> ratings = new Hashtable<String, Integer>();
	private Hashtable<String, Integer> genres = new Hashtable<String, Integer>();
	
	public UserGenres(int pmxid, String tableName, Things things)  {
    	this.tableName = tableName;
    	this.pmxid = pmxid;
    	this.things = things;
    	
    	/**
    	 * populate table of ratings
    	 */
    	
    	try {
			cxn = DriverManager.getConnection(
					"jdbc:postgresql://127.0.0.1:5432/postgres", "postgres",
					"yougov");
			
		} catch (SQLException e) {
			System.out.println("Connection Failed in User");
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
			String query = "SELECT thing_uuid, rating FROM " + tableName +" WHERE users = " + pmxid;
			userRatings = stmt.executeQuery(query);
			while (userRatings.next()){
				rating = userRatings.getInt("rating");
				thing_uuid = userRatings.getString("thing_uuid");
				ratings.put(thing_uuid, rating);
			//userRatings.close();
			}
			
    	} catch (SQLException e) {
    		System.out.println("user");
			e.printStackTrace();
			
		}	
    	
    	/**
    	 * populate table of genres and their scores
    	 */
    	
    	for (String ratedItem : ratings.keySet()){
    		//do any have mutiple genres?? Then you'd have to deal with that
    		String genre = things.getGenre(ratedItem);
    		if (genres.containsKey(genre)){
    			int current = genres.get(genre);
    			//raise count for that object by 1
    			genres.put(genre, current + 1);
    		}
    		else{
    			genres.put(genre, 1);
    		}
    	}
    	
    	
    }
	
	public Hashtable<String, Integer> getRatingsTable(){
		return ratings;
	}
	
	public Hashtable<String, Integer> getGenresTable(){
		return genres;
	}
	
	public int getpmxid(){
		return pmxid;
	}
	
	public Integer getRating(String thing_uuid){
		return ratings.get(thing_uuid);
	}
	
	public Integer getGenreRating(String genre){
		return genres.get(genre);
	}
	
	public int length(){
		return ratings.size();
	}
	
	public void closeCon(){
		try {
			userRatings.close();
			cxn.close();
		} catch (SQLException e) {
			System.out.println("couldnt close userRatings");
			e.printStackTrace();
		}
		
	}
	

}
