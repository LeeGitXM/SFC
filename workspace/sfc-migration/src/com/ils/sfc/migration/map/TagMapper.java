package com.ils.sfc.migration.map;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * Convert a GSI names into Ignition Tag Paths
 */
public class TagMapper {
	private static String TAG_PROVIDER_PREF_KEY = "TagProvider";
	private final Map<String,String> tagMap;     // Lookup by G2 classname
	/** 
	 * Constructor: 
	 */
	public TagMapper() {
		tagMap = new HashMap<>();
	}
	
	
	/**
	 * Perform a database lookup to create a map of G2
	 * block names to Ignition blocks. We create an additional
	 * map of preferences - in particular we're after the tag 
	 * provider name.
	 * @param cxn open database connection
	 */
	public void createMap(Connection cxn) {
		ResultSet rs = null;
		try {
			Statement statement = cxn.createStatement();
			statement.setQueryTimeout(30);  // set timeout to 30 sec.
			
			rs = statement.executeQuery("select * from TagMap");
			while(rs.next())
			{
				String gsi = rs.getString("GSIName");
				String tagPath = rs.getString("TagPath");
				tagMap.put(gsi.toLowerCase(), tagPath.trim());
				//System.err.println(TAG+".setTagPaths: MAP "+gsi+":"+tagPath);
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
	 * Lookup is case insensitive.
	 * @param g2Name
	 * @return
	 */
	public String getTagPath(String g2Name) {
		return tagMap.get(g2Name.toLowerCase());
	}

}
