package com.ils.python.lookup;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;

/**
 * Convert a G2 classname into a BLT classname
 */
public class ProcedureMapper {
	private static final String TAG = "ProcedureMapper";
	private final LoggerEx log;
	/** 
	 * Constructor: 
	 */
	public ProcedureMapper() {
		this.log = LogUtil.getLogger(getClass().getPackage().getName());
	}
	
	/**
	 * Perform a database lookup to create a map of G2
	 * block names to Ignition blocks.
	 * @param cxn open database connection
	 */
	public Map<String,String> createMap(Connection cxn) {
		Map<String,String> procedureMap = new HashMap<>();
		ResultSet rs = null;
		try {
			Statement statement = cxn.createStatement();
			statement.setQueryTimeout(30);  // set timeout to 30 sec.
			log.infof("ProcedureMapper: selecting ...");
			rs = statement.executeQuery("select * from ProcedureMap");
			while(rs.next())
			{
				String g2 = rs.getString("G2Procedure");
				String ignition = rs.getString("IgnitionProcedure");
				procedureMap.put(g2, ignition);
				log.infof("ProcedureMapper: add %s = %s",g2,ignition);
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
		return procedureMap;
	}
}
