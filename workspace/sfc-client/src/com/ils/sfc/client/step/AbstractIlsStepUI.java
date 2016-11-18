package com.ils.sfc.client.step;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.RoundRectangle2D;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.json.JSONObject;

import com.ils.sfc.common.IlsProperty;
import com.ils.sfc.common.step.AllSteps;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.sfc.ElementStateEnum;
import com.inductiveautomation.sfc.client.api.ChartStatusContext;
import com.inductiveautomation.sfc.client.api.ClientStepFactory;
import com.inductiveautomation.sfc.client.ui.AbstractStepUI;
import com.inductiveautomation.sfc.elements.steps.ChartStepProperties;
import com.inductiveautomation.sfc.elements.steps.ExpressionParamCollection;
import com.inductiveautomation.sfc.elements.steps.enclosing.EnclosingStepProperties;
import com.inductiveautomation.sfc.elements.steps.enclosing.ExecutionMode;
import com.inductiveautomation.sfc.elements.steps.enclosing.ReturnParamCollection;
import com.inductiveautomation.sfc.rpc.ChartStatus;
import com.inductiveautomation.sfc.rpc.ChartStatus.StepElementStatus;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

public abstract class AbstractIlsStepUI extends AbstractStepUI {		
	protected static final ImageIcon messageIcon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/message.png"));
	protected static final ImageIcon questionIcon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/question.png"));
	protected static final ImageIcon clockIcon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/clock.png"));
	protected static final ImageIcon asteriskIcon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/asterisk.png"));
	protected final static int BORDER_WIDTH = 3;
	protected final static Color BORDER_DARK_COLOR = Color.darkGray;
	protected final static Color BORDER_LIGHT_COLOR = new Color(250,250,250); // Light gray
	
	private static final int INSET = 10;
	protected enum PaletteTabs { Foundation, Messages, Input, Control, Notification, IO, Query, File, Window };
	protected static final Color LONG_RUNNING_COLOR = new Color(255, 255, 153);
	private static final LoggerEx log = LogUtil.getLogger(AbstractIlsStepUI.class.getPackage().getName());		
	
	public static ClientStepFactory[] clientStepFactories = {
		QueueMessageStepUI.FACTORY,
		SaveQueueStepUI.FACTORY,
		SetQueueStepUI.FACTORY,
		ShowQueueStepUI.FACTORY,
		ClearQueueStepUI.FACTORY,
		YesNoStepUI.FACTORY,
		CancelStepUI.FACTORY,
		PauseStepUI.FACTORY,
		ControlPanelMessageStepUI.FACTORY,
		TimedDelayStepUI.FACTORY,
		DeleteDelayNotificationStepUI.FACTORY,
		PostDelayNotificationStepUI.FACTORY,
		EnableDisableStepUI.FACTORY,
		SelectInputStepUI.FACTORY,
		LimitedInputStepUI.FACTORY,
		DialogMessageStepUI.FACTORY,
		CollectDataStepUI.FACTORY,
		InputStepUI.FACTORY,
		RawQueryStepUI.FACTORY,
		SimpleQueryStepUI.FACTORY,
		SaveDataStepUI.FACTORY,
		PrintFileStepUI.FACTORY,
		PrintWindowStepUI.FACTORY,
		CloseWindowStepUI.FACTORY,
		ShowWindowStepUI.FACTORY,
		ReviewDataStepUI.FACTORY,		
		ReviewDataWithAdviceStepUI.FACTORY,		
		ReviewFlowsStepUI.FACTORY,		
		//IlsEnclosingStepUI.FACTORY,
		ProcedureStepUI.FACTORY,
		OperationStepUI.FACTORY,
		PhaseStepUI.FACTORY,
		ConfirmControllersStepUI.FACTORY,
		WriteOutputStepUI.FACTORY,
		PVMonitorStepUI.FACTORY,
		MonitorDownloadStepUI.FACTORY,
		ManualDataEntryStepUI.FACTORY
	};
	
