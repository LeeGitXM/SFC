package com.ils.sfc.client.step;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.ils.sfc.common.step.ControlPanelMessageStepDelegate;
import com.inductiveautomation.sfc.client.api.ClientStepFactory;
import com.inductiveautomation.sfc.client.api.StepUI;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

public class ControlPanelMessageStepUI extends AbstractIlsStepUI {
	protected static Icon controlPanelMessageIcon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/controlPanelMessage.png"));
  
    public static final ClientStepFactory FACTORY = new ControlPanelMessageStepFactory();

   	@Override
	protected Icon getIcon() { return asteriskIcon; }
	
	@Override
	protected String getText() { return "<html><b>Control<br>Panel</html>"; }

    public static final class ControlPanelMessageStepFactory extends ControlPanelMessageStepDelegate implements ClientStepFactory {
    	private ControlPanelMessageStepUI UI = new ControlPanelMessageStepUI();

        @Override
        public StepUI createStepUI(ChartUIElement element) {
            return UI;
        }

        @Override
        public Icon getPaletteIcon() {
            return controlPanelMessageIcon; 
        }

        @Override
        public Icon getRolloverPaletteIcon() {
            return controlPanelMessageIcon; 
        }

        @Override
        public String getPaletteText() {
            return "Post Message to Control Panel";
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
			return PaletteTabs.Notification.toString();
		}

    }

}

