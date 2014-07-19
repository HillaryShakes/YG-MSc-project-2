package collabFiltering.recommenders;

/**
 * Serendipitous rec
 * Items are chosen initially as all items rated by neighbours
 * that were not rated by the current user. Then these are scored
 * according to how many of the neighbours rated them. The N highest
 * scored are then output as a Set of the thing_uuid strings.
 */

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import collabFiltering.Pair;
import collabFiltering.TableUsers;
import collabFiltering.Things;
import collabFiltering.users.User;

public class RecRarity implements Recommend {
	
	private TableUsers answersTable;
	private Things thingsTable;
	private Set<String> recommendationIDs;
	
	public RecRarity(TableUsers answersTable, Things thingsTable){
		this.answersTable = answersTable;
		this.thingsTable = thingsTable;
		
	}
	
	/*
	 * for keys in user not in the user's already rated items and not
	 * already in set, add to set then add one to count, 
	 * if already in set just add one to count
	 * N.B. this first section is the same as before, get a count for
	 * each item of how often it is seen amoungst neighbours.
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
			User thisUser = answersTable.getUser(pair.getpmx());
			Set<String> newUserKeys = thisUser.getRatingsTable().keySet();
			for (String key : newUserKeys){
				if (userKeys.contains(key)){
					//ignore if item has already been rated by the user
				}else if(recommendList.keySet().contains(key)){
					//if the item is already in the list, up it's count by one
					//as another neighbour agrees on this item
					int current = recommendList.get(key);
					recommendList.put(key, current +1);
				}else{
					recommendList.put(key, 1);
				}
			}
		}
		
		//The second stage makes a new table and copies in each value, adding
		//it's rarity score. This probably isn't the best way though...
		Hashtable<String, Double> recommendList2 = new Hashtable<String, Double>();
		
		for (Entry<String, Integer> entry : recommendList.entrySet()){
			//the item
			String thing_uuid = entry.getKey();
			
			//number on neighbours that rated the item
			int value = entry.getValue();
			
			//number of users in total that rated the item
			int count = thingsTable.getCount(thing_uuid);
			
			//number of neighbours
			int numNeighbours = neighbours.size();
			
			//number of users in total
			int numUsers = answersTable.getAnswersTable().size();
			
			//srendipity score
			double serendipityScore = (((double)value)/numNeighbours) - 
					(((double)count)/numUsers);
			
			//give item a final score that takes into account the value
			//and gives a bonus for a high serendipity score (I have multiplied by
			//number of neighbours just to make it a comparable size to the scores
			//so it can add to this.
			double score = ((double) value) + (serendipityScore*10*numNeighbours);
			recommendList2.put(thing_uuid, score);
			
		}
		
		List<Entry<String, Double>> recommendations = new ArrayList<Entry<String, Double>>(numRecs);

		int run = 0;
		for (Entry<String, Double> entry : recommendList2.entrySet()){
			double score = entry.getValue();
			if (run == 0){
				recommendations.add(entry);
				run ++;
			}else if (run < numRecs){
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
		for (Entry<String, Double> entry : recommendations){
			recommendationIDs.add(entry.getKey());
		}
		return recommendationIDs;
	}

	
}
