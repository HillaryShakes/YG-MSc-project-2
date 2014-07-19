package collabFiltering;

import java.util.List;
import java.util.Set;

public class CFRecSystem implements RecSystem{
	
	private NearestNeighbours neighbourhood ;
	private Recommend recommender;
	private TableUsers answersTable;
	private int numNeighbours;
	private int numRecs;
	private List<Pair> neighbours;
	private Set<String> recommendations;
	
	public CFRecSystem(TableUsers answersTable, NearestNeighbours neighbourhood, Recommend recommender,  int numNeighbours, int numRecs){
		this.neighbourhood = neighbourhood;
		this.recommender = recommender;
		this.answersTable = answersTable;
		this.numNeighbours = numNeighbours;
		this.numRecs = numRecs;
	}

	@Override
	public void makeRecommendations(int pmxid) {
		neighbours = neighbourhood.getNeighbours(numNeighbours, pmxid);
		recommendations = recommender.getRecommendations(numRecs, neighbours, pmxid);
		
	}

	public void printRecommendations(Printer print, int pmxid) {
		print.printRecs(pmxid, recommendations);
		
	}

}
