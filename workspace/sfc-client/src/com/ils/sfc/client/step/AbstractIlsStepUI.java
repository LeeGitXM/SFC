package com.ils.sfc.client.step;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.ils.sfc.common.IlsProperty;
import com.ils.sfc.common.step.AllSteps;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.sfc.ChartStateEnum;
import com.inductiveautomation.sfc.ElementStateEnum;
import com.inductiveautomation.sfc.client.api.ChartStatusContext;
import com.inductiveautomation.sfc.client.api.ClientStepFactory;
import com.inductiveautomation.sfc.client.ui.AbstractStepUI;
import com.inductiveautomation.sfc.elements.steps.ExpressionParamCollection;
import com.inductiveautomation.sfc.elements.steps.enclosing.EnclosingStepProperties;
import com.inductiveautomation.sfc.elements.steps.enclosing.ExecutionMode;
import com.inductiveautomation.sfc.elements.steps.enclosing.ReturnParamCollection;
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
	protected static final Color FINISHED_RUNNING_COLOR = new Color(237, 237, 237);
	private static final LoggerEx log = LogUtil.getLogger(AbstractIlsStepUI.class.getPackage().getName());
	private boolean stepActivated = false;
	private boolean finished = false;
	
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
	protected Color getHeadingColor() { return Color.black; }
	

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
		float outlineWidth = 4.0f;
		Stroke stroke = new BasicStroke(outlineWidth,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND);
		g2d.setStroke(stroke);
		g2d.setPaint(BORDER_DARK_COLOR);
		g2d.draw(rect);

    	// Draw the heading in the upper 1/3 of the box.
		String heading = getHeading();
		double width = cellWidth - 2*INSET;
		double height= cellHeight-2*INSET;
		if( heading!=null && !heading.isEmpty() && height>0 &&width>0 ) {
			Font font = getHeaderFont(g2d,width,height/3,heading);
			double textWidth = font.getStringBounds(heading, g2d.getFontRenderContext()).getWidth();
			paintTextAt(g2d,heading,(float)((width-textWidth)/2),2*INSET+(int)(height/12),getHeadingColor(),font);
			
			// Add the step name in the bottom 2/3. Allow 2 lines.
			String stepName = chartElement.get(IlsProperty.NAME);
			if( stepName!=null && !stepName.isEmpty() ) {
				Rectangle2D bounds = font.getStringBounds(stepName, g2d.getFontRenderContext());
				int pos = getTextBreak(stepName);
				if( pos>0 && bounds.getWidth() > width ) {
					// Double line
					String line = stepName.substring(0, pos);
					font = getNameFont(g2d,width,height/4,line,font);
					textWidth = font.getStringBounds(line, g2d.getFontRenderContext()).getWidth();
					paintTextAt(g2d,line,(float)((width-textWidth)/2),INSET+(int)(height/2),Color.black,font);
					// Line 2
					line = stepName.substring(pos+1);
					textWidth = font.getStringBounds(line, g2d.getFontRenderContext()).getWidth();
					paintTextAt(g2d,line,(float)((width-textWidth)/2),INSET+(int)(3*height/4),Color.black,font);
				}
				else {
					// Single line
					if( bounds.getWidth()>width) {
						font = getNameFont(g2d,width,height/4,stepName,font);
						bounds = font.getStringBounds(stepName, g2d.getFontRenderContext());
					}
					else {
						font = font.deriveFont(Font.PLAIN);
					}
					textWidth = bounds.getWidth();
					paintTextAt(g2d,stepName,(float)((width-textWidth)/2),INSET+(int)(7*height/12),Color.black,font);
				}
			}
		}
    	
    
    	if( getIcon()!=null) {
    		boolean center = (heading==null||heading.isEmpty());
    		drawIcon(g2d,cellWidth,cellHeight,getIcon(),center);
    	}
    	g2d.setColor(originalBackground);
    	g2d.setTransform(oldTransform);
    }



	/** Initialize the Encapsulation step properties for our step types (e.g. ProcedureStep)
     *  that are really Encapsulation steps
     */
	protected static void initializeFoundationStepUI(ChartUIElement element, String s88Level) {
		element.set(EnclosingStepProperties.EXECUTION_MODE, ExecutionMode.RunUntilCompletion);
		element.set(EnclosingStepProperties.PASSED_PARAMS, new ExpressionParamCollection());
		element.set(EnclosingStepProperties.RETURN_PARAMS, new ReturnParamCollection());
		log.infof("Skipping setting the associated data (PETE)");
//        JSONObject associatedData = element.get(ChartStepProperties.AssociatedData);
//        if(associatedData == null) {
//        	associatedData = new JSONObject();
//    		element.set(ChartStepProperties.AssociatedData, associatedData);
//        }
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

		

	/**
	 * Utility method to paint a text string.
	 * @param g
	 * @param text
	 * @param xpos left of the text
	 * @param ypos top of the text
	 * @param fill color of the text
	 */
	private void paintTextAt(Graphics2D g, String text, float xpos, float ypos, Color fill,Font font) {
		if( xpos<0 ) xpos = 0;
		if( ypos<0 ) ypos = 0;
		FontRenderContext frc = g.getFontRenderContext();
		GlyphVector vector = font.createGlyphVector(frc, text);
		Shape textShape = vector.getOutline(xpos, ypos);
		g.setColor(fill);
		g.fill(textShape);
	}

	private Color getBackgroundColor(ChartUIElement chartElement,ChartStatusContext chartStatusContext) {
    	boolean chartNotStarted = !chartStatusContext.getChartStatus().isPresent();
    	StepElementStatus stepElementStatus = chartStatusContext.getStepStatus(chartElement);
    	ElementStateEnum stepState = stepElementStatus.getElementState();
    	Color background  = Color.WHITE;    // Color for block that has not run

    	String factoryId = (String)chartElement.get(IlsProperty.FACTORY_ID);
		if(!finished && AllSteps.longRunningFactoryIds.contains(factoryId)) {
			background = LONG_RUNNING_COLOR;
		}

    	if(chartNotStarted) {
    		if(AllSteps.longRunningFactoryIds.contains(factoryId)) {
    			background = LONG_RUNNING_COLOR;
    		}
    		else {
    			background = Color.WHITE;
    		}
    	}
    	else {
    		if(stepState.isRunning()) {
    			if( chartStatusContext.getChartStatus().get().getChartState().equals(ChartStateEnum.Suspended) ) {
    				background = Color.WHITE;
    			}
    			else {
    				background = Color.green.brighter();
    			}
    		}
			else {
				if(stepActivated && ElementStateEnum.Inactive == stepState) {
					finished = true;
					background = FINISHED_RUNNING_COLOR;
				}
			}
			if(ElementStateEnum.Activating == stepState) {   // This is smells bad, but I couldn't find another way to tell when it's done - CJL
				stepActivated = true;
			}
    	}
		return background;
	}
	// Compute the maximum font possible for the available space
	private Font getHeaderFont(Graphics2D g,double width,double height,String text) {
		FontRenderContext frc = g.getFontRenderContext();
		Font font = g.getFont();
		Rectangle2D bounds = g.getFont().getStringBounds(text, frc);
		float scaleFactor = (float)(width/bounds.getWidth());
		float scaley = (float)(height/bounds.getHeight());
		if(scaley<scaleFactor) scaleFactor = scaley;
		font = font.deriveFont(Font.BOLD,font.getSize2D()*scaleFactor);
		return font;
	}
	// Compute the maximum font possible for the available space for a line of the
	// name. However no bigger than the header font
	private Font getNameFont(Graphics2D g,double width,double height,String text,Font headerFont) {
		FontRenderContext frc = g.getFontRenderContext();
		Font font = g.getFont();
		Rectangle2D bounds = g.getFont().getStringBounds(text, frc);
		float scaleFactor = (float)(width/bounds.getWidth());
		float scaley = (float)(height/bounds.getHeight());
		if(scaley<scaleFactor) scaleFactor = scaley;
		float size = font.getSize2D()*scaleFactor;
		if( size > headerFont.getSize2D() ) size = headerFont.getSize2D();
		font = font.deriveFont(Font.PLAIN,size);
		return font;
	}
	// Find the first space past 1/2 way
	private int getTextBreak(String line) {
		int midpoint = line.length()/2;
		int pos = 0;
		while( pos<midpoint ) {
			pos = line.indexOf(" ", pos+1);
			if( pos<0 ) break;
		}
		return pos;
	}
}
