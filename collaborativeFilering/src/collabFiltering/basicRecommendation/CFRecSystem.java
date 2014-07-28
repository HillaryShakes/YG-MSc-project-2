package collabFiltering.basicRecommendation;

import java.util.List;
import java.util.Set;

import collabFiltering.NearestNeighbours;
import collabFiltering.Pair;
import collabFiltering.RecSystem;
import collabFiltering.Recommend;
import collabFiltering.TableUsers;
import collabFiltering.printers.Printer;

public class CFRecSystem implements RecSystem{
	
	private NearestNeighbours neighbourhood ;
	private Recommend recommender;
	private TableUsers answersTable;
	private int numNeighbours;
	private List<Pair> neighbours;
	private Set<String> recommendations;
	
	public CFRecSystem(TableUsers answersTable, NearestNeighbours neighbourhood, Recommend recommender,  int numNeighbours){
		this.neighbourhood = neighbourhood;
		this.recommender = recommender;
		this.answersTable = answersTable;
		this.numNeighbours = numNeighbours;
	}

	@Override
	public void makeRecommendations(int pmxid, int numRecs) {
		neighbours = neighbourhood.getNeighbours(numNeighbours, pmxid);
		recommendations = recommender.getRecommendations(numRecs, neighbours, pmxid);
		
	}

	public void printRecommendations(Printer print, int pmxid) {
		print.printRecs(pmxid, recommendations);
		
	}

}
