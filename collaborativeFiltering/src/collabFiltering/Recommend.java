package collabFiltering;


import java.util.List;
import java.util.Set;

public interface Recommend {
	public Set<String> getRecommendations(int numRecs, List<Pair> neighbours, int pmxid);

}
