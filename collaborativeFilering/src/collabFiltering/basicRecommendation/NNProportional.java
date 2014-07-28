package collabFiltering.basicRecommendation;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import collabFiltering.NearestNeighbours;
import collabFiltering.Pair;
import collabFiltering.TableUsers;
import collabFiltering.User;


/**
 * nearest neighbours proportionally 
 * Add up the number of items each potential neighbour has rated that 
 * the user in question has also rated then that is divided by the 
 * total number that the neighbour had rated to give a similarity that
 * is the proportion of items a user has rated that they have in common 
 * with the user. 
 * Choose N most similar and output in a list of their pmxid-score pairs. 
 */



public class NNProportional implements NearestNeighbours{
	
	private TableUsers answersTable;


	public NNProportional(TableUsers answersTable){
		this.answersTable = answersTable;
		
	}

	@Override
	public List<Pair> getNeighbours(int numNeighbours, int pmxid) {
		
		User user = answersTable.getUser(pmxid);
		Set<String> userKeys = user.getRatingsTable().keySet();
		
		/*
		 * Initialise vector of ten of the current user, each with a score of zero.
		 * This should mean that all are replaced with users with a higher similarity
		 * score. However in the situation where they are not all replaced this should
		 * not affect the final recommendations as the current user cannot contribute
		 * any new items and this way will not throw an error as the pmxid is valid.
		 */
		
		List<Pair> neighbours = new ArrayList<Pair>(numNeighbours);
		Pair init = new Pair(pmxid,0.0);
		for (int i = 0; i < numNeighbours; i++){
			neighbours.add(init);
		}
		
		
		int k;
		//go through all users and evaluate their similarity
		for (int id : answersTable.userList){
			
			User thisUser = answersTable.getUser(id);
			k = 0;
			
			//count the number of items they have in common with the user
			for (String item : userKeys){
				if (thisUser.getRatingsTable().containsKey(item)){
					k ++;	
				}
				
			}
			
			//divide by number of ratings that user has so have as a proportion
			double l = ((double) k )*k/ thisUser.length();
			
			

			if ( l < 1){
			for (int i = 0; i < numNeighbours; i ++){
				if (l > neighbours.get(i).getValue()){
					//add user in the right place
					Pair newEntry = new Pair(id, l);
					neighbours.add(i, newEntry);
					//remove last entry, keeping the right length
					neighbours.remove(numNeighbours);
					break;
				}
			}
			}
			
			
		}
		return neighbours;
	}
	
}

