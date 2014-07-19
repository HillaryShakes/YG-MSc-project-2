package collabFiltering;

import java.util.Hashtable;

public interface User {
	public Hashtable<String, Integer> getRatingsTable();
	public int getpmxid();
	public Integer getRating(String thing_uuid);
	public int length();
	public void closeCon();

}
