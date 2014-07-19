package collabFiltering;

import java.util.Set;

public class GenreRecSystem implements RecSystem{
	
	private TableUsers answersTable;
	private int numRecs;
	private Set<String> recommendations;
	

	@Override
	public void makeRecommendations(int pmxid) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void printRecommendations(Printer print, int pmxid) {
		print.printRecs(pmxid, recommendations);
		
	}

}
