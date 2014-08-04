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
	private int numNeighbours;
	Random rand = new Random(); 
	private int numRecs;
	private Hashtable<String, List<Pair>> oldNeighbours;
	private List<PairString> uselessGenres;
	Hashtable<String, Double> genresTable;

	public GenreRecSystem( TableUsers answersTable, Things thingsTable, int numNeighbours){
		this.answersTable = answersTable;
		this.thingsTable = thingsTable;
		this.numNeighbours = numNeighbours;
		recommendations = new HashSet<String>();
	}
	
	@Override
	public void makeRecommendations(int pmxid, int numRecs) {
		this.numRecs = numRecs;
		Hashtable<String, Double> extraRecs = new Hashtable<String, Double>();
		 /** work out genres*/
		//might want to surround with try catch because needs to be usergenres
		/* get user*/
		UserGenresSerializable user =  answersTable.getUser(pmxid);
		/* get their scores for all their genres*/
		genresTable = user.getGenresTable();
		/* this will be their top N scored genres for now take N as the number
		 * of recs since this should be sufficient as there would be a minimum number
		 * of one rec from each genre*/
		List<PairString> potentialGenre = new ArrayList<PairString>(numRecs);
		
		/* initialise to be a random set of N genres in genresTable 
		 * - or just mother genre but give all a score of zero so they 
		 * are easily overtaken.
		 */
		for (int i = 0; i < 20; i++){
			/* don't really think it should be a random one actually */
			//int k = rand.nextInt((genresTable.keySet().size()) -1);
			//List<String> genreList = new ArrayList<String>(genresTable.keySet());
			//String randomGenre = genreList.get(k);
			//PairString init = new PairString(randomGenre, 0.0);
			String movies = "b9be3802-a904-11e1-9412-005056900141";
			PairString init = new PairString(movies, 0.0);
			potentialGenre.add(init) ;
		}
		String total = "total";
		for (String genre : genresTable.keySet()){
			 /* choose the top N rated genres from their genrelist */
			if (genre.equals(total)){
			}else{
				double l = score(genre);
				//System.out.println("genre: " +  genre);
				//System.out.println("userProp: " + userProp + " rarityScore: " + rarityScore + " l: " + l);
				for (int i = 0; i < 20; i ++){
					if (l > potentialGenre.get(i).getValue()){
						PairString newEntry = new PairString(genre, l);
						potentialGenre.add(i, newEntry);
						potentialGenre.remove(20);
						break;
					}
				}
			}
		}
		/* keep track of the useless genres so you can take them out second time */
		uselessGenres = new ArrayList<PairString>();
		oldNeighbours = new Hashtable<String, List<Pair>>();
		int i = 0;
		while(recommendations.size() < numRecs){
			
			//System.out.println("numRecsLeft: " + (numRecs - recommendations.size()));
			
			if( ((double) numRecs - recommendations.size()) < (((double) potentialGenre.size())/2) || i > numRecs ){
				break;
			}
			
			/* calculate scores */
			calculateScores(potentialGenre, uselessGenres);
		
			/* recommend proportionally to interest in genres */
			recommend(potentialGenre, pmxid);
			i ++;
			
		}
		
		/* if for some reason it was unable to come up with the correct
		 * number of recommendations, fill out the rest from just nearest 
		 * neighbours within movies. */
		int numN = numNeighbours;
		int j = 0;
		while (recommendations.size() < numRecs){
			if (j > 100){
				System.out.println("too many loops " + pmxid);
				break;
			}
		String motherGenre = "b9be3802-a904-11e1-9412-005056900141"; 
		/* choose neighbours in movies overall and recommend as usual from this to fill
		 * out what extra recs are needed */
		NNGenreRarity nn = new NNGenreRarity(answersTable, thingsTable, motherGenre);
		RecGenreCI rec = new RecGenreCI(answersTable, motherGenre, thingsTable, recommendations);
		List<Pair> neighbours = nn.getNeighbours(numN, pmxid);
		int numExtraRecs = numRecs - recommendations.size();
		Set<String> genreRecs = rec.getRecommendations(numExtraRecs, neighbours, pmxid);
		for (String recommendation : genreRecs){
			recommendations.add(recommendation);
			//System.out.println("extra: " + recommendation);
		}
		numN += 1;
		j ++;
		}
	}
	
	public Set<String> getRecommendations(){
		return recommendations;
	}
	
	
	private double score(String genre){
		/** TODO score genres according to rarity of genre */
		double userProp = (((double) genresTable.get(genre))/genresTable.get("total"));
		double totProp = ((double) thingsTable.getGenreCount(genre))/thingsTable.getGenreCount("total");
		//double rarityScore = userProp * (userProp / totProp);
		//System.out.println("genre: " + genre + " prop: " + (userProp/totProp) + " original: " + userProp);
		double rarityScore = Math.min(userProp * (userProp / totProp), userProp * 50);
		double l = userProp + rarityScore;
		//double l = rarityScore;
		return l;
	}
	
	private List<PairString> calculateScores(List<PairString> potentialGenre, List<PairString> uselessGenres){
		for (PairString genre : uselessGenres){
				potentialGenre.remove(genre);
		}
		for (PairString genre : potentialGenre){
			double l = score(genre.getItem());
		genre.setValue(l);
		}
		double totalGenreScores = 0.0;
		for (PairString genre : potentialGenre){
			totalGenreScores += genre.getValue();
		}
		/* work out the proportions of each of their top N genres */
		for (PairString genre : potentialGenre){
			genre.setValue(genre.getValue()/totalGenreScores);
			//System.out.println("genre score: " + genre.getItem() + " , " + genre.getValue());
		}
		return potentialGenre;
		}

	private void recommend(List<PairString> potentialGenre, int pmxid){
		int numRecsNeeded = numRecs - recommendations.size();
				for (PairString genre : potentialGenre){
					/* work out number of recs to produce from this genre */
					int numGenRecs = (int) Math.round( genre.getValue()*numRecsNeeded);
					//System.out.println("genre: " + genre.getItem() + " number :" + numGenRecs);
					
					if (numGenRecs + recommendations.size() > numRecs){
						numGenRecs = numRecs - recommendations.size();
					}
					
					if (numGenRecs + recommendations.size() <= numRecs){
					/* choose neighbours within this genre */
					List<Pair> neighbours;
						if (oldNeighbours.keySet().contains(genre.getItem())){
							neighbours = oldNeighbours.get(genre.getItem());
						}else{
							NNGenreRarity nn = new NNGenreRarity(answersTable, thingsTable, genre.getItem());
							neighbours = nn.getNeighbours(numNeighbours, pmxid);
						}
						//System.out.println("genre: " + genre.getItem());
						//for (Pair neighbour : neighbours){
						//	System.out.println(neighbour.getpmx());
						//}
					
					/* set up a recommender giving it the recommendations so far so as not to 
					 * repeat items in multiple genres */
					RecGenreCI rec = new RecGenreCI(answersTable, genre.getItem(), thingsTable, recommendations);
					/* produce recommendations from unusual overlap within genre of neighbours */
					Set<String> genreRecs = rec.getRecommendations(numGenRecs, neighbours, pmxid);
					for (String recommendation : genreRecs){
						recommendations.add(recommendation);
						//System.out.println(recommendation);
					}
					
					//System.out.println("numRecsProdced: " + genreRecs.size() + " numGenRecs: " + numGenRecs);
					if (genreRecs.size() < numGenRecs){
						uselessGenres.add(genre);	
					}else{
						oldNeighbours.put(genre.getItem(), neighbours);
					}
					//numRecsNeeded = numRecs - recommendations.size();
					}
					}
	}
	
	
	@Override
	public void printRecommendations(Printer print, int pmxid) {
		print.printRecs(pmxid, recommendations);
		
	}

}
