package com.ils.sfc.client.step;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.ils.sfc.common.step.SaveDataStepDelegate;
import com.inductiveautomation.sfc.client.api.ClientStepFactory;
import com.inductiveautomation.sfc.client.api.StepUI;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

public class SaveDataStepUI extends AbstractIlsStepUI {
	protected static Icon saveDataIcon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/saveData.png"));
  
    public static final ClientStepFactory FACTORY = new SaveDataStepFactory();

   	@Override
	protected Icon getIcon() { return null; }
	
	@Override
	protected String getHeading() { return "Save Data"; }

    public static final class SaveDataStepFactory extends SaveDataStepDelegate implements ClientStepFactory {
    	private SaveDataStepUI UI = new SaveDataStepUI();

        @Override
        public StepUI createStepUI(ChartUIElement element) {
            return UI;
        }

        @Override
        public Icon getPaletteIcon() {
            return saveDataIcon; 
        }

        @Override
        public Icon getRolloverPaletteIcon() {
            return getPaletteIcon(); 
        }

        @Override
        public String getPaletteText() {
            return "Save Recipe";
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
			return PaletteTabs.File.toString();
		}

    }

}

