package com.ils.sfc.client;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.ils.sfc.common.AbortStepDelegate;
import com.ils.sfc.util.IlsSfcCommonUtils;
import com.inductiveautomation.sfc.client.api.ClientStepFactory;
import com.inductiveautomation.sfc.client.api.StepUI;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

public class AbortStepUI extends AbstractIlsStepUI {
	protected static Icon abortIcon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/abort.png"));
  
    public static final ClientStepFactory FACTORY = new AbortStepFactory();

   	@Override
	protected Icon getIcon() { return null; }
	
	@Override
	protected String getText() { return "<html><b><font color=red>Abort!</html>"; }

    public static final class AbortStepFactory extends AbortStepDelegate implements ClientStepFactory {
    	private AbortStepUI UI = new AbortStepUI();

        @Override
        public StepUI createStepUI(ChartUIElement element) {
            return UI;
        }

        @Override        public Icon getPaletteIcon() {
            return abortIcon; 
        }

        @Override
        public Icon getRolloverPaletteIcon() {
            return abortIcon; 
        }

        @Override
        public String getPaletteText() {
            return "Abort Recipe";
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
			return PaletteTabs.Control.toString();
		}

    }

}

