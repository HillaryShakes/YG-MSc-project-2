package makeAnswersMatrix;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;
import java.io.FileWriter;
import java.io.IOException;

public class Printer {
	
	FileWriter writer;

	
	public Printer(String filename, int numRecs){
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
	
	public void printRecs(int pmxid, Set<String> recommendationIDs) {
		Connection cxn = null;

    	try {
			cxn = DriverManager.getConnection(
					"jdbc:postgresql://127.0.0.1:5432/postgres", "postgres",
					"yougov");
			
		} catch (SQLException e) {
			System.out.println("Connection Failed in User");
			e.printStackTrace();
		
		}
    	try {
			 
			Class.forName("org.postgresql.Driver");

		} catch (ClassNotFoundException e) {

			System.out.println("Where is your PostgreSQL JDBC Driver? "
					+ "Include in your library path!");
			e.printStackTrace();
			return;
		}
    	
    	System.out.println("Recommendations for user " + pmxid + ":");
		
		for (String item: recommendationIDs) {
        	try {
				Statement st = cxn.createStatement();
				String query = "SELECT namex FROM yougov.taxonomy WHERE thing_uuid = '" + item + "'";
				ResultSet rs = st.executeQuery(query);
					while (rs.next()){
						System.out.println(rs.getString("namex"));
					}
					
					rs.close();
				
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
		
		}
		
		
		
	}

	public void csvRecs(int pmxid, Set<String> recommendationIDs){
		try
		{
		    Connection cxn = null;
	    	try {
				cxn = DriverManager.getConnection(
						"jdbc:postgresql://127.0.0.1:5432/postgres", "postgres",
						"yougov");
				
			} catch (SQLException e) {
				System.out.println("Connection Failed in User");
				e.printStackTrace();
			
			}
	    	try {
				 
				Class.forName("org.postgresql.Driver");

			} catch (ClassNotFoundException e) {

				System.out.println("Where is your PostgreSQL JDBC Driver? "
						+ "Include in your library path!");
				e.printStackTrace();
				return;
			}
	    	
	    	writer.append("" + pmxid);
	    	writer.append(",");
			
			for (String item: recommendationIDs) {
	        	try {
					Statement st = cxn.createStatement();
					String query = "SELECT namex FROM yougov.taxonomy WHERE thing_uuid = '" + item + "'";
					ResultSet rs = st.executeQuery(query);
						while (rs.next()){
							writer.append("" + (rs.getString("namex")));
							writer.append(',');
						}
						
						rs.close();
					
					
				} catch (SQLException e) {
					e.printStackTrace();
				}
	        	
			}
			writer.append("\n");
		    
		
		}
		catch(IOException e)
		{
		     e.printStackTrace();
		} 
	}
	
	public void closecsvPrint(){
		
		try {
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
		
	}
}
