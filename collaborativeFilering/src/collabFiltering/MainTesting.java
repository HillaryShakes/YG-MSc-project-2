package collabFiltering;

import genreBasedRecommendation.GenreRecSystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import collabFiltering.basicRecommendation.CFRecSystem;
import collabFiltering.basicRecommendation.NNRarity;
import collabFiltering.basicRecommendation.RecRarity;
import collabFiltering.printers.CSV2ColPrinter;
import collabFiltering.printers.CSVRowsPrinter;
import collabFiltering.printers.MiniPrinter;
import collabFiltering.printers.Printer;


public class MainTesting {
	

	public static void main(String[] args){
		
		/**  The pmxids I have:	*/
		 
		
		int hillary = 9467900;
		int stephan = 9734410;
		int freddie = 9706552;
		
		String tableName = "yougov.movies_random_1000";
		int numRecs = 30;
		int numNeighbours = 10;
		String fileName = "file.csv";
		
		/** connect to database */
		
		Connection cxn = null;
		List<Integer> userList = new ArrayList<Integer>();
		
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
		
    	
		
		
		/**  Set up	 */
		
		Things things = new Things(tableName, cxn);
		System.out.println("Made things");
		
		TableUsers answersTable = new TableUsers(tableName, things, userList, cxn);
		System.out.println("Made users");
		
		/**  choose nearest neighbours method */
		
		//NearestNeighbours neighbourhood = new NNProportional(answersTable);
		//NearestNeighbours neighbourhood = new NNRarity(answersTable, things);
		
		/**  choose recommender */
		
		//Recommend recommender = new RecRarity(answersTable, things);
		//Recommend recommender = new RecCommonInterest(answersTable);
		
		/**  choose printer	 */
		
		//Printer print = new CSV2ColPrinter(fileName, numRecs);
		//Printer print = new CSVRowsPrinter(fileName, numRecs);
		Printer print = new MiniPrinter();
		
		
		int pmxid = hillary;
		//for (int pmxid : userList){
		
			/**  build recommender	 */
		
			RecSystem system = new GenreRecSystem(answersTable, things);
			//RecSystem system = new RandomRec(tableName, cxn);
			//RecSystem system = new CFRecSystem(answersTable, neighbourhood, recommender, numNeighbours);
			
			/**  make recommendations	 */
			system.makeRecommendations(pmxid, numRecs);
			/** Print recommendations */
			
			system.printRecommendations(print, pmxid);
			
			
		//}
			
			print.closePrint();

				cxn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		
		
	}
}
