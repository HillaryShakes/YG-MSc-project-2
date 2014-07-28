package collabFiltering;

public class PairString {
	private String item;
	private double value;
	
	public PairString(String item, double value){
		this.item = item;
		this.value = value;
	}
	
	public String getItem(){
		return item;
	}
	
	public double getValue(){
		return value;
	}
	
	public void setValue(double newVal){
		value = newVal;
	}

}
