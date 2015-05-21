package com.ils.sfc.client.step;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.ils.sfc.common.step.QueueMessageStepDelegate;
import com.inductiveautomation.sfc.client.api.ClientStepFactory;
import com.inductiveautomation.sfc.client.api.StepUI;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

public class QueueMessageStepUI extends AbstractIlsStepUI {
	protected static Icon queueIcon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/queue.png"));
  
    public static final ClientStepFactory FACTORY = new QueueMessageStepFactory();
    
   	@Override
	protected Icon getIcon() { return messageIcon; }
	
	@Override
	protected String getText() { return "<html><b>Queue<br>Msg</html>"; }
	
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
            return "Post  Message to Queue";
        }

        @Override
        public String getPaletteTooltip() {
            return getPaletteText();
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

