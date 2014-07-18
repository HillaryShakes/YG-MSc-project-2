package collabFiltering;

public class Pair<user, value>{
	private int user;
	private double value;
	
	public Pair(int user, double value){
		this.user = user;
		this.value = value;
	}
	
	public int getpmx(){
		return user;
	}
	
	public double getValue(){
		return value;
	}
	
}
