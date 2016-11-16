package com.ils.sfc.client.step;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.ils.sfc.common.step.SaveQueueStepDelegate;
import com.inductiveautomation.sfc.client.api.ClientStepFactory;
import com.inductiveautomation.sfc.client.api.StepUI;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

public class SaveQueueStepUI extends AbstractIlsStepUI {
	protected static Icon saveQueueIcon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/saveQueue.png"));
  
    public static final ClientStepFactory FACTORY = new SaveQueueStepFactory();

   	@Override
	protected Icon getIcon() { return messageIcon; }
	
	@Override
	protected String getHeading() { return "Save Queue"; }

    public static final class SaveQueueStepFactory extends SaveQueueStepDelegate implements ClientStepFactory {
    	private SaveQueueStepUI UI = new SaveQueueStepUI();

        @Override
        public StepUI createStepUI(ChartUIElement element) {
            return UI;
        }

        @Override
        public Icon getPaletteIcon() {
            return saveQueueIcon; 
        }

        @Override
        public Icon getRolloverPaletteIcon() {
            return getPaletteIcon(); 
        }

        @Override
        public String getPaletteText() {
            return "Save Queue";
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

