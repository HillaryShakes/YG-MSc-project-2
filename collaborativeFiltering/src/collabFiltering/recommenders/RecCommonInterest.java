package collabFiltering.recommenders;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import collabFiltering.Pair;
import collabFiltering.TableUsers;
import collabFiltering.users.User;

/**
 * basic common interest rec
 * Items are chosen initially as all items rated by neighbours
 * that were not rated by the current user. Then these are scored
 * according to how many of the neighbours rated them. The N highest
 * scored are then output as a Set of the thing_uuid strings.
 */


public class RecCommonInterest implements Recommend{
	
	private TableUsers answersTable;
	private Set<String> recommendationIDs;



	public RecCommonInterest(TableUsers answersTable){
		this.answersTable = answersTable;
		
	}
	/*
	 * for keys in user not in the user's already rated items and not
	 * already in set, add to set then add one to count, 
	 * if already in set just add one to count
	 */

	@Override
	public Set<String> getRecommendations(int numRecs, List<Pair> neighbours, int pmxid) {
		
		//this is the user we are trying to recommend for
		User user = answersTable.getUser(pmxid);
		
		//their keys are a set of all the items they have rated
		Set<String> userKeys = user.getRatingsTable().keySet();
		
		
		
		 /* Make a hash table to keep all potential items that could be recommended
		 * according to their neighbours, i.e. all the items their neighbours have
		 * rated that they haven't and keep a score of how many of their neighbours 
		 * have rated them.*/ 
		
		Hashtable<String, Integer> recommendList = new Hashtable<String, Integer>();
		
		//each neighbour is a pair consisting of the neighbour (their pmxid) 
		//and their similarity score
		for (Pair pair : neighbours){
			User neighbour = answersTable.getUser(pair.getpmx());
			Set<String> newUserKeys = neighbour.getRatingsTable().keySet();
			for (String key : newUserKeys){
				if (userKeys.contains(key)){
					//ignore if item has already been rated by the user
				}else if(recommendList.keySet().contains(key)){
					//if the item is already in the list, up it's count by one
					//as another neighbour agrees on this item
					int current = recommendList.get(key);
					recommendList.put(key, current +1);
				}else{
					//if this is the first occurance of an item,
					//add it to the list with a count of one.
					recommendList.put(key, 1);
				}
			}
		}
		
		/*
		 * Now make a list of top N recommendations to actually return
		 */
		
		List<Entry<String, Integer>> recommendations = new ArrayList<Entry<String, Integer>>(numRecs);

        //have to keep track of the first N runs to initialise with
		//the items in the right order
		int run = 0;
		for (Entry<String, Integer> entry : recommendList.entrySet()){
			int score = entry.getValue();
			/*
			 * initialise with first N ordered entries
			 */
			
			//add the first item no matter what
			if (run == 0){
				recommendations.add(entry);
				run ++;
			}else if (run < numRecs){
				//add subsequent N-1 items in the right positions
				//so that the list contains N items in order of 
				//their scores.
				boolean notinserted = true;
				for (int i = 0; i < run-1; i ++){
					if (score >= recommendations.get(i).getValue()){
						recommendations.add(i, entry);
						notinserted = false;
						break;
					}
				}
				if (notinserted){
					recommendations.add(entry);
				}
					
				
					run ++;
					
			}else{
				/*
				 * when first N entries have been added, 
				 * subsequent entries are inserted at the correct
				 * place  according to their score
				 * (if they score higher than the lowest score so far)
				 * and the lowest scoring item is removed, keeping the 
				 * size of the list at N. 
				 */
			for (int i = 0; i < numRecs-1; i ++){
				if (score > recommendations.get(i).getValue()){
					recommendations.remove(numRecs-1);
					recommendations.add(i, entry);
					break;
					
				}
			}
			}
			
		
		}
		
		recommendationIDs = new HashSet<String>(numRecs);
		//now can drop the scores and output the top N items
		for (Entry<String, Integer> entry : recommendations){
			recommendationIDs.add(entry.getKey());
		}
		return recommendationIDs;
	}


}
