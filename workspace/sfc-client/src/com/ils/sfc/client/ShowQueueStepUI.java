package com.ils.sfc.client;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.ils.sfc.client.AbstractIlsStepUI.PaletteTabs;
import com.ils.sfc.common.ShowQueueStepDelegate;
import com.ils.sfc.util.IlsSfcCommonUtils;
import com.inductiveautomation.sfc.client.api.ClientStepFactory;
import com.inductiveautomation.sfc.client.api.StepUI;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

public class ShowQueueStepUI extends AbstractIlsStepUI {
	protected static Icon showQueueIcon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/showQueue.png"));
  
    public static final ClientStepFactory FACTORY = new ShowQueueStepFactory();

   	@Override
	protected Icon getIcon() { return messageIcon; }
	
	@Override
	protected String getText() { return "<html><b>Show<br>Queue</html>"; }

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
        	element.merge(IlsSfcCommonUtils.createPropertySet(properties));
        }		
	
		@Override
		public String getCategory() {
			return PaletteTabs.Messages.toString();
		}

    }

}

