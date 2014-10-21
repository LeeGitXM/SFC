package com.ils.sfc.client.step;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.ils.sfc.common.step.SetQueueStepDelegate;
import com.ils.sfc.util.IlsSfcCommonUtils;
import com.inductiveautomation.sfc.client.api.ClientStepFactory;
import com.inductiveautomation.sfc.client.api.StepUI;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

public class SetQueueStepUI extends AbstractIlsStepUI {
	protected static Icon setQueueIcon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/setQueue.png"));
  
    public static final ClientStepFactory FACTORY = new SetQueueStepFactory();

   	@Override
	protected Icon getIcon() { return messageIcon; }
	
	@Override
	protected String getText() { return "<html><b>Set<br>Queue</html>"; }

    public static final class SetQueueStepFactory extends SetQueueStepDelegate implements ClientStepFactory {
    	private SetQueueStepUI UI = new SetQueueStepUI();

        @Override
        public StepUI createStepUI(ChartUIElement element) {
            return UI;
        }

        @Override
        public Icon getPaletteIcon() {
            return setQueueIcon; 
        }

        @Override
        public Icon getRolloverPaletteIcon() {
            return setQueueIcon;
        }

        @Override
        public String getPaletteText() {
            return "Set Queue";
        }

        @Override
        public String getPaletteTooltip() {
            return "Set the current Message Queue";
        }

        @Override
        public void initializeStep(ChartUIElement element) {
        	element.merge(IlsSfcCommonUtils.createPropertySet(properties));
        }		
	
		@Override
		public String getCategory() {
			return PaletteTabs.Messages.toString();
		}

    }

}

