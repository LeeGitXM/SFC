package com.ils.common.block;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;



/**
 * Hold an attribute of a block. This class is designed to be serializable via
 * any XML or JSON serializer.
 */
public class BlockProperty {
	private static final String TAG = "BlockProperty";
	private static LoggerEx log = LogUtil.getLogger(PalettePrototype.class.getPackage().getName());
	private boolean editable;
	private PropertyType type = PropertyType.STRING;
	private String binding = "";
	private BindingType bindingType = BindingType.NONE;
	private String name;
	private String quality;
	private Object value;
	private List<ChangeListener> changeListeners = new ArrayList<ChangeListener>();

	/** 
	 * Constructor: Sets all attributes.
	 */
	public BlockProperty(String name,Object value,PropertyType type,boolean canEdit) {
		this.name = name;
		this.value = value;
		this.type = type;
		this.editable = canEdit;
	}
	
	public BlockProperty() {
	}
	
	public void addChangeListener(ChangeListener c) {
		changeListeners.add(c);
	}
	
	public void removeChangeListener(ChangeListener c) {
		changeListeners.remove(c);
	}
	
	private void notifyChangeListeners() {
		ChangeEvent event = new ChangeEvent(this);
		for(ChangeListener l: changeListeners) {
			l.stateChanged(event);
		}
	}
	/**
	 * Deserialize from a Json string 
	 * @param json
	 * @return the BlockProperty created from the input JSON string
	 */
	public static BlockProperty createProperty(String json) {
		BlockProperty property = new BlockProperty();
		if( json!=null && json.length()>0 )  {
			ObjectMapper mapper = new ObjectMapper();

			try {
				property = mapper.readValue(json, BlockProperty.class);
			} 
			catch (JsonParseException jpe) {
				log.warnf("%s: createProperty parse exception (%s)",TAG,jpe.getLocalizedMessage());
			}
			catch(JsonMappingException jme) {
				log.warnf("%s: createProperty mapping exception (%s)",TAG,jme.getLocalizedMessage());
			}
			catch(IOException ioe) {
				log.warnf("%s: createProperty IO exception (%s)",TAG,ioe.getLocalizedMessage());
			} 
		}
		return property;
	}
	
	public boolean isEditable() {return editable;}
	public void setEditable(boolean editable) {this.editable = editable;}
	public String getBinding() {return binding;}
	public void setBinding(String lnk) {this.binding = lnk;}
	public String getName() {return name;}
	public void setName(String name) {this.name = name;}
	public String getQuality() {return quality;}
	public void setQuality(String quality) {this.quality = quality;}

	public BindingType getBindingType() { return bindingType; }
	public void setBindingType(BindingType type) { this.bindingType = type; }
	public PropertyType getType() {return type;}
	public void setType(PropertyType type) {this.type = type;}
	public Object getValue() {return value;}
	
	public void setValue(Object value) {
		this.value = value;
		notifyChangeListeners();
	}
	
	/**
	 * Serialize into a JSON string
	 */
	public String toJson() {
		ObjectMapper mapper = new ObjectMapper();
		String json="";
		try {
			json = mapper.writeValueAsString(this);
		}
		catch(Exception ge) {
			log.warnf("%s: toJson (%s)",TAG,ge.getMessage());
		}
		log.tracef("%s: toJson = %s",TAG,json);
		return json;
	}
	
	/**
	 * A readable string representation for debugging.
	 */
	public String toString() {
		return getName()+"="+(value==null?"null":value.toString());
	}
}
