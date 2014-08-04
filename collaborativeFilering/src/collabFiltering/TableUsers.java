package collabFiltering;

import genreBasedRecommendation.ThingsSerializable;
import genreBasedRecommendation.UserGenres;
import genreBasedRecommendation.UserGenresSQL;
import genreBasedRecommendation.UserGenresSerializable;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;



public class TableUsers implements Serializable{
	
	private int pmxid;
	private String tableName;
	private Hashtable<Integer, UserGenresSerializable> answers = new Hashtable<Integer, UserGenresSerializable>();
	private ResultSet users;
	private ThingsSerializable things;
	public List<Integer> userList;
	
	
	public TableUsers(String tableName, ThingsSerializable things, List<Integer> userList){
		
		this.tableName = tableName;
		this.things = things;
		this.userList = userList;
		
    	for (int pmxid : userList){
			/*
			 * choose user type:
			 */
			//final User newUser = new UserRatings(pmxid, tableName);
			final UserGenres newUser = new UserGenres(pmxid, tableName, things);
			UserGenresSerializable seriUser = 
					new UserGenresSerializable(newUser.getpmxid(), newUser.getRatingsTable(), newUser.getGenresTable(), newUser.getGenreItems());
			//final User newUser = new UserGenresSQL(pmxid, tableName, things, cxn);
			answers.put(pmxid, seriUser);
		}	
	}
	
	public Hashtable<Integer, UserGenresSerializable> getAnswersTable(){
		return answers;
	}
	
	public UserGenresSerializable getUser(int pmxid){
		return answers.get(pmxid);
	}
	

}
