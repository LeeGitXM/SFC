package com.ils.sfc.client.step;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.ils.sfc.common.step.PVMonitorStepDelegate;
import com.inductiveautomation.sfc.client.api.ClientStepFactory;
import com.inductiveautomation.sfc.client.api.StepUI;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

public class PVMonitorStepUI extends AbstractIlsStepUI {
	protected static Icon pvMonitorIcon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/pvMonitor.png"));
  
    public static final ClientStepFactory FACTORY = new PVMonitorStepFactory();

   	@Override
	protected Icon getIcon() { return null; }
	
	@Override
	protected String getText() { return "<html><center><b>PV<br>Monitor</html>"; }

    public static final class PVMonitorStepFactory extends PVMonitorStepDelegate implements ClientStepFactory {
    	private PVMonitorStepUI UI = new PVMonitorStepUI();

        @Override
        public StepUI createStepUI(ChartUIElement element) {
            return UI;
        }

        @Override
        public Icon getPaletteIcon() {
            return pvMonitorIcon; 
        }

        @Override
        public Icon getRolloverPaletteIcon() {
            return getPaletteIcon(); 
        }

        @Override
        public String getPaletteText() {
            return "PV Monitor";
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

