package genreBasedRecommendation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;
import java.util.Map.Entry;
import java.util.Set;

import collabFiltering.Pair;
import collabFiltering.PairString;
import collabFiltering.RecSystem;
import collabFiltering.TableUsers;
import collabFiltering.Things;
import collabFiltering.printers.Printer;

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
	Random rand = new Random(); 

	public GenreRecSystem( TableUsers answersTable, Things thingsTable){
		this.answersTable = answersTable;
		this.thingsTable = thingsTable;
		recommendations = new HashSet<String>();
	}
	
	@Override
	public void makeRecommendations(int pmxid, int numRecs) {
		int totalGenRecs = 0;
		Hashtable<String, Double> extraRecs = new Hashtable<String, Double>();
		 /** work out genres*/
		//might want to surround with try catch because needs to be usergenres
		/* get user*/
		UserGenres user = (UserGenres) answersTable.getUser(pmxid);
		/* get their scores for all their genres*/
		Hashtable<String, Double> genresTable = user.getGenresTable();
		/* this will be their top N scored genres for now take N as the number
		 * of recs since this should be sufficient as there would be a minimum number
		 * of one rec from each genre*/
		List<PairString> potentialGenre = new ArrayList<PairString>(numRecs);
		
		/* initialise to be a random set of N genres in genresTable but give all
		 * all a score of zero so they are easily overtaken.
		 */
		for (int i = 0; i < numRecs; i++){
			int k = rand.nextInt((genresTable.keySet().size()) -1);
			List<String> genreList = new ArrayList<String>(genresTable.keySet());
			String randomGenre = genreList.get(k);
			PairString init = new PairString(randomGenre, 0.0);
			potentialGenre.add(init) ;
		}
		
		for (String genre : genresTable.keySet()){
			 /* choose the top N rated genres from their genrelist */
			if (genre != "total"){
				/** TODO score genres according to rarity of genre */
				//double l = genresTable.get(genre);
				//double l = (genresTable.get(genre)/genresTable.get("total"));
				double l = (genresTable.get(genre)/genresTable.get("total"))*(genresTable.get(genre)/genresTable.get("total")) 
						/ thingsTable.getGenreCount(genre)/thingsTable.getGenreCount("total");
				for (int i = 0; i < numRecs; i ++){
					if (l > potentialGenre.get(i).getValue()){
						PairString newEntry = new PairString(genre, l);
						potentialGenre.add(i, newEntry);
						potentialGenre.remove(numRecs);
						break;
					}
				}
			}
		}

		double totalGenreScores = 0.0;
		for (PairString genre : potentialGenre){
			totalGenreScores += genre.getValue();
		}
		/* work out the proportions of each of their top N genres */
		for (PairString genre : potentialGenre){
			genre.setValue(genre.getValue()/totalGenreScores);
		}
		/* recommend proportionally to interest in genres */
		for (PairString genre : potentialGenre){
			/* work out number of recs to produce from this genre */
			int numGenRecs = (int) Math.round( genre.getValue()*numRecs);
			System.out.println("genre: " + genre.getItem() + " number :" + numGenRecs);
			if (recommendations.size() + numGenRecs > numRecs){
				numGenRecs = numRecs - recommendations.size();
			}
			if (recommendations.size() + numGenRecs < numRecs){
			/* choose neighbours within this genre */
			NNGenreRarity nn = new NNGenreRarity(answersTable, thingsTable, genre.getItem());
			/* set up a recommender giving it the recommendations so far so as not to 
			 * repeat items in multiple genres */
			RecGenreCI rec = new RecGenreCI(answersTable, genre.getItem(), thingsTable, recommendations);
			/* produce recommendations from unusual overlap within genre of neighbours */
			List<Pair> neighbours = nn.getNeighbours(numNeighbours, pmxid);
			for (Pair neighbour : neighbours){
				//System.out.println(neighbour.getpmx() + " , " + neighbour.getValue());
			}
			Set<String> genreRecs = rec.getRecommendations(numGenRecs, neighbours, pmxid);
			for (String recommendation : genreRecs){
				recommendations.add(recommendation);
				System.out.println(recommendation);
			}
			}
			}
		
		
		/* if for some reason it was unable to come up with the correct
		 * number of recommendations, fill out the rest from just nearest 
		 * neighbours within movies. */
		while (recommendations.size() < numRecs){
		String motherGenre = "b9be3802-a904-11e1-9412-005056900141"; 
		/* choose neighbours in movies overall and recommend as usual from this to fill
		 * out what extra recs are needed */
		NNGenreRarity nn = new NNGenreRarity(answersTable, thingsTable, motherGenre);
		RecGenreCI rec = new RecGenreCI(answersTable, motherGenre, thingsTable, recommendations);
		List<Pair> neighbours = nn.getNeighbours(numNeighbours, pmxid);
		int numExtraRecs = numRecs - recommendations.size();
		Set<String> genreRecs = rec.getRecommendations(numExtraRecs, neighbours, pmxid);
		for (String recommendation : genreRecs){
			recommendations.add(recommendation);
			System.out.println("extra: " + recommendation);
		}
		
	}
		
	}

	@Override
	public void printRecommendations(Printer print, int pmxid) {
		print.printRecs(pmxid, recommendations);
		
	}

}
