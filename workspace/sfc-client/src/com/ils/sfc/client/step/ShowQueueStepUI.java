package com.ils.sfc.client.step;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.ils.sfc.common.step.ShowQueueStepDelegate;
import com.inductiveautomation.sfc.client.api.ClientStepFactory;
import com.inductiveautomation.sfc.client.api.StepUI;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

public class ShowQueueStepUI extends AbstractIlsStepUI {
	protected static Icon showQueueIcon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/showQueue.png"));
  
    public static final ClientStepFactory FACTORY = new ShowQueueStepFactory();

   	@Override
	protected ImageIcon getIcon() { return null; }
	
	@Override
	protected String getHeading() { return "Show Queue"; }

    public static final class ShowQueueStepFactory extends ShowQueueStepDelegate implements ClientStepFactory {
    	private ShowQueueStepUI UI = new ShowQueueStepUI();
 
        @Override
        public StepUI createStepUI(ChartUIElement element) {
            return UI;
        }

        @Override
        public Icon getPaletteIcon() {
            return showQueueIcon;
        }

        @Override
        public Icon getRolloverPaletteIcon() {
            return showQueueIcon; 
        }

        @Override
        public String getPaletteText() {
            return "Show Queue";
        }

        @Override
        public String getPaletteTooltip() {
            return "Show the current Message Queue";
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

