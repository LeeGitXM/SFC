package com.ils.sfc.client.step;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.ils.sfc.common.step.CollectDataStepDelegate;
import com.ils.sfc.util.IlsSfcCommonUtils;
import com.inductiveautomation.sfc.client.api.ClientStepFactory;
import com.inductiveautomation.sfc.client.api.StepUI;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

public class CollectDataStepUI extends AbstractIlsStepUI {
	protected static Icon collectDataIcon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/collectData.png"));
  
    public static final ClientStepFactory FACTORY = new CollectDataStepFactory();

   	@Override
	protected Icon getIcon() { return null; }
	
	@Override
	protected String getText() { return "<html><center><b>Collect<br>Data</html>"; }

    public static final class CollectDataStepFactory extends CollectDataStepDelegate implements ClientStepFactory {
    	private CollectDataStepUI UI = new CollectDataStepUI();

        @Override
        public StepUI createStepUI(ChartUIElement element) {
            return UI;
        }

        @Override
        public Icon getPaletteIcon() {
            return collectDataIcon; 
        }

        @Override
        public Icon getRolloverPaletteIcon() {
            return getPaletteIcon(); 
        }

        @Override
        public String getPaletteText() {
            return "Collect Data";
        }

        @Override
        public String getPaletteTooltip() {
            return "Read data from a tag";
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