	private static Map<String,ClientStepFactory> factoriesById = new HashMap<String,ClientStepFactory>();
	static {
		for(ClientStepFactory factory: clientStepFactories) {
			factoriesById.put(factory.getId(), factory);
		}
	}
	
	public static ClientStepFactory getFactory(String id) {
		return factoriesById.get(id);
	}
		
	protected AbstractIlsStepUI() {
	}
	
	/** Subclasses override to get icon to display. */
	protected ImageIcon getIcon() { return null; }
	
	/** Subclasses override to get text to display. */
	protected abstract String getHeading();
	
	/** Subclasses override to set the color of the heading text. */
	protected String getHeadingColor() { return "black"; }
	

	/**
	 * This is the method that draws the block plus label and icon. Since there is so little
	 * space, treat the icon as a "badge".
	 * Note: The chartElement size() returns 1x1. 
	 */
    @Override
    public void drawStep(ChartUIElement chartElement, ChartStatusContext chartStatusContext, Graphics2D g2d) {
    	
    	// get the size of the cell:
    	double cellWidth = g2d.getClipBounds().getWidth();
    	double cellHeight = g2d.getClipBounds().getHeight();

		// Turn on anti-aliasing
    	g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
  	
    	// draw connections to other steps
    	this.drawUpLink(g2d);
    	this.drawDownLink(g2d);
    	// IA's drawing uses some significant scaling--remove that for our label display,
    	// but retain the translation part:
    	AffineTransform oldTransform = g2d.getTransform();
    	g2d.translate(INSET,INSET);
    	// Draw the box.
    	Color originalBackground = g2d.getBackground();
		
		RoundRectangle2D.Double rect = new RoundRectangle2D.Double(0,0, cellWidth-2*INSET, cellHeight-2*INSET, 8, 8);
		g2d.setPaint(getBackgroundColor(chartElement,chartStatusContext));
		g2d.fill(rect);
		// Setup for outlining
		float outlineWidth = 5.0f;
		Stroke stroke = new BasicStroke(outlineWidth,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND);
		g2d.setStroke(stroke);
		g2d.setPaint(BORDER_DARK_COLOR);
		g2d.draw(rect);

    	// The label consists of the class and step name.
    	// Here we dynamically add the step.
		String stepName = chartElement.get(IlsProperty.NAME);
    	String heading = getHeading();
    	String text = null;
    	if( heading!=null ) {
    		String hsize = "5";
    		if( heading.length()>10 ) hsize = "6";
    		String textColor = getHeadingColor();
    		String pcnt="95%", lpcnt="100%";
    		if( stepName.length()>10 ) { pcnt ="80%"; lpcnt="90%"; }
    		if(stepName.length()>20 )  { pcnt ="70%"; lpcnt="75%"; }

    		text = "<html><center><h"+hsize+" style=\"color:"+textColor+";padding-top:3px;margin:0px;\">"+heading+"</h"+hsize+"</center>";
    		text = text + "<center><p style=\"font-size:"+pcnt+";line-height:"+lpcnt+";margin:0px;\">" + stepName + "</p></center></html>";
    	}



  
    	
    	if( getIcon()!=null) {
    		boolean center = (heading==null||heading.isEmpty());
    		drawIcon(g2d,cellWidth,cellHeight,getIcon(),center);
    	}
    	g2d.setColor(originalBackground);
    	g2d.setTransform(oldTransform);
    }

	private Color getBackgroundColor(ChartUIElement chartElement,ChartStatusContext chartStatusContext) {
    	boolean chartNotStarted = !chartStatusContext.getChartStatus().isPresent();
    	StepElementStatus stepElementStatus = chartStatusContext.getStepStatus(chartElement);
    	ElementStateEnum stepState = stepElementStatus.getElementState();
    	Color background  = Color.lightGray;    // Color for block that has run
    	if(chartNotStarted) {
    		String factoryId = (String)chartElement.get(IlsProperty.FACTORY_ID);
    		if(AllSteps.longRunningFactoryIds.contains(factoryId)) {
    			background = LONG_RUNNING_COLOR;
    		}
    		else {
    			background = Color.WHITE;
    		}
    	}
    	else {
    		if(stepState.isRunning()) {
				background = Color.green.brighter();
    		}
			else if(ElementStateEnum.Paused == stepState) {
				background = Color.blue.brighter();
			}
    	}
		return background;
	}

