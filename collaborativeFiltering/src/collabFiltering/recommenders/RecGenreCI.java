package collabFiltering.recommenders;

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
import collabFiltering.users.UserGenres;

public class RecGenreCI implements Recommend{
	
	private TableUsers answersTable;
	private Set<String> recommendationIDs;
	private String genre;
	private Things things;
	private Set<String> recommendations;


	public RecGenreCI (TableUsers answersTable, String genre, Things things, Set<String> recommendations){
		this.answersTable = answersTable;
		this.genre = genre;
		this.things = things;
		this.recommendations = recommendations;
		
	}

public Set<String> getRecommendations(int numRecs, List<Pair> neighbours, int pmxid) {
		
		//this is the user we are trying to recommend for
	//may need try/catch or fix somehow
		UserGenres user = (UserGenres) answersTable.getUser(pmxid);
		
		//their keys are a set of all the items they have rated
		Set<String> userKeys = user.getRatingsTable().keySet();
		
		//System.out.println("neighbours:");
		//for (Pair userx : neighbours){
			//System.out.println(userx.getpmx());
		//}
		
		//make set only of ratings for this genre
				Set<String> userGenreKeys = new HashSet<String>();
				for (String key : userKeys){ 
					if (things.getGenres(key).contains(genre)){
						//System.out.println("item: " + key + " genre: " + genre);
						userGenreKeys.add(key);
					}
				}
		
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
				if (things.getGenres(key).contains(genre)){
					if (userKeys.contains(key) || recommendations.contains(key)){
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
		}
		
		/*
		 * Now make a list of top N recommendations to actually return
		 */
		//The second stage makes a new table and copies in each value, adding
				//it's rarity score. This probably isn't the best way though...
				Hashtable<String, Double> recommendList2 = new Hashtable<String, Double>();
				
				for (Entry<String, Integer> entry : recommendList.entrySet()){
					//the item
					String thing_uuid = entry.getKey();
					
					//number on neighbours that rated the item
					int value = entry.getValue();
					
					//number of users in total that rated the item
					int count = things.getCount(thing_uuid);
					
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
					double score = ((double) value) + (serendipityScore*numNeighbours);
					recommendList2.put(thing_uuid, score);
					
				}
				
				List<Entry<String, Double>> recommendations = new ArrayList<Entry<String, Double>>(numRecs);

        //have to keep track of the first N runs to initialise with
		//the items in the right order
		int run = 0;
		for (Entry<String, Double> entry : recommendList2.entrySet()){
			double score = entry.getValue();
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
				for (int i = 0; i < numRecs; i ++){
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
		for (Entry<String, Double> entry : recommendations){
			recommendationIDs.add(entry.getKey());
			//System.out.println(entry.getKey() + " , " + entry.getValue());
		}
		return recommendationIDs;
	}

}
