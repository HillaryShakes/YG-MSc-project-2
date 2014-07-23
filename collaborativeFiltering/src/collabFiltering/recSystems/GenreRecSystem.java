package collabFiltering.recSystems;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import collabFiltering.TableUsers;
import collabFiltering.Things;
import collabFiltering.neighbours.NNGenreRarity;
import collabFiltering.printers.Printer;
import collabFiltering.recommenders.RecGenreCI;
import collabFiltering.users.UserGenres;

/**
 * N.B assuming you don't have multiple genres. If you do you'd need
 * to make sure you don't have items recommended multiple times so 
 * pass in rec list so far to each genreRec. Also would need to change
 * way you count genre scores.
 * also N.B. no randomness yet
 */

public class GenreRecSystem implements RecSystem{
	
	private TableUsers answersTable;
	private Set<String> recommendations;
	private Things thingsTable;
	private int numNeighbours = 10;
	

	public GenreRecSystem( TableUsers answersTable, Things thingsTable){
		this.answersTable = answersTable;
		this.thingsTable = thingsTable;
		recommendations = new HashSet<String>();
	}
	
	@Override
	public void makeRecommendations(int pmxid, int numRecs) {

		 /* work out genres*/
		//might want to surround with try catch because needs to be usergenres
		//System.out.println("make recs...");
		UserGenres user = (UserGenres) answersTable.getUser(pmxid);
		Hashtable<String, Integer> genresTable = user.getGenresTable();
		for (String genre : genresTable.keySet()){
			 /* work out proportion of num recs to each */
			if (genre != "total"){
			//System.out.println(genre + " : " + genresTable.get(genre));
			//System.out.println("total: " + genresTable.get("total"));
				
			int  numGenRecs = (int) Math.round(numRecs * (((double) genresTable.get(genre))/genresTable.get("total")));
			/**have NN that finds neighbours within genre.
			 * want it to take answersTable and thingsTable and
			 * then refine these to look only at sections of relevant genre
			 * can go through users in usertable as before but
			 */
			//System.out.println("numGenRecs: " + numGenRecs);
			if (numGenRecs >= 1){
			NNGenreRarity nn = new NNGenreRarity(answersTable, thingsTable, genre);
			RecGenreCI rec = new RecGenreCI(answersTable, genre, thingsTable, recommendations);
			//System.out.println("genre: " + genre);
			Set<String> genreRecs = rec.getRecommendations(numGenRecs, nn.getNeighbours(numNeighbours, pmxid), pmxid);
			
			for (String recommendation : genreRecs){
				recommendations.add(recommendation);
				//System.out.println(recommendation);
			}
			}

			}
			
			
		
		
		}
		

		
	}

	@Override
	public void printRecommendations(Printer print, int pmxid) {
		print.printRecs(pmxid, recommendations);
		
	}

}
