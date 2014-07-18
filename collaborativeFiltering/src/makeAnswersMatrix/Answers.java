package makeAnswersMatrix;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;


public class Answers {
	
	private int pmxid;
	private String tableName;
	private Hashtable<Integer, User> answers = new Hashtable<Integer, User>();
	private ResultSet users;
	private Connection cxn = null;
	public int[] userList;
	
	
	public Answers(String tableName){
		
		this.tableName = tableName;

		
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
    		//count users to make userList of right size
			Statement stmt = cxn.createStatement();
			ResultSet countUsers = stmt.executeQuery("SELECT COUNT(DISTINCT users) FROM " + tableName);
			int numUsers = 0;
			while (countUsers.next()){
				numUsers = countUsers.getInt("count");
			}
			//list of users, can't remember why I needed that...
			userList = new int[numUsers];
			String query = "SELECT DISTINCT(users) FROM " + tableName;
			users = stmt.executeQuery(query);
			int i = 0;
			while (users.next()){
				pmxid = users.getInt("users");
				final User newUser = new User(pmxid, tableName);
				answers.put(pmxid, newUser);
				newUser.closeCon();
				userList[i] = pmxid;
				i = i + 1;
			}
			
    	} catch (SQLException e) {
    		System.out.println("answers");
			e.printStackTrace();
		}	
	}
	
	public Hashtable<Integer, User> getAnswersTable(){
		return answers;
	}
	
	public User getUser(int pmxid){
		return answers.get(pmxid);
	}
	
	
	public void closeCon(){
		try {
			users.close();
			cxn.close();
		} catch (SQLException e) {
			System.out.println("couldnt close userRatings");
			e.printStackTrace();
		}
	}

}
