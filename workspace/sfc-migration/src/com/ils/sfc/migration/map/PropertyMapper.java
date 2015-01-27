package com.ils.sfc.migration.map;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * Copy property values from G2 blocks into Ignition. Use the 
 * database as a lookup to map property names between the two systems.
 */
public class PropertyMapper {
	private final String TAG = "PropertyMapper";
	private final Map<String,String> propertyMap;     // Lookup by G2 property name
	/** 
	 * Constructor: 
	 */
	public PropertyMapper() {
		propertyMap = new HashMap<String,String>();
	}

	/**
	 * For all classes, perform a database lookup to map attribute names.
	 * Key by Ignition name.
	 * 
	 * @param cxn open database connection
	 */
	public void createMap(Connection cxn) {
		// Read the database to create the map.
		ResultSet rs = null;
		try {
			Statement statement = cxn.createStatement();
			statement.setQueryTimeout(30);  // set timeout to 30 sec.

			rs = statement.executeQuery("select * from PropertyMap");
			while(rs.next())
			{
				String g2Class = rs.getString("G2Class");
				String g2Property = rs.getString("G2Property");
				String iProperty = rs.getString("Name");
				String key = makePropertyMapKey(g2Class,g2Property);
				propertyMap.put(key,iProperty);
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
	}

	
	
	/**
	 * Create the key for lookup in the property map. Simply
	 * concatenate the class name and the property. The key
	 * is case-insensitive.
	 */
	private String makePropertyMapKey(String cname, String pname) {
		String key = cname.toUpperCase()+":"+pname.toUpperCase();
		return key;
	}

}
	

