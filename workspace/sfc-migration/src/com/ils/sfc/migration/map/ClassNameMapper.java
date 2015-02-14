package com.ils.sfc.migration.map;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * Convert a G2 classname into a BLT classname
 */
public class ClassNameMapper {
	private static final String TAG = "ClassNameMapper";
	private final Map<String,String> classMap;     // Lookup by G2 classname
	private final Map<String,String> enclosureMap;     // Lookup by G2 classname
	/** 
	 * Constructor: 
	 */
	public ClassNameMapper() {
		classMap = new HashMap<String,String>();
		enclosureMap = new HashMap<String,String>();
	}
	
	
	/**
	 * Perform a database lookup to create a map of G2
	 * block names to Ignition blocks.
	 * @param cxn open database connection
	 */
	public void createMap(Connection cxn) {
		ResultSet rs = null;
		try {
			Statement statement = cxn.createStatement();
			statement.setQueryTimeout(30);  // set timeout to 30 sec.
			
			rs = statement.executeQuery("select * from ClassMap");
			while(rs.next())
			{
				String g2 = rs.getString("G2Class");
				String ignition = rs.getString("FactoryId");
				classMap.put(g2, ignition);
				String encloses = rs.getString("Encloses");
				enclosureMap.put(g2, encloses);
			}
			rs.close();
		}
		catch(SQLException e) {
			// if the error message is "out of memory", 
			// it probably means no database file is found
			System.err.println(e.getMessage());
		}
		finally {
			if( rs!=null) {
				try { rs.close(); } catch(SQLException ignore) {}
			}
		}
	}
	
	
	/**
	 * 
	 * @param className the G2 class name
	 * @return the corresponding Ignition factoryId
	 */
	public String factoryIdForClass(String className) {
		String factId = classMap.get(className);
		return factId;
	}
	
	/**
	 * 
	 * @param className the G2 class name
	 * @return whether or not this class is an enclosure
	 */
	public boolean isClassAnEncapsulation(String className) {
		boolean flag = false;  // default
		String result = enclosureMap.get(className);
		if( result!=null ) {
			flag = result.equalsIgnoreCase("true");
		}
		return flag;
	}
	
}
