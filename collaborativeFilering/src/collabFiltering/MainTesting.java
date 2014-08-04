package collabFiltering;

import genreBasedRecommendation.GenreRecSystem;
import genreBasedRecommendation.ThingsSerializable;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import randomRecommendation.RandomRec;
import collabFiltering.basicRecommendation.CFRecSystem;
import collabFiltering.basicRecommendation.NNRarity;
import collabFiltering.basicRecommendation.RecRarity;
import collabFiltering.printers.CSV2ColPrinter;
import collabFiltering.printers.CSVRowsPrinter;
import collabFiltering.printers.MiniPrinter;
import collabFiltering.printers.Printer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;


public class MainTesting {
	

	public static void main(String[] args){
		
		/**  The pmxids I have:	*/
		 
		
		int hillary = 9467900;
		int stephan = 9734410;
		int freddie = 9706552;
		int james = 9219525;
		int rosamund = 13484032;
		
		String tableName = "yougov.movies_august";
		int numRecs = 27;
		int numNeighbours = 1;
		String fileName = "group2.csv";
		
		/** connect to database */
		
		Connection cxn = null;
		List<Integer> userList = new ArrayList<Integer>();
		List<Integer> friendsList = new ArrayList<Integer>();
		
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
    	
    	/** make list of users */
    	
    
			Statement stmt = cxn.createStatement();
			String query = "SELECT DISTINCT(users)"
					+ " FROM " + tableName;
			ResultSet users = stmt.executeQuery(query);
			while (users.next()){
				int pmx = users.getInt("users");
				userList.add(pmx);
			}
			users.close();
			/** make list of users to make recs for */
	    	
		    
			Statement stmt2 = cxn.createStatement();
			String query2 = "SELECT DISTINCT(users)"
					+ " FROM yougov.friends";
			ResultSet friends = stmt2.executeQuery(query2);
			while (friends.next()){
				int pmx = friends.getInt("users");
				friendsList.add(pmx);
			}
			friends.close();
			
			Statement stmt3 = cxn.createStatement();
			String query3 = "SELECT DISTINCT(users)"
					+ " FROM yougov.group1";
			ResultSet group = stmt2.executeQuery(query3);
			while (group.next()){
				int pmx = group.getInt("users");
				friendsList.add(pmx);
			}
			group.close();
		
    	
		
		
		/**  Set up	 */
		
		Things things = new Things(tableName, cxn);
		ThingsSerializable thingsSerializable = new ThingsSerializable(things.getCountTable(), things.getItemGenreTable(),
				things.getGenreItemSet(), things.getGenreCountTable(), things.getGenreTypeTable());
		
		TableUsers answersTable = new TableUsers(tableName, thingsSerializable, userList);
		System.out.println("Made users");
		
			
	      try {
	    	  OutputStream file = new FileOutputStream("answersTable.ser");
		      OutputStream buffer = new BufferedOutputStream(file);
			ObjectOutput output = new ObjectOutputStream(buffer);
			output.writeObject(answersTable);
			output.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	      
			  try {
				  InputStream file = new FileInputStream("answersTable.ser");
			      //InputStream buffer = new BufferedInputStream(file);
			      ObjectInput input = new ObjectInputStream (file);
			       
				try {
					TableUsers answersTable2 = (TableUsers)input.readObject();
					input.close();
			
	      cxn.close();
		/**  choose nearest neighbours method */
		
		//NearestNeighbours neighbourhood = new NNProportional(answersTable);
		//NearestNeighbours neighbourhood = new NNRarity(answersTable, things);
		
		/**  choose recommender */
		
		//Recommend recommender = new RecRarity(answersTable, things);
		//Recommend recommender = new RecCommonInterest(answersTable);
		
		/**  choose printer	 */
		
		Printer print = new CSV2ColPrinter(fileName, numRecs);
		//Printer print = new CSVRowsPrinter(fileName, numRecs);
		//Printer print = new MiniPrinter();
		
		
		//int pmxid = hillary;
		for (int pmxid : friendsList){
		
			/**  build recommender	 */
		
			RecSystem system = new GenreRecSystem(answersTable, things, numNeighbours);
			//RecSystem system = new RandomRec(tableName, cxn);
			//RecSystem system = new CFRecSystem(answersTable, neighbourhood, recommender, numNeighbours);
			
			/**  make recommendations	 */
			system.makeRecommendations(pmxid, numRecs);
			/** Print recommendations */
			
			Set<String> recommendations = system.getRecommendations();
			/*double score = 0;
			
			for (String item : recommendations){
				score += ((double) things.getCount(item))/things.getCount("ba7998c8-a904-11e1-9412-005056900141");
			}
			
			System.out.println(score*1.5);*/
			system.printRecommendations(print, pmxid);
			
			
		}
			
			print.closePrint();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			  } catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
	}
}
