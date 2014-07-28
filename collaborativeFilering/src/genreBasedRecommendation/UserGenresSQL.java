package genreBasedRecommendation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;
import java.util.Set;

import collabFiltering.Things;
import collabFiltering.User;

public class UserGenresSQL implements User{
	private int rating;
	private String tableName;
	private Things things;
	private int pmxid;
	private String thing_uuid;
	private ResultSet userRatings;
	private Connection cxn = null;
	private Hashtable<String, Integer> ratings = new Hashtable<String, Integer>();
	private Hashtable<String, Double> genres = new Hashtable<String, Double>();
	
	public UserGenresSQL(int pmxid, String tableName, Things things, Connection cxn)  {
    	this.tableName = tableName;
    	this.pmxid = pmxid;
    	this.things = things;
    	this.cxn = cxn;
    	
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
				ratings.put(thing_uuid, rating);
			}
			userRatings.close();
    	} catch (SQLException e) {
    		System.out.println("user");
			e.printStackTrace();
			
		}	
		
		/**
    	 * populate table of genres and their scores
    	 */
		try {
			Statement stmt = cxn.createStatement();
			String query2 = "SELECT yougov.relationships.genre_uuid, "
					+ "COUNT(yougov.relationships.genre_uuid) "
					+ "FROM " + tableName 
					+ " INNER JOIN yougov.relationships "
					+ "ON yougov.relationships.thing_uuid = " + tableName + ".thing_uuid"
					+ " WHERE " + tableName + ".users = " + pmxid
					+ " GROUP BY yougov.relationships.genre_uuid";
			ResultSet genreRatings = stmt.executeQuery(query2);
			while (genreRatings.next()){
				String genre = genreRatings.getString("genre_uuid");
				int count = genreRatings.getInt("count");
				genres.put(genre, (double) count);
			}
			genreRatings.close();
			int tot = 0;
			String query3 = "SELECT COUNT(" + tableName + ".users)"
					+ " FROM " + tableName
					+ " INNER JOIN yougov.relationships "
					+ "ON yougov.relationships.thing_uuid = " + tableName +".thing_uuid"
					+ " WHERE " + tableName + ".users = " + pmxid
					+ " AND yougov.relationships.relationship != 'type'";
			ResultSet total = stmt.executeQuery(query3);
			while (total.next()){
				tot = total.getInt("count");
			}
			total.close();
    	} catch (SQLException e) {
    		System.out.println("user");
			e.printStackTrace();
			
		}	
		
		/* Scale */
		System.out.println("made a new user");
    	
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

	@Override
	public Set<String> getItemsFromGenre(String genre) {
		// TODO Auto-generated method stub
		return null;
	}
	

}
