package com.ils.sfc.migration.map;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Copy property values from G2 blocks into Ignition. Use the 
 * database as a lookup to map property names between the two systems.
 */
public class PropertyMapper {
	private final String TAG = "PropertyMapper";
	private final Map<String,String> propertyMap;     // Lookup by factoryId, Ignition property
	private final Map<String,List<String>> propertiesMap;     // Lookup by factoryId,
	/** 
	 * Constructor: 
	 */
	public PropertyMapper() {
		propertyMap = new HashMap<>();
		propertiesMap = new HashMap<>();
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

			rs = statement.executeQuery("select * from SfcPropertyMap");
			while(rs.next())
			{
				String factoryId = rs.getString("FactoryId");
				String iProperty = rs.getString("Property");
				String g2Property = rs.getString("G2Property");
				String key = makePropertyMapKey(factoryId,iProperty);
				propertyMap.put(key,g2Property);
				List<String> properties = propertiesMap.get(factoryId);
				if( properties==null) {
					properties = new ArrayList<>();
					propertiesMap.put(factoryId,properties);
				}
				properties.add(iProperty);
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

	public String g2Property(String factoryId,String propertyName) {
		String key = makePropertyMapKey(factoryId,propertyName);
		String result = propertyMap.get(key);
		//System.err.println(TAG+".g2Property for "+factoryId+":"+propertyName+"="+result);
		return result;
	}
	
	/**
	 * 
	 * @param factoryId
	 * @return a list of properties appropriate for a class
	 */
	public List<String> getPropertyList(String factoryId) {
		return propertiesMap.get(factoryId);
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
	

