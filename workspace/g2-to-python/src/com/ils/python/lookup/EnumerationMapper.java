package com.ils.python.lookup;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * Create a map of G2 globals (e.g. symbols) to their values
 */
public class EnumerationMapper {
	private final String TAG = "EnumerationMapper";
	/** 
	 * Constructor: 
	 */
	public EnumerationMapper() {
	}

	/**
	 * For all classes, perform a database lookup to map attribute names.
	 * Key by Ignition name.
	 * 
	 * @param cxn open database connection
	 */
	public Map<String,String> createMap(Connection cxn) {
		// Read the database to create the map.
		ResultSet rs = null;
		HashMap<String,String> constantMap = new HashMap<>();
		try {
			Statement statement = cxn.createStatement();
			statement.setQueryTimeout(30);  // set timeout to 30 sec.

			rs = statement.executeQuery("select * from EnumerationMap");
			while(rs.next())
			{
				String name = rs.getString("G2Name");
				String enumName = rs.getString("EnumerationName");
				String value = rs.getString("Value");
				constantMap.put(name,value);
			}
			rs.close();
		}
		catch(SQLException e) {
			// if the error message is "out of memory", 
			// it probably means no database file is found
			System.err.println(TAG+".createMap "+e.getMessage());
		}
		finally {
			if( rs!=null) {
				try { rs.close(); } catch(SQLException ignore) {}
			}
		}
		return constantMap;
	}
}
	

