package com.ils.sfc.client.step;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.ils.sfc.common.step.QueueMessageStepDelegate;
import com.inductiveautomation.sfc.client.api.ClientStepFactory;
import com.inductiveautomation.sfc.client.api.StepUI;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

public class QueueMessageStepUI extends AbstractIlsStepUI {
	protected static Icon queueIcon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/queueMessage.png"));
  
    public static final ClientStepFactory FACTORY = new QueueMessageStepFactory();
    
   	@Override
	protected ImageIcon getIcon() { return null; }
	
	@Override
	protected String getHeading() { return "Queue Msg"; }
	
    public static final class QueueMessageStepFactory extends QueueMessageStepDelegate implements ClientStepFactory {
    	private QueueMessageStepUI UI = new QueueMessageStepUI();
 
        @Override
        public StepUI createStepUI(ChartUIElement element) {
            return UI;
        }

        @Override
        public Icon getPaletteIcon() {
            return queueIcon; //icon;
        }

        @Override
        public Icon getRolloverPaletteIcon() {
            return queueIcon;
        }

        @Override
        public String getPaletteText() {
            return "";
        }

        @Override
        public String getPaletteTooltip() {
            return "Post a message to a queue";
        }

        @Override
        public void initializeStep(ChartUIElement element) {
        	element.merge(getPropertySet());
        }		
	
		@Override
		public String getCategory() {
			return PaletteTabs.Messages.toString();
		}

    }

}

