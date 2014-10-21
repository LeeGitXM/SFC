package com.ils.sfc.client.step;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.ils.sfc.common.step.DebugPropertiesStepDelegate;
import com.ils.sfc.util.IlsSfcCommonUtils;
import com.inductiveautomation.sfc.client.api.ClientStepFactory;
import com.inductiveautomation.sfc.client.api.StepUI;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

public class DebugPropertiesStepUI extends AbstractIlsStepUI {
	//protected static Icon debugPropertiesIcon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/debugProperties.png"));
  
    public static final ClientStepFactory FACTORY = new DebugPropertiesStepFactory();

   	@Override
	protected Icon getIcon() { return null; }
	
	@Override
	protected String getText() { return "<html><b>Debug<br>Properties!</html>"; }

    public static final class DebugPropertiesStepFactory extends DebugPropertiesStepDelegate implements ClientStepFactory {
    	private DebugPropertiesStepUI UI = new DebugPropertiesStepUI();

        @Override
        public StepUI createStepUI(ChartUIElement element) {
            return UI;
        }

        @Override
        public Icon getPaletteIcon() {
            return null; // debugPropertiesIcon; 
        }

        @Override
        public Icon getRolloverPaletteIcon() {
            return getPaletteIcon(); 
        }

        @Override
        public String getPaletteText() {
            return "Debug Properties";
        }

        @Override
        public String getPaletteTooltip() {
            return getPaletteText();
        }

        @Override
        public void initializeStep(ChartUIElement element) {
        	element.merge(IlsSfcCommonUtils.createPropertySet(properties));
        }		
	
		@Override
		public String getCategory() {
			return PaletteTabs.Debug.toString();
		}

    }

}

