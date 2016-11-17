package com.ils.sfc.client.step;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.ils.sfc.common.step.ManualDataEntryStepDelegate;
import com.inductiveautomation.sfc.client.api.ClientStepFactory;
import com.inductiveautomation.sfc.client.api.StepUI;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

public class ManualDataEntryStepUI extends AbstractIlsStepUI {
	protected static Icon manualDataEntryIcon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/enterData.png"));
  
    public static final ClientStepFactory FACTORY = new ManualDataEntryStepFactory();

   	@Override
	protected ImageIcon getIcon() { return null; }
	
	@Override
	protected String getHeading() { return "Enter Data"; }
	
    public static final class ManualDataEntryStepFactory extends ManualDataEntryStepDelegate implements ClientStepFactory {
    	private ManualDataEntryStepUI UI = new ManualDataEntryStepUI();

        @Override
        public StepUI createStepUI(ChartUIElement element) {
            return UI;
        }

        @Override
        public Icon getPaletteIcon() {
            return manualDataEntryIcon; 
        }

        @Override
        public Icon getRolloverPaletteIcon() {
            return getPaletteIcon(); 
        }

        @Override
        public String getPaletteText() {
            return "Data Entry";
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
			return PaletteTabs.Input.toString();
		}

    }

}

