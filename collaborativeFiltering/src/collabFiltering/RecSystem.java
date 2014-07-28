package collabFiltering;

import collabFiltering.printers.Printer;

public interface RecSystem {
	public void makeRecommendations(int pmxid, int numRecs);
	public void printRecommendations(Printer print, int pmxid);

}
