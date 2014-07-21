package collabFiltering;

import java.util.List;
import java.util.Set;

import collabFiltering.neighbours.NNRarity;
import collabFiltering.neighbours.NearestNeighbours;
import collabFiltering.printers.MiniPrinter;
import collabFiltering.printers.Printer;
import collabFiltering.recSystems.CFRecSystem;
import collabFiltering.recSystems.GenreRecSystem;
import collabFiltering.recSystems.RecSystem;
import collabFiltering.recommenders.RecRarity;
import collabFiltering.recommenders.Recommend;


public class MainTesting {
	

	public static void main(String[] args){
		
		/**  The pmxids I have:	*/
		 
		
		int hillary = 9467900;
		int stephan = 9734410;
		int freddie = 9706552;
		
		String tableName = "yougov.movies_genres2_random_1000";
		int numRecs = 30;
		int numNeighbours = 10;
		
		
		/**  Set up	 */
		
		Things things = new Things(tableName);
		things.closeCon();
		System.out.println("Made things");
		
		TableUsers answersTable = new TableUsers(tableName, things);
		answersTable.closeCon();
		System.out.println("Made users");
		
		/**  choose nearest neighbours method */
		
		//NearestNeighbours neighbourhood = new NNProportional(answersTable);
		//NearestNeighbours neighbourhood = new NNRarity(answersTable, things);
		
		/**  choose recommender */
		
		//Recommend recommender = new RecRarity(answersTable, things);
		//Recommend recommender = new RecCommonInterest(answersTable);
		
		/**  choose printer	 */
		
		//Printer print = new CSV2ColPrinter("printing.csv", numRecs);
		Printer print = new MiniPrinter();
		
		/**  build recommender	 */
		
		RecSystem system = new GenreRecSystem(answersTable, things);
		//RecSystem system = new CFRecSystem(answersTable, neighbourhood, recommender, numNeighbours);
		int pmxid = hillary;
		//for (int pmxid : answersTable.userList){
			
			/**  make recommendations	 */
		
			system.makeRecommendations(pmxid, numRecs);
			
			/** Print recommendations */
			
			system.printRecommendations(print, pmxid);
			
			
		//}
			
			print.closePrint();
		
		
	}
}
