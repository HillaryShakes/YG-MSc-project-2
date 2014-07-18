package makeAnswersMatrix;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;
import java.io.FileWriter;
import java.io.IOException;

public class CSVRowsPrinter implements Printer{
	
	FileWriter writer;

	
	public CSVRowsPrinter(String filename, int numRecs){
		try {
			writer = new FileWriter(filename); 
		    writer.append("pmxid");
		    writer.append(',');
		    for (int x= 1; x < numRecs+1; x++){
		    	writer.append(""+ x);
		    	writer.append(',');
		    }
		   
		    writer.append("\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	

	public void printRecs(int pmxid, Set<String> recommendationIDs){
		try {
			writer.append("" + pmxid);
			writer.append(",");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
			
			for (String item: recommendationIDs) {
				
				try {
					writer.append(item);
					writer.append(",");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    
		
			}
			try {
				writer.append("\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
