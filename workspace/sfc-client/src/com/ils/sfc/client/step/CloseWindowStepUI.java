package com.ils.sfc.client.step;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.ils.sfc.common.step.CloseWindowStepDelegate;
import com.inductiveautomation.sfc.client.api.ClientStepFactory;
import com.inductiveautomation.sfc.client.api.StepUI;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

public class CloseWindowStepUI extends AbstractIlsStepUI {
	protected static Icon closeWindowIcon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/closeWindow.png"));
  
    public static final ClientStepFactory FACTORY = new CloseWindowStepFactory();

   	@Override
	protected Icon getIcon() { return null; }
	
	@Override
	protected String getHeading() { return "Close Window"; }

    public static final class CloseWindowStepFactory extends CloseWindowStepDelegate implements ClientStepFactory {
    	private CloseWindowStepUI UI = new CloseWindowStepUI();

        @Override
        public StepUI createStepUI(ChartUIElement element) {
            return UI;
        }

        @Override
        public Icon getPaletteIcon() {
            return closeWindowIcon; 
        }

        @Override
        public Icon getRolloverPaletteIcon() {
            return closeWindowIcon; 
        }

        @Override
        public String getPaletteText() {
            return "Close Window";
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
			return PaletteTabs.Window.toString();
		}

    }

}

