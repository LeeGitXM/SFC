package com.ils.sfc.client.step;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.ils.sfc.common.step.ClearQueueStepDelegate;
import com.inductiveautomation.sfc.client.api.ClientStepFactory;
import com.inductiveautomation.sfc.client.api.StepUI;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

public class ClearQueueStepUI extends AbstractIlsStepUI {
	protected static Icon clearQueueIcon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/clearQueue.png"));
  
    public static final ClientStepFactory FACTORY = new ClearQueueStepFactory();

   	@Override
	protected Icon getIcon() { return messageIcon; }
	
	@Override
	protected String getText() { return "<html><b>Clear<br>Queue</html>"; }

    public static final class ClearQueueStepFactory extends ClearQueueStepDelegate implements ClientStepFactory {
    	private ClearQueueStepUI UI = new ClearQueueStepUI();

        @Override
        public StepUI createStepUI(ChartUIElement element) {
            return UI;
        }

        @Override
        public Icon getPaletteIcon() {
            return clearQueueIcon; 
        }

        @Override
        public Icon getRolloverPaletteIcon() {
            return clearQueueIcon; 
        }

        @Override
        public String getPaletteText() {
            return "Clear Queue";
        }

        @Override
        public String getPaletteTooltip() {
            return "Clear the current Message Queue";
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

