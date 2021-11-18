package com.ils.sfc.client.step;

import java.awt.Color;

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
	protected ImageIcon getIcon() { return null; }
	
   	/**
   	 * 
   	 */
	@Override
	protected String getHeading() { return "PV Monitor"; } 
	
	@Override
	protected Color getBorderColor() { return Color.blue; }

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
            return "";
        }

        @Override
        public String getPaletteTooltip() {
            return "Monitor a set of tags for download / performance status";
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

