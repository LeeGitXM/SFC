package com.ils.sfc.client.step;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.ils.sfc.common.step.PrintWindowStepDelegate;
import com.inductiveautomation.sfc.client.api.ClientStepFactory;
import com.inductiveautomation.sfc.client.api.StepUI;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

public class PrintWindowStepUI extends AbstractIlsStepUI {
	protected static Icon printWindowIcon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/printWindow.png"));
  
    public static final ClientStepFactory FACTORY = new PrintWindowStepFactory();

   	@Override
	protected ImageIcon getIcon() { return null; }
	
	@Override
	protected String getHeading() { return "Print Window"; }

    public static final class PrintWindowStepFactory extends PrintWindowStepDelegate implements ClientStepFactory {
    	private PrintWindowStepUI UI = new PrintWindowStepUI();

        @Override
        public StepUI createStepUI(ChartUIElement element) {
            return UI;
        }

        @Override
        public Icon getPaletteIcon() {
            return printWindowIcon; 
        }

        @Override
        public Icon getRolloverPaletteIcon() {
            return getPaletteIcon(); 
        }

        @Override
        public String getPaletteText() {
            return "";
        }

        @Override
        public String getPaletteTooltip() {
            return "Print a window";
        }

        @Override
        public void initializeStep(ChartUIElement element) {
        	element.merge(getPropertySet());
        }		
	
		@Override
		public String getCategory() {
			return PaletteTabs.Window.toString();
		}

    }

}

