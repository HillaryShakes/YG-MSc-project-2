package collabFiltering.neighbours;

import java.util.List;

import collabFiltering.Pair;

public interface NearestNeighbours {
	
	public List<Pair> getNeighbours(int numNeigbours, int pmxid);
	

}
