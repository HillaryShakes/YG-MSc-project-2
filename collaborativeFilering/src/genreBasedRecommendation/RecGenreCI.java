package genreBasedRecommendation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import collabFiltering.Pair;
import collabFiltering.Recommend;
import collabFiltering.TableUsers;
import collabFiltering.Things;
import collabFiltering.User;

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
	recommendationIDs = new HashSet<String>(numRecs);
	if (numRecs == 0){
		return recommendationIDs;
	}
		
		/* this is the user we are trying to recommend for */
	//may need try/catch or fix somehow
		UserGenres user = (UserGenres) answersTable.getUser(pmxid);
		
		/* make set only of ratings for this genre */
		Set<String> userGenreKeys = user.getItemsFromGenre(genre);
		
		 /* Make a hash table to keep all potential items that could be recommended
		 * according to their neighbours, i.e. all the items their neighbours have
		 * rated that they haven't and keep a score of how many of their neighbours 
		 * have rated them.*/ 
		Hashtable<String, Integer> recommendList = new Hashtable<String, Integer>();
		
		/* Each neighbour is a pair of the neighbour id and their similarity score */
		for (Pair pair : neighbours){
			UserGenres neighbour = (UserGenres) answersTable.getUser(pair.getpmx());
			Set<String> newUserKeys = neighbour.getItemsFromGenre(genre);
			if(newUserKeys != null){
			for (String key : newUserKeys){
				if (userGenreKeys.contains(key) || recommendations.contains(key)){
					/* ignore if item has already been rated by the user */
				}else if(recommendList.keySet().contains(key)){
					/*if the item is already in the list, up it's count by one
					 *as another neighbour agrees on this item */
					int current = recommendList.get(key);
					recommendList.put(key, current +1);
				}else{
					/*if this is the first occurance of an item,
					 * add it to the list with a count of one. */
					recommendList.put(key, 1);
					}
				
			}
			}
		}
		
		/*
		 * Now make a list of top N recommendations to actually return
		 */
		/* The second stage makes a new table and copies in each value, adding
		 * it's rarity score. This probably isn't the best way though...
		 */
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
					
			//rarity score
			/** TODO fiddle with this score */
			double neighbourProp = ((double)value)/numNeighbours;
			double totalProp = ((double)count)/numUsers;
			double rarityScore = neighbourProp * (neighbourProp / totalProp);
			//double rarityScore = (neighbourProp / totalProp);
			//System.out.println("rarity: " + rarityScore + " count: " + count);	
			/* give item a final score that takes into account the value
			 * and gives a bonus for a high serendipity score (I have multiplied by
			 * number of neighbours just to make it a comparable size to the scores
			 * so it can add to this.*/
			//double score = ((double) value) + (serendipityScore*numNeighbours);
			double score =  rarityScore;
			//double score =  count;
			//double score =  (double) neighbourProp + (rarityScore*100);
			//double score = 1.0 + Math.min(rarityScore, 20.0)*0;
			recommendList2.put(thing_uuid, score);
					
		}
				
		/* find the highest scoring items */
		List<Entry<String, Double>> recommendations = new ArrayList<Entry<String, Double>>(numRecs);

        /* have to keep track of the first N runs to initialise with
		 * the items in the right order */
		int run = 0;
		for (Entry<String, Double> entry : recommendList2.entrySet()){
			double score = entry.getValue();
			/* initialise with first N ordered entries */		
			/* add the first item no matter what */
			if (run == 0){
				recommendations.add(entry);
				run ++;
			}else if (run < numRecs){
				/* add subsequent N-1 items in the right positions
				 * so that the list contains N items in order of 
				 * their scores. */
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
		
		
		/* now can drop the scores and output the top N items */
		for (Entry<String, Double> entry : recommendations){
			recommendationIDs.add(entry.getKey());
		}
		return recommendationIDs;
	}

}
