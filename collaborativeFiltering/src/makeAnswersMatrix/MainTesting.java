package makeAnswersMatrix;

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
		
		Answers answersTable = new Answers(tableName);
		answersTable.closeCon();
		
		Things thingsTable = new Things(tableName);
		thingsTable.closeCon();
		
		//NearestNeighbours neighbourhood = new NNProportional(answersTable);
		NearestNeighbours neighbourhood = new NNRarity(answersTable, thingsTable);
		Recommend recommender = new RecRarity(answersTable, thingsTable);
		//Recommend recommender = new RecCommonInterest(answersTable);
		//Printer print = new CSV2ColPrinter("printing.csv", numRecs);
		Printer print = new MiniPrinter();
		
		int pmxid = hillary;
		//for (int pmxid : answersTable.userList){
			
			/**  Find the users nearest neighbours	 */
			
			List<Pair> neighbours = neighbourhood.getNeighbours(numNeighbours, pmxid);
			
			/** Get recommendations from neighbours ratings */

			Set<String> recommendations = recommender.getRecommendations(numRecs, neighbours, pmxid);
			
			/** Print recommendations */
			
			
			print.printRecs(pmxid, recommendations);
			
		//}
			
			print.closePrint();
		
		
	}
}
