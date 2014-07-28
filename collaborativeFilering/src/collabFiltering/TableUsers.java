package collabFiltering;

import genreBasedRecommendation.UserGenres;
import genreBasedRecommendation.UserGenresSQL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;



public class TableUsers {
	
	private int pmxid;
	private String tableName;
	private Hashtable<Integer, User> answers = new Hashtable<Integer, User>();
	private ResultSet users;
	private Things things;
	private Connection cxn = null;
	public List<Integer> userList;
	
	
	public TableUsers(String tableName, Things things, List<Integer> userList, Connection cxn){
		
		this.tableName = tableName;
		this.things = things;
		this.cxn = cxn;
		this.userList = userList;
		
    	for (int pmxid : userList){
			/*
			 * choose user type:
			 */
			//final User newUser = new UserRatings(pmxid, tableName);
			final User newUser = new UserGenres(pmxid, tableName, things, cxn);
			//final User newUser = new UserGenresSQL(pmxid, tableName, things, cxn);
			answers.put(pmxid, newUser);
		}	
	}
	
	public Hashtable<Integer, User> getAnswersTable(){
		return answers;
	}
	
	public User getUser(int pmxid){
		return answers.get(pmxid);
	}
	

}
