package genreBasedRecommendation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import collabFiltering.NearestNeighbours;
import collabFiltering.Pair;
import collabFiltering.TableUsers;
import collabFiltering.Things;
import collabFiltering.User;

public class NNGenreRarity implements NearestNeighbours{
	
	private TableUsers answersTable;
	private Things thingsTable;
	private String genre;
	Random rand = new Random(); 


	public NNGenreRarity(TableUsers answersTable, Things thingsTable, String genre){
		this.answersTable = answersTable;
		this.thingsTable = thingsTable;
		this.genre = genre;
	}

	@Override
	public List<Pair> getNeighbours(int numNeighbours, int pmxid) {
		//check ok
		UserGenres user = (UserGenres) answersTable.getUser(pmxid);
		/* make set of user ratings for this genre */
		Set<String> userGenreKeys = user.getItemsFromGenre(genre);
		/* make list of N nearest neighbours */
		List<Pair> neighbours = new ArrayList<Pair>();
		/* initialise to be a random set of N neighbours in userlist but give all
		 * all a score of zero so they are easily overtaken.
		 */
		for (int i = 0; i < numNeighbours; i++){
			int k = rand.nextInt((answersTable.userList.size()) -1);
			Pair init = new Pair(answersTable.userList.get(k), 0.0);
			neighbours.add(init) ;
		}
		
		
		double k;
		for (int id : answersTable.userList){
			
			UserGenres thisUser = (UserGenres) answersTable.getUser(id);
			k = 0.0;
			int j = 0;
			
			if (thisUser.getGenresTable().containsKey(genre)){
			for (String item : userGenreKeys){
				if (thisUser.getItemsFromGenre(genre).contains(item)){
					/* add score for item depending on how rare it is */
					/** TODO fiddle with good rarity score */
					double rarity = answersTable.getAnswersTable().size()/thingsTable.getCount(item);
					//System.out.println("rarity: " + rarity);
					/* add up score for user */
					//System.out.println(rarity);
					k += Math.min(rarity, 100);
					//k += 1;
					/* keep track of how many items in common,
					 * if neighbour won't add anything new, don't need them */
					j += 1;
				}
				
			}
			/* decrease size proportional to length of potential neighbour
			 * so people that happen to have rated lots don't end up being everyone's 
			 * neighbour */
			/** TODO fiddle with weighting of proportional to shortness */
			//double l = ((double) k )*k/ thisUser.length();
			//this is too much helping short ones
			//double l = k;
			double l = ((double) k )/ (Math.max(thisUser.getItemsFromGenre(genre).size(), 10));
					//((double) thingsTable.getGenreItemSet().get(genre).size())/100));
			//double l = k;

			/* check that user will add something, i.e. need x < 1 */
			double x = ((double) j) / thisUser.getItemsFromGenre(genre).size();
			//System.out.println(thisUser.getpmxid() + " , " + x);
			
			if (x < 1.0){
			for (int i = 0; i < numNeighbours; i ++){
				if (l > neighbours.get(i).getValue()){
					/* add user in the right place */
					Pair newEntry = new Pair(id, l);
					neighbours.add(i, newEntry);
					/* remove last entry, keeping the right length. */
					neighbours.remove(numNeighbours);
					break;
				}
			}
			}
			
			}
		}
		/* add one random neighbour to end so if nighbours for a particular topic don't
		 * give any new ratings, you don't get stuck with the same things genreated
		 * each time and ultimately too few ratings. */
			
			/*boolean neighbourNeeded = true; 
			while(neighbourNeeded){
			int i = rand.nextInt((answersTable.userList.size()) -1); 
			Pair newEntry = new Pair(answersTable.userList.get(i), 0.0);
			if(answersTable.getUser(newEntry.getpmx()).getGenresTable().containsKey(genre)){
				neighbours.add(numNeighbours -1 , newEntry);
				neighbours.remove(numNeighbours);
				neighbourNeeded = false;
			}
			}*/
			
		
		return neighbours;
	}

}
