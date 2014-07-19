package collabFiltering;

import java.util.List;
import java.util.Set;


public class MainTesting {
	

	public static void main(String[] args){
		
		/**  The pmxids I have:	*/
		 
		
		int hillary = 9467900;
		int stephan = 9734410;
		int freddie = 9706552;
		
		String tableName = "yougov.movies_random_1000";
		int numRecs = 10;
		int numNeighbours = 20;
		
		
		/**  Set up	 */
		
		Things things = new Things(tableName);
		things.closeCon();
		
		TableUsers answersTable = new TableUsers(tableName, things);
		answersTable.closeCon();
		
		/**  choose nearest neighbours method */
		
		//NearestNeighbours neighbourhood = new NNProportional(answersTable);
		NearestNeighbours neighbourhood = new NNRarity(answersTable, things);
		
		/**  choose recommender */
		
		Recommend recommender = new RecRarity(answersTable, things);
		//Recommend recommender = new RecCommonInterest(answersTable);
		
		/**  choose printer	 */
		
		//Printer print = new CSV2ColPrinter("printing.csv", numRecs);
		Printer print = new MiniPrinter();
		
		/**  build recommender	 */
		
		RecSystem system = new CFRecSystem(answersTable, neighbourhood, recommender, numNeighbours, numNeighbours);
		
		int pmxid = hillary;
		//for (int pmxid : answersTable.userList){
			
			/**  make recommendations	 */
		
			system.makeRecommendations(pmxid);
			
			/** Print recommendations */
			
			system.printRecommendations(print, pmxid);
			
			
		//}
			
			print.closePrint();
		
		
	}
}
