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
public class ConstantMapper {
	private final String TAG = "ConstantMapper";
	private final Map<String,String> constantMap;     // Lookup by G2 property name
	/** 
	 * Constructor: 
	 */
	public ConstantMapper() {
		constantMap = new HashMap<String,String>();
	}

	/**
	 * For all classes, perform a database lookup to map attribute names.
	 * Key by Ignition name.
	 * 
	 * @param cxn open database connection
	 */
	public HashMap<String,String> createMap(Connection cxn) {
		// Read the database to create the map.
		ResultSet rs = null;
		HashMap<String,String> constantMap = new HashMap<String,String>();
		try {
			Statement statement = cxn.createStatement();
			statement.setQueryTimeout(30);  // set timeout to 30 sec.

			rs = statement.executeQuery("select * from ConstantMap");
			while(rs.next())
			{
				String name = rs.getString("G2Name");
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
	

