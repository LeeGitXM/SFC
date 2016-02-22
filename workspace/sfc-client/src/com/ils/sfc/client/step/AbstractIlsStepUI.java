package com.ils.sfc.client.step;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import org.json.JSONException;
import org.json.JSONObject;

import system.ils.sfc.common.Constants;

import com.google.common.base.Optional;
import com.ils.sfc.common.IlsProperty;
import com.ils.sfc.common.step.AllSteps;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.sfc.ChartStateEnum;
import com.inductiveautomation.sfc.ElementStateEnum;
import com.inductiveautomation.sfc.client.api.ChartStatusContext;
import com.inductiveautomation.sfc.client.ui.AbstractStepUI;
import com.inductiveautomation.sfc.client.api.ClientStepFactory;
import com.inductiveautomation.sfc.elements.steps.ChartStepProperties;
import com.inductiveautomation.sfc.elements.steps.ExpressionParamCollection;
import com.inductiveautomation.sfc.elements.steps.enclosing.EnclosingStepProperties;
import com.inductiveautomation.sfc.elements.steps.enclosing.ExecutionMode;
import com.inductiveautomation.sfc.elements.steps.enclosing.ReturnParamCollection;
import com.inductiveautomation.sfc.rpc.ChartStatus;
import com.inductiveautomation.sfc.rpc.ChartStatus.StepElementStatus;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

public abstract class AbstractIlsStepUI extends AbstractStepUI {
	private static LoggerEx logger = LogUtil.getLogger(AbstractIlsStepUI.class.getName());		
	protected static Icon messageIcon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/message.png"));
	protected static Icon questionIcon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/question.png"));
	protected static Icon clockIcon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/clock.png"));
	protected static Icon asteriskIcon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/asterisk.png"));
	private JLabel label = new JLabel();
	protected enum PaletteTabs { Foundation, Messages, Input, Control, Notification, IO, Query, File, Window };
	protected static final Color LONG_RUNNING_COLOR = new Color(255, 255, 153);
	
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
    	label.setText(getText());
    	label.setBorder(new LineBorder(Color.gray, 2));
    	label.setIcon(getIcon());	
    	label.setHorizontalTextPosition(SwingConstants.LEFT);  // text is to left of icon
    	label.setHorizontalAlignment(SwingConstants.CENTER);
    	label.setVerticalAlignment(SwingConstants.CENTER);
    	label.setBackground(Color.white);
    	label.setOpaque(true);
	}
	
	/** Subclasses override to get icon to display. */
	protected Icon getIcon() { return null; }
	
	/** Subclasses override to get text to display. */
	protected String getText() { return null; }
	
	protected boolean isFoundationStep() {
		return this instanceof ProcedureStepUI || this instanceof OperationStepUI ||
			this instanceof PhaseStepUI;
	}
	
    @Override
    public void drawStep(ChartUIElement propertyValues, ChartStatusContext chartStatusContext, 
    	Graphics2D g2d) {
    	// get the size of the cell:
    	double cellWidth = g2d.getClipBounds().getWidth() * g2d.getTransform().getScaleX();
    	double cellHeight = g2d.getClipBounds().getHeight() * g2d.getTransform().getScaleY();
    	
    	// Give the label a slight inset
    	int inset = 4;
    	label.setSize((int)cellWidth - 2 * inset, (int)cellHeight - 2 * inset);
    	    	
    	// draw connections to other steps
    	this.drawUpLink(g2d);
    	this.drawDownLink(g2d);
    	// IA's drawing uses some significant scaling--remove that for our label display,
    	// but retain the translation part:
    	AffineTransform oldTransform = g2d.getTransform();
    	AffineTransform transform = new AffineTransform();
    	double tx = oldTransform.getTranslateX() + inset;
    	double ty = oldTransform.getTranslateY() + inset;
    	transform.translate(tx, ty);

    	// For foundation steps, add the step name
    	String oldLabelText = null;
		String stepName = propertyValues.get(IlsProperty.NAME);
    	if(isFoundationStep()) {
    		oldLabelText = label.getText();
    		label.setText(oldLabelText.replace("</html>", "<br>" + stepName + "</html>"));
    	}

    	boolean chartNotStarted = !chartStatusContext.getChartStatus().isPresent();
    	StepElementStatus stepElementStatus = chartStatusContext.getStepStatus(propertyValues);
    	ElementStateEnum stepState = stepElementStatus.getElementState();
    	boolean wasActivated = stepElementStatus.getLastActivation() != null;
    	Color background = null;
    	Color ranColor = Color.lightGray;
    	if(chartNotStarted) {
    		background = getBackgroundColor(propertyValues);
    	}
    	else {
    		ChartStatus chartStatus = chartStatusContext.getChartStatus().get();
			//if(chartStatus.getChartState().isTerminal()) {
			//	background = Color.lightGray;
			//}
    		if(stepState.isRunning()) {
				background = Color.green.brighter();
    		}
			else if(ElementStateEnum.Paused == stepState) {
				background = Color.blue.brighter();
			}
			else if(wasActivated) {
				background = ranColor;
			}
			else {
				// hasn't run yet
				background = Color.white;
			}			
    	}
    	label.setBackground(background);
    	g2d.setTransform(transform);
    	label.paint(g2d);
    	g2d.setTransform(oldTransform);
    	
    	if(isFoundationStep()) {
    		label.setText(oldLabelText);
    	}    	
    }

	private Color getBackgroundColor(ChartUIElement propertyValues) {
		String factoryId = (String)propertyValues.get(IlsProperty.FACTORY_ID);
		if(AllSteps.longRunningFactoryIds.contains(factoryId)) {
			return LONG_RUNNING_COLOR;
		}
		else {
			return Color.WHITE;
		}
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

}
