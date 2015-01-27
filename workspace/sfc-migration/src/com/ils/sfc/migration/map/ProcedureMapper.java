package com.ils.sfc.migration.map;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * Map G2 Procedure names to Python module paths.
 */
public class ProcedureMapper {
	private final String TAG = "ProcedureMapper";
	private final Map<String,String> procedureMap;     // Lookup by G2 property name
	private ResultSet rs;
	/** 
	 * Constructor: 
	 */
	public ProcedureMapper() {
		procedureMap = new HashMap<String,String>();
	}

	/**
	 * For all classes, perform a database lookup to map attribute names.
	 * Key by Ignition name.
	 * 
	 * @param cxn open database connection
	 */
	public void createMap(Connection cxn) {
		rs = null;
		try {
			Statement statement = cxn.createStatement();
			statement.setQueryTimeout(30);  // set timeout to 30 sec.

			rs = statement.executeQuery("select * from ProcedureMap");
			while(rs.next())
			{
				String g2Procedure = rs.getString("G2Procedure");
				String pythonModule = rs.getString("IgnitonProcedure");
				
				procedureMap.put(g2Procedure.toLowerCase(),pythonModule);
			}
			rs.close();
		}
		catch(SQLException e) {
			// if the error message is "out of memory", 
			// it probably means no database file is found
			System.err.println(TAG+"createMap: "+e.getMessage());
		}
		finally {
			if( rs!=null) {
				try { rs.close(); } catch(SQLException ignore) {}
			}
		}
	}
	
	
}
	

