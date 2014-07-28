package randomRecommendation;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import collabFiltering.RecSystem;
import collabFiltering.printers.Printer;

public class RandomRec implements RecSystem{
	String tableName;
	Connection cxn = null;
	private Set<String> recommendations;
	
	public RandomRec(String tableName, Connection cxn){
		this.tableName = tableName;
		this.cxn = cxn;
		recommendations = new HashSet<String>();
	}

	@Override
	public void makeRecommendations(int pmxid, int numRecs) {
		try {
			Statement stmt = cxn.createStatement();
			//System.out.println("recs for: " + pmxid);
			String query = "SELECT thing_uuid "
					+ "FROM " + tableName
					+ " WHERE random() < 0.01 "
					+ "EXCEPT" 
					+ " SELECT thing_uuid "
					+ "FROM " + tableName
					+ " WHERE users =  " + pmxid
					+ " LIMIT " + numRecs;
			ResultSet recs = stmt.executeQuery(query);
			while (recs.next()){
				String rec = recs.getString("thing_uuid");
				//System.out.println(rec);
				recommendations.add(rec);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	@Override
	public void printRecommendations(Printer print, int pmxid) {
		print.printRecs(pmxid, recommendations);
		
	}

}
