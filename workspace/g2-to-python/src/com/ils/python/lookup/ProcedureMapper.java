package com.ils.python.lookup;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * Convert a G2 classname into a BLT classname
 */
public class ProcedureMapper {
	private static final String TAG = "ProcedureMapper";
	private static final String UNDEFINED_NAME = "UNDEFINED";
	private final Map<String,String> procedureMap;     // Lookup by G2 classname
	/** 
	 * Constructor: 
	 */
	public ProcedureMapper() {
		procedureMap = new HashMap<String,String>();
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
			
			rs = statement.executeQuery("select * from ProcedureMap");
			while(rs.next())
			{
				String g2 = rs.getString("G2Procedure");
				String ignition = rs.getString("IgnitionProcedure");
				procedureMap.put(g2, ignition);
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
	 * Use our map to get the Ignition class name. Set the discovered
	 * name in the ignition block object. On error, print a warning
	 * message and insert a default class name. (This allows us to
	 * continue processing and collect all the errors at once).
	 * 
	 * @param g2proc incoming G2 procedure name
	 * @return Ignition equivalent
	 */
	public String getProcedureName(String g2Proc) {
		String pname = procedureMap.get(g2Proc);
		if( pname==null) {
			pname = UNDEFINED_NAME;
			System.err.println(TAG+".getProcedureName: "+g2Proc+" has no Ignition equivalent");
		}
		return pname;
	}
	
	
}