	/** Initialize the Encapsulation step properties for our step types (e.g. ProcedureStep)
     *  that are really Encapsulation steps
     */
	protected static void initializeFoundationStepUI(ChartUIElement element, String s88Level) {
		element.set(EnclosingStepProperties.EXECUTION_MODE, ExecutionMode.RunUntilCompletion);
		element.set(EnclosingStepProperties.PASSED_PARAMS, new ExpressionParamCollection());
		element.set(EnclosingStepProperties.RETURN_PARAMS, new ReturnParamCollection());
        JSONObject associatedData = element.get(ChartStepProperties.AssociatedData);
        if(associatedData == null) {
        	associatedData = new JSONObject();
    		element.set(ChartStepProperties.AssociatedData, associatedData);
        }
	}
	
	/**
	 *  Draw the icon as a "badge" in the top right corner of the box. Ignore if
	 *  the box is too small. We assume icon has nearly square dimensions.
	 *  
	 */
	protected void drawIcon(Graphics2D g,double width,double height,ImageIcon icon,boolean center) {
		if( !center && height<20 ) return;   // Too small
		Image img = icon.getImage();
		// If we center the icon, make its height the full height of the box, otherwise 1/3
		
		if(center) {
			int imageHeight = (int)height-2*INSET;
			int x = (int)((width - imageHeight)/2.);
			int y = (int)((height-imageHeight)/2.)-INSET;
			img = img.getScaledInstance(-1, imageHeight, Image.SCALE_SMOOTH);
			Icon icn  = new ImageIcon(img);
			icn.paintIcon(null, g, x, y);
		}
		else {    // put in upper right
			int imageHeight = (int)height/2;
			int x = (int)(width - imageHeight)-INSET;
			int y = -INSET;
			img = img.getScaledInstance(-1, imageHeight, Image.SCALE_FAST);
			Icon icn  = new ImageIcon(img);
			icn.paintIcon(null, g, x, y);
		}
		
	}
		/*
		// Draw the text that is part of the rendered box. Recognize \n or \\n as newlines.
		// Pad with spaces so that we center
		protected void drawEmbeddedText(Graphics2D g,int offsetx,int offsety) {
			String text = block.getEmbeddedLabel();
			if( text == null || text.length()==0 ) return;
			Dimension sz = getPreferredSize();
			String[] lines = text.split("\n");
			if( lines.length==1 ) lines = text.split("\\n");
			int lineCount = lines.length;
			int dy = 3*block.getEmbeddedFontSize()/4;
			int y = sz.height/2 - (lineCount-1)*dy/2;
			for( String line: lines) {
				paintTextAt(g,line,sz.width/2+offsetx,y+offsety,Color.BLACK,block.getEmbeddedFontSize());
				y+=dy;
			}
		}
		
		*/
		/**
		 * Utility method to paint a text string.
		 * @param g
		 * @param text
		 * @param xpos center of the text
		 * @param ypos center of the text
		 * @param fill color of the text
		 */
		/*
		private void paintTextAt(Graphics2D g, String text, float xpos, float ypos, Color fill,int fontSize) {
			Font font = g.getFont();
			font = font.deriveFont((float)fontSize);  // This is, presumably the correct way
			FontRenderContext frc = g.getFontRenderContext();
			GlyphVector vector = font.createGlyphVector(frc, text);
			Rectangle2D bounds = vector.getVisualBounds();
			// xpos, ypos are centers. Adjust to upper left.
			ypos+= bounds.getHeight()/2f;
			xpos-= bounds.getWidth()/2f;

			Shape textShape = vector.getOutline(xpos, ypos);
			g.setColor(fill);
			g.fill(textShape);
		}
	}
	*/
}
