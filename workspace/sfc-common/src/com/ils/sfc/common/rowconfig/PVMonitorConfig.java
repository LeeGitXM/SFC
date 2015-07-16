package com.ils.sfc.common.rowconfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PVMonitorConfig extends RowConfig {
	private List<Row> rows = new ArrayList<Row>();
	public static class Row {
		public boolean enabled = true;
		public String pvKey;
		public String targetType;
		public Object targetNameIdOrValue;
		public String strategy;
		public String limits;
		public String download;
		public double persistence;
		public double consistency;
		public double deadTime;
		public double tolerance;
		public String toleranceType;
		public String status;

		// transient values used in monitoring (maybe this should be a separate object)
		@JsonIgnore
		public double targetValue;  
		@JsonIgnore
		public double lowLimit;  
		@JsonIgnore
		public double highLimit;  
		@JsonIgnore
		public double inToleranceTime;  //  time at which the value last entered tolerance
		@JsonIgnore
		public double outToleranceTime;  //  time at which the value last exited tolerance
		@JsonIgnore
		public boolean inTolerance;	// was the last pv in tolerance?
		@JsonIgnore
		public double downloadTime;  //  time at which the setpoint was downloaded
		@JsonIgnore
		public boolean isDownloaded;
		@JsonIgnore
		public boolean persistenceOK;
		@JsonIgnore
		public Object ioRD;   // recipe data for the IO object
		@JsonIgnore
		public Object io;   // the controller
		@JsonIgnore
		public Object isOutput;   // is the io recipe data an Output object?
	}

	public List<Row> getRows() {
		return rows;
	}
	
	public static PVMonitorConfig fromJSON(String json) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(json, PVMonitorConfig.class);
	}

	@Override
	public void addRow() {
		rows.add(new Row());
	}

	@Override
	public void removeRow(int index) {
		rows.remove(index);
	}
	
	@JsonIgnore
	@Override
	public int getRowCount() {
		return rows.size();
	}


}
