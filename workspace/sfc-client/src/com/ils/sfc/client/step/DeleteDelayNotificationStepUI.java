package com.ils.sfc.client.step;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.ils.sfc.common.step.DeleteDelayNotificationStepDelegate;
import com.inductiveautomation.sfc.client.api.ClientStepFactory;
import com.inductiveautomation.sfc.client.api.StepUI;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

public class DeleteDelayNotificationStepUI extends AbstractIlsStepUI {
	protected static Icon deleteDelayNotificationIcon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/workCompleted.png"));
  
    public static final ClientStepFactory FACTORY = new DeleteDelayNotificationStepFactory();

   	@Override
	protected Icon getIcon() { return null; }
	
	@Override
	protected String getText() { return "<html><b><center>Work<br>Complete</html>"; }

    public static final class DeleteDelayNotificationStepFactory extends DeleteDelayNotificationStepDelegate implements ClientStepFactory {
    	private DeleteDelayNotificationStepUI UI = new DeleteDelayNotificationStepUI();
 
        @Override
        public StepUI createStepUI(ChartUIElement element) {
            return UI;
        }

        @Override
        public Icon getPaletteIcon() {
            return deleteDelayNotificationIcon;
        }

        @Override
        public Icon getRolloverPaletteIcon() {
            return getPaletteIcon(); 
        }

        @Override
        public String getPaletteText() {
            return "Delete Delay Notification Dialog";
        }

        @Override
        public String getPaletteTooltip() {
            return "Remove all delay notification dialogs";
        }

        @Override
        public void initializeStep(ChartUIElement element) {
        	element.merge(getPropertySet());
        }		
	
		@Override
		public String getCategory() {
			return PaletteTabs.Notification.toString();
		}

    }

}

