package com.ils.sfc.client.step;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.ils.sfc.common.step.ConfirmControllersStepDelegate;
import com.inductiveautomation.sfc.client.api.ClientStepFactory;
import com.inductiveautomation.sfc.client.api.StepUI;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

public class ConfirmControllersStepUI extends AbstractIlsStepUI {
	protected static Icon confirmControllersIcon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/checkControllers.png"));
  
    public static final ClientStepFactory FACTORY = new ConfirmControllersStepFactory();

   	@Override
	protected Icon getIcon() { return null; }
	
	@Override
	protected String getText() { return "<html><center><b>Check<br>Mode</html>"; }

    public static final class ConfirmControllersStepFactory extends ConfirmControllersStepDelegate implements ClientStepFactory {
    	private ConfirmControllersStepUI UI = new ConfirmControllersStepUI();

        @Override
        public StepUI createStepUI(ChartUIElement element) {
            return UI;
        }

        @Override
        public Icon getPaletteIcon() {
            return confirmControllersIcon; 
        }

        @Override
        public Icon getRolloverPaletteIcon() {
            return getPaletteIcon(); 
        }

        @Override
        public String getPaletteText() {
            return "Check Controller Mode";
        }

        @Override
        public String getPaletteTooltip() {
            return "Confirm Controller Mode";
        }

        @Override
        public void initializeStep(ChartUIElement element) {
        	element.merge(getPropertySet());
        }		
	
		@Override
		public String getCategory() {
			return PaletteTabs.IO.toString();
		}

    }

}

