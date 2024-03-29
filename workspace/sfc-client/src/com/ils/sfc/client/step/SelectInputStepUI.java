package com.ils.sfc.client.step;

import java.awt.Color;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.ils.sfc.common.step.SelectInputStepDelegate;
import com.inductiveautomation.sfc.client.api.ClientStepFactory;
import com.inductiveautomation.sfc.client.api.StepUI;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

public class SelectInputStepUI extends AbstractIlsStepUI {
	protected static Icon selectInputIcon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/selectInput.png"));
  
    public static final ClientStepFactory FACTORY = new SelectInputStepFactory();

   	@Override
	protected ImageIcon getIcon() { return null; }
	
	@Override
	protected String getHeading() { return "Select Input"; }
	
	@Override
	protected Color getBorderColor() { return Color.blue; }
    
    public static final class SelectInputStepFactory extends SelectInputStepDelegate implements ClientStepFactory {
    	private SelectInputStepUI UI = new SelectInputStepUI();

        @Override
        public StepUI createStepUI(ChartUIElement element) {
            return UI;
        }

        @Override
        public Icon getPaletteIcon() {
            return selectInputIcon; 
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
            return "User selection from a list of choices";
        }

        @Override
        public void initializeStep(ChartUIElement element) {
        	element.merge(getPropertySet());
        }		
	
		@Override
		public String getCategory() {
			return PaletteTabs.Input.toString();
		}

    }

}

