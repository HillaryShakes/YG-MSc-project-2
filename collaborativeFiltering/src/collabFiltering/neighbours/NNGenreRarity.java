package collabFiltering.neighbours;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import collabFiltering.Pair;
import collabFiltering.TableUsers;
import collabFiltering.Things;
import collabFiltering.users.User;
import collabFiltering.users.UserGenres;

public class NNGenreRarity implements NearestNeighbours{
	
	private TableUsers answersTable;
	private Things thingsTable;
	private String genre;


	public NNGenreRarity(TableUsers answersTable, Things thingsTable, String genre){
		this.answersTable = answersTable;
		this.thingsTable = thingsTable;
		this.genre = genre;
	}

	@Override
	public List<Pair> getNeighbours(int numNeighbours, int pmxid) {
		//check ok
		UserGenres user = (UserGenres) answersTable.getUser(pmxid);
		Set<String> userKeys = user.getRatingsTable().keySet();
		//make set only of ratings for this genre
		Set<String> userGenreKeys = new HashSet<String>();
		for (String key : userKeys){
			if (thingsTable.getGenres(key).contains(genre)){
				userGenreKeys.add(key);
			}
		}
		
		List<Pair> neighbours = new ArrayList<Pair>(numNeighbours);
		Pair init = new Pair(pmxid,0.0);
		for (int i = 0; i < numNeighbours; i++){
			neighbours.add(init);
		}
		
		
		double k;
		for (int id : answersTable.userList){
			
			User thisUser = answersTable.getUser(id);
			k = 0.0;
			
			for (String item : userGenreKeys){
				if (thisUser.getRatingsTable().containsKey(item)){
					//add score for item depending on how rare it is
					double rarity = 100.0/thingsTable.getCount(item);
					
					/* the rarity is effectively multiplied by 100 as the 
					 * numbers would just be so small. 
					 * Ten is then added as the rarity is not the only important
					 * factor and the fact that the user has this item in common
					 * at all still deserves a decent score, i.e this factor, that
					 * could be varied is just to decrease the weighting of the rarity */
					 
					k += (10 + rarity);	
				}
				
			}
			
			
			for (int i = 0; i < numNeighbours; i ++){
				if (k > neighbours.get(i).getValue()){
					//add user in the right place
					Pair newEntry = new Pair(id, k);
					neighbours.add(i, newEntry);
					//remove last entry, keeping the right length.
					neighbours.remove(numNeighbours);
					break;
				}
			}
			
		
			
		}
		return neighbours;
	}

}
