package collabFiltering.recommenders;


import java.util.List;
import java.util.Set;

import collabFiltering.Pair;

public interface Recommend {
	public Set<String> getRecommendations(int numRecs, List<Pair> neighbours, int pmxid);

}
