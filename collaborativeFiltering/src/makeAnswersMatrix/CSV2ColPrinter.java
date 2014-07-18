package makeAnswersMatrix;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

public class CSV2ColPrinter implements Printer{
FileWriter writer;

	
	public CSV2ColPrinter(String filename, int numRecs){
		try {
			writer = new FileWriter(filename); 
		    writer.append("pmxid");
		    writer.append(',');
		    writer.append("Recommendation");
		    writer.append("\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	

	public void printRecs(int pmxid, Set<String> recommendationIDs){
		
			
			for (String item: recommendationIDs) {
				
				try {
					
					writer.append("" + pmxid);
					writer.append(",");
					writer.append(item);
					writer.append("\n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    
		
			}
		}
		
	
	
	public void closePrint(){
		
		try {
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
		
	}
}
