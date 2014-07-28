package collabFiltering;

import java.util.Hashtable;
import java.util.Set;

public interface User {
	public Hashtable<String, Integer> getRatingsTable();
	public int getpmxid();
	public Integer getRating(String thing_uuid);
	public int length();
	
	public Hashtable<String, Double> getGenresTable();
	public Double getGenreRating(String genre);
	public Set<String> getItemsFromGenre(String genre);

}
