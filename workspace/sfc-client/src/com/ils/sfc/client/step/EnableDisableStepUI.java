package com.ils.sfc.client.step;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.ils.sfc.common.step.EnableDisableStepDelegate;
import com.inductiveautomation.sfc.client.api.ClientStepFactory;
import com.inductiveautomation.sfc.client.api.StepUI;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

public class EnableDisableStepUI extends AbstractIlsStepUI {
	protected static Icon enableDisableIcon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/enableDisable.png"));
  
    public static final ClientStepFactory FACTORY = new EnableDisableStepFactory();

   	@Override
	protected Icon getIcon() { return null; }
	
	@Override
	protected String getText() { return "<html><b><center><b>Enable/<br>Disable</html>"; }

    public static final class EnableDisableStepFactory extends EnableDisableStepDelegate implements ClientStepFactory {
    	private EnableDisableStepUI UI = new EnableDisableStepUI();

        @Override
        public StepUI createStepUI(ChartUIElement element) {
            return UI;
        }

        @Override
        public Icon getPaletteIcon() {
            return enableDisableIcon; 
        }

        @Override
        public Icon getRolloverPaletteIcon() {
            return getPaletteIcon();
        }

        @Override
        public String getPaletteText() {
            return "Enable/Disable Command";
        }

        @Override
        public String getPaletteTooltip() {
            return "Enable/Disable a Control Panel button";
        }

        @Override
        public void initializeStep(ChartUIElement element) {
        	element.merge(getPropertySet());
        }		
	
		@Override
		public String getCategory() {
			return PaletteTabs.Control.toString();
		}

    }

}

