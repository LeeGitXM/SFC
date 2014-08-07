package com.ils.common.block;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ils.common.connection.ConnectionType;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;


/**
 * This prototype contains all necessary information to create an
 * anchor point on a block as shown in the designer.
 * 
 * This class is designed to be serializable via JSON.
 */
public class AnchorPrototype {
	private static LoggerEx log = LogUtil.getLogger(AnchorPrototype.class.getPackage().getName());
	private static final String TAG = "AnchorPrototype";
	private AnchorDirection anchorDirection; // Terminus or Origin
	private ConnectionType connectionType;   // Datatype constraint for this anchor
	private String annotation = "";              // Single character used to label the anchor
	private String name;
	
	/**
	 * Deserialize from a Json 
	 * @param json
	 * @return the prototype object created from the string
	 */
	public static AnchorPrototype createAnchorDescription(String json) {
		AnchorPrototype prototype = new AnchorPrototype();
		if( json!=null && json.length()>0 )  {
			ObjectMapper mapper = new ObjectMapper();

			try {
				prototype = mapper.readValue(json, AnchorPrototype.class);
			} 
			catch (JsonParseException jpe) {
				log.warnf("%s: createAnchorDescription parse exception (%s)",TAG,jpe.getLocalizedMessage());
			}
			catch(JsonMappingException jme) {
				log.warnf("%s: createAnchorDescription mapping exception (%s)",TAG,jme.getLocalizedMessage());
			}
			catch(IOException ioe) {
				log.warnf("%s: createAnchorDescription IO exception (%s)",TAG,ioe.getLocalizedMessage());
			}; 
		}
		return prototype;
	}

	
	/**
	 * This is the constructor used during serialization.
	 */
	public AnchorPrototype() {
	}

	/**
	 * This is a convenience constructor that sets almost everything.
	 */
	public AnchorPrototype(String nam,AnchorDirection direction,ConnectionType ct) {
		this.name = nam;
		this.anchorDirection = direction;
		this.connectionType = ct;
	}
	
	public AnchorDirection getAnchorDirection() {return anchorDirection;}
	public ConnectionType getConnectionType() {return connectionType;}
	public String getAnnotation() {return annotation;}
	public String getName() {return name;	}
	public void setAnchorDirection(AnchorDirection anchorDirection) {this.anchorDirection = anchorDirection;}
	public void setConnectionType(ConnectionType connectionType) {this.connectionType = connectionType;}
	public void setAnnotation(String marker) {this.annotation = marker;}
	public void setName(String name) {this.name = name;}
	
	/**
	 * Serialize into a JSON string
	 */
	public String toJson() {
		ObjectMapper mapper = new ObjectMapper();
		String json="";
		log.warnf("%s: toJson ...",TAG);
		try {
			json = mapper.writeValueAsString(this);
		}
		catch(Exception ge) {
			log.warnf("%s: toJson (%s)",TAG,ge.getMessage());
		}
		return json;
	}
}
