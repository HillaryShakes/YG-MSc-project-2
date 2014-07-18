package collabFiltering;

import java.util.Set;

public interface Printer {
	public void printRecs(int pmxid, Set<String> recommendationIDs);
	public void closePrint();

}
