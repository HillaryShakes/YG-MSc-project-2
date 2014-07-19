package collabFiltering.recSystems;

import java.util.Hashtable;
import java.util.Set;

import collabFiltering.TableUsers;
import collabFiltering.printers.Printer;
import collabFiltering.users.UserGenres;

public class GenreRecSystem implements RecSystem{
	
	private TableUsers answersTable;
	private Set<String> recommendations;
	

	public GenreRecSystem( TableUsers answersTable){
		this.answersTable = answersTable;
	}
	
	@Override
	public void makeRecommendations(int pmxid, int numRecs) {

		 /* work out genres*/
		//might want to surround with try catch because needs to be usergenres
		UserGenres user = (UserGenres) answersTable.getUser(pmxid);
		Hashtable<String, Integer> genresTable = user.getGenresTable();
		int numRatings = user.getRatingsTable().size();
		for (String genre : genresTable.keySet()){
			 /* work out proportion of num recs to each */
			int  numGenRecs = Math.round(numRecs * (genresTable.get(genre)/numRatings));
			/**have NN that finds neighbours wihin genre.
			 * want it to take answersTable and thingsTable and
			 * then refine these to look only at sections of relevent genre
			 * can go through users in usertable as before but
			 */
		}
		
		 
		 /* call CFRecSystem on these? */

		
	}

	@Override
	public void printRecommendations(Printer print, int pmxid) {
		print.printRecs(pmxid, recommendations);
		
	}

}
