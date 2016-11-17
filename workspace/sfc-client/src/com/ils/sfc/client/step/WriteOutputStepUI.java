package com.ils.sfc.client.step;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.ils.sfc.common.step.WriteOutputStepDelegate;
import com.inductiveautomation.sfc.client.api.ClientStepFactory;
import com.inductiveautomation.sfc.client.api.StepUI;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

public class WriteOutputStepUI extends AbstractIlsStepUI {
	protected static Icon writeOutputIcon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/writeOutput.png"));
  
    public static final ClientStepFactory FACTORY = new WriteOutputStepFactory();

   	@Override
	protected ImageIcon getIcon() { return null; }
	
	@Override
	protected String getHeading() { return "Write Output"; }

    public static final class WriteOutputStepFactory extends WriteOutputStepDelegate implements ClientStepFactory {
    	private WriteOutputStepUI UI = new WriteOutputStepUI();

        @Override
        public StepUI createStepUI(ChartUIElement element) {
            return UI;
        }

        @Override
        public Icon getPaletteIcon() {
            return writeOutputIcon; 
        }

        @Override
        public Icon getRolloverPaletteIcon() {
            return getPaletteIcon(); 
        }

        @Override
        public String getPaletteText() {
            return "Write Outputs";
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
			return PaletteTabs.IO.toString();
		}

    }

}

