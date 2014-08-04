package genreBasedRecommendation;

import java.io.Serializable;
import java.sql.Connection;
import java.util.Hashtable;
import java.util.Set;

public class ThingsSerializable implements Serializable{
	
	/* number of ratings each item got */
	private Hashtable<String, Integer> countTable;
	/* the set of genres each item belongs to */
	private Hashtable<String, Set> itemGenreTable;
	/* the set of items that belong to each genre */
	private Hashtable<String, Set> genreItemSet;
	private Hashtable<String, Integer> genreCountTable;
	/* all the genres with the types of relationship. Eg. 'movies' has type 'type', 
	 * a specific director would have type 'directed by' etc */
	private Hashtable<String, String> genreTypeTable;
	
	public ThingsSerializable(Hashtable<String, Integer> countTable, Hashtable<String, Set> itemGenreTable, 
			Hashtable<String, Set> genreItemSet, Hashtable<String, Integer> genreCountTable, Hashtable<String, String> genreTypeTable){
		this.countTable = countTable;
		this.itemGenreTable = itemGenreTable;
		this.genreItemSet = genreItemSet;
		this.genreCountTable = genreCountTable;
		this.genreTypeTable = genreTypeTable;
			
	}

	public Hashtable<String, Integer> getCountTable(){
		return countTable;
	}
	
	public Hashtable<String, Set> getItemGenreTable(){
		return itemGenreTable;
	}
	
	public Hashtable<String, Set> getGenreItemSet(){
		return genreItemSet;
	}
	
	public int getCount(String thing_uuid){
		return countTable.get(thing_uuid);
	}
	
	public Set<String> getGenres(String thing_uuid){
		return itemGenreTable.get(thing_uuid);
	}
	
	public Set getItems(String genre){
		return genreItemSet.get(genre);
	}
	
	public int getGenreCount(String genre){
		return genreCountTable.get(genre);
	}
	
	public String getGenreType(String genre){
		return genreTypeTable.get(genre);
	}
}
