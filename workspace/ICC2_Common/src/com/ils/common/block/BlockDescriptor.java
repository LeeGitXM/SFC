package com.ils.common.block;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;


/**
 * This view prototype contains all necessary information to create an
 * entry on the Block-and-Connector workspace representing the target block.
 * 
 * This class is designed to be serializable via JSON.
 */
public class BlockDescriptor {
	private static LoggerEx log = LogUtil.getLogger(BlockDescriptor.class.getPackage().getName());
	private static final String TAG = "BlockDescription";
	private List<AnchorPrototype> anchors;
	private int background = Color.white.getRGB();  // Transmit color as an int for serialization.
	private String blockClass = null;               // Class of the block in the Gateway (app. ... implies Python)
	private int    embeddedFontSize = 24;
	private String embeddedIcon="";       // 32x32 icon to place in block in designer
	private String embeddedLabel="";      // Label place in block in designer
	private String iconPath = null;       // Icon to use for an icon-only block
	private int preferredHeight = 0;      // Size block to its "natural" size
	private int preferredWidth  = 0;
	private BlockStyle style = BlockStyle.SQUARE;
	
	
	public BlockDescriptor() {
		anchors = new ArrayList<AnchorPrototype>();
	}
	
	/**
	 * Deserialize from a Json 
	 * @param json
	 * @return the BlockDescriptor created from the string
	 */
	public static BlockDescriptor createPrototype(String json) {
		BlockDescriptor prototype = new BlockDescriptor();
		if( json!=null && json.length()>0 )  {
			ObjectMapper mapper = new ObjectMapper();

			try {
				prototype = mapper.readValue(json, BlockDescriptor.class);
			} 
			catch (JsonParseException jpe) {
				log.warnf("%s: createPrototype parse exception (%s)",TAG,jpe.getLocalizedMessage());
			}
			catch(JsonMappingException jme) {
				log.warnf("%s: createPrototype mapping exception (%s)",TAG,jme.getLocalizedMessage());
			}
			catch(IOException ioe) {
				log.warnf("%s: createPrototype IO exception (%s)",TAG,ioe.getLocalizedMessage());
			}
			 
		}
		return prototype;
	}

	public void addAnchor(AnchorPrototype anchor) { anchors.add(anchor); }
	public List<AnchorPrototype> getAnchors() { return anchors; }
	public int getBackground() {return background;}
	public String getBlockClass() { return blockClass; }
	public int getEmbeddedFontSize() {return embeddedFontSize;}
	public String getEmbeddedIcon() {return embeddedIcon;}
	public String getEmbeddedLabel() {return embeddedLabel;}
	public String getIconPath() {return iconPath;}
	public int getPreferredHeight() {return preferredHeight;}
	public int getPreferredWidth() {return preferredWidth;}
	public BlockStyle getStyle() { return style; }

	
	public void setAnchors(List<AnchorPrototype> anchors) { this.anchors = anchors; }
	public void setBackground(int background) {this.background = background;}
	public void setBlockClass(String blockClass) { this.blockClass = blockClass; }
	public void setEmbeddedFontSize(int embeddedFontSize) {this.embeddedFontSize = embeddedFontSize;}
	public void setEmbeddedIcon(String embeddedIcon) {this.embeddedIcon = embeddedIcon;}
	public void setEmbeddedLabel(String embeddedLabel) {this.embeddedLabel = embeddedLabel;}
	public void setIconPath(String iconPath) {this.iconPath = iconPath;}
	public void setPreferredHeight(int preferredHeight) {this.preferredHeight = preferredHeight;}
	public void setPreferredWidth(int preferredWidth) {this.preferredWidth = preferredWidth;}
	public void setStyle(BlockStyle style) { this.style = style; }

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
