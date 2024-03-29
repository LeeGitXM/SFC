package com.ils.sfc.migration.map;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * Convert a G2 classname into a SFC classname
 */
public class ClassNameMapper {
	private final Map<String,String> classMap;        // Lookup by G2 classname
	private final Map<String,Boolean> transitionMap;  // Lookup by Ignition factory id
	// Legal types are: enclosure, transition, ""
	private final Map<String,String> typeMap;         // Lookup by G2 classname
	
	/** 
	 * Constructor: 
	 */
	public ClassNameMapper() {
		classMap = new HashMap<>();
		// Holds true if an explicit transition is required
		transitionMap = new HashMap<>();
		// Legal types are: enclosure, transition, 
		typeMap = new HashMap<>();
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
			
			rs = statement.executeQuery("select * from SfcClassMap");
			while(rs.next())
			{
				String g2 = rs.getString("G2Class");
				String ignition = rs.getString("FactoryId");
				classMap.put(g2, ignition);
				Boolean requiresTransition = new Boolean((rs.getInt("RequiresTransition")>0));
				transitionMap.put(g2, requiresTransition);
				String type = rs.getString("Type");
				typeMap.put(g2, type);
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
	 * @return whether or not the ignition block requires a transition
	 */
	public boolean classRequiresTransition(String claz) {
		boolean required = false;
		if( transitionMap.get(claz)!=null ) {
			required = transitionMap.get(claz).booleanValue();
		}
		return required;
	}
	
	/**
	 * 
	 * @param className the G2 class name
	 * @return whether or not this class is an enclosure
	 */
	public boolean isEncapsulation(String className) {
		boolean flag = false;  // default
		String result = typeMap.get(className);
		if( result!=null ) {
			flag = result.equalsIgnoreCase("enclosure");
		}
		return flag;
	}
	
	/**
	 * 
	 * @param className the G2 class name
	 * @return whether or not this class is a transition
	 */
	public boolean isParallel(String className) {
		boolean flag = false;  // default
		String result = typeMap.get(className);
		if( result!=null ) {
			flag = result.equalsIgnoreCase("parallel");
		}
		return flag;
	}
	/**
	 * 
	 * @param className the G2 class name
	 * @return whether or not this class is a transition
	 */
	public boolean isTransition(String className) {
		boolean flag = false;  // default
		String result = typeMap.get(className);
		if( result!=null ) {
			flag = result.equalsIgnoreCase("transition");
		}
		return flag;
	}
}
