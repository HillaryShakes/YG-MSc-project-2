package collabFiltering;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;


public class UserRatings implements User{
	
	private int rating;
	private String tableName;
	private int pmxid;
	private String thing_uuid;
	private ResultSet userRatings;
	private Connection cxn = null;
	private Hashtable<String, Integer> ratings = new Hashtable<String, Integer>();
	
	public UserRatings(int pmxid, String tableName)  {
    	this.tableName = tableName;
    	this.pmxid = pmxid;
    	
    	
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
			}
			
    	} catch (SQLException e) {
    		System.out.println("user");
			e.printStackTrace();
			
		}	
    	
    	
    }
	
	public Hashtable<String, Integer> getRatingsTable(){
		return ratings;
	}
	
	public int getpmxid(){
		return pmxid;
	}
	
	public Integer getRating(String thing_uuid){
		return ratings.get(thing_uuid);
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
