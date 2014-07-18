package makeAnswersMatrix;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/** Choose neighbours based on how unusual their similarities to the users are.
 * For each potential neighbour for each rated item they have in common with the 
 * current user, this item is given a rarity score. These are accumulated to give
 * the potential neighbour a similarity score to the user that values more unusual 
 * items in common more highly. The N users with the highest scores are then output
 * in a list of pmxid-score pairs.
*/


public class NNRarity implements NearestNeighbours{
	
	private Answers answersTable;
	private Things thingsTable;


	public NNRarity(Answers answersTable, Things thingsTable){
		this.answersTable = answersTable;
		this.thingsTable = thingsTable;
	}

	@Override
	public List<Pair> getNeighbours(int numNeighbours, int pmxid) {
		
		User user = answersTable.getUser(pmxid);
		Set<String> userKeys = user.getRatingsTable().keySet();
		
		List<Pair> neighbours = new ArrayList<Pair>(numNeighbours);
		Pair init = new Pair(pmxid,0.0);
		for (int i = 0; i < numNeighbours; i++){
			neighbours.add(init);
		}
		
		
		double k;
		for (int id : answersTable.userList){
			
			User thisUser = answersTable.getUser(id);
			k = 0.0;
			
			for (String item : userKeys){
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

