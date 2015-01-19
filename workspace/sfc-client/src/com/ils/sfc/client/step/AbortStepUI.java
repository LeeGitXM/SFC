package com.ils.sfc.client.step;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.ils.sfc.common.step.AbortStepDelegate;
import com.inductiveautomation.sfc.client.api.ClientStepFactory;
import com.inductiveautomation.sfc.client.api.StepUI;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

public class AbortStepUI extends AbstractIlsStepUI {
	protected static Icon cancelIcon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/abort.png"));
  
    public static final ClientStepFactory FACTORY = new AbortStepFactory();

   	@Override
	protected Icon getIcon() { return null; }
	
	@Override
	protected String getText() { return "<html><b><font color=red>Cancel!</html>"; }

    public static final class AbortStepFactory extends AbortStepDelegate implements ClientStepFactory {
    	private AbortStepUI UI = new AbortStepUI();

        @Override
        public StepUI createStepUI(ChartUIElement element) {
            return UI;
        }

        @Override
        public Icon getPaletteIcon() {
            return cancelIcon; 
        }

        @Override
        public Icon getRolloverPaletteIcon() {
            return cancelIcon; 
        }

        @Override
        public String getPaletteText() {
            return "Cancel Recipe";
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
			return PaletteTabs.Control.toString();
		}

    }

}

