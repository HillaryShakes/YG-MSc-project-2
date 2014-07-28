package collabFiltering.printers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;

public class MiniPrinter implements Printer{
	
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

	@Override
	public void closePrint() {
		// nothing 
		
	}

}
