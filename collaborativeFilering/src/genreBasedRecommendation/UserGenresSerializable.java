package genreBasedRecommendation;

import java.io.Serializable;
import java.sql.Connection;
import java.util.Hashtable;
import java.util.Set;

import collabFiltering.Things;
import collabFiltering.User;

public class UserGenresSerializable implements Serializable{
	private Hashtable<String, Integer> ratings;
	private Hashtable<String, Double> genres; 
	private Hashtable<String, Set<String>> genreItems; 
	private int pmxid;
	
	public UserGenresSerializable(int pmxid, Hashtable<String, Integer> ratings, Hashtable<String, Double> genres, Hashtable<String, Set<String>> genreItems){
		this.pmxid = pmxid;
		this.ratings = ratings;
		this.genres = genres;
		this.genreItems = genreItems;
	}


	public Hashtable<String, Integer> getRatingsTable() {
		return ratings;
	}


	public int getpmxid() {
		return pmxid;
	}


	public Integer getRating(String thing_uuid) {
		return ratings.get(thing_uuid);
	}


	public int length() {
		return ratings.size();
	}


	public Hashtable<String, Double> getGenresTable() {
		return genres;
	}


	public Double getGenreRating(String genre) {
		return genres.get(genre);
	}
	


	public Set<String> getItemsFromGenre(String genre) {
		return genreItems.get(genre);
	}

}
