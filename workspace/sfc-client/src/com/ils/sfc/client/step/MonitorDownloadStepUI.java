package com.ils.sfc.client.step;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.ils.sfc.common.step.MonitorDownloadStepDelegate;
import com.inductiveautomation.sfc.client.api.ClientStepFactory;
import com.inductiveautomation.sfc.client.api.StepUI;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

public class MonitorDownloadStepUI extends AbstractIlsStepUI {
	protected static Icon monitorDownloadIcon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/monitorDownload.png"));
  
    public static final ClientStepFactory FACTORY = new MonitorDownloadStepFactory();

   	@Override
	protected Icon getIcon() { return null; }
	
	@Override
	protected String getHeading() { return "Download GUI"; }

    public static final class MonitorDownloadStepFactory extends MonitorDownloadStepDelegate implements ClientStepFactory {
    	private MonitorDownloadStepUI UI = new MonitorDownloadStepUI();

        @Override
        public StepUI createStepUI(ChartUIElement element) {
            return UI;
        }

        @Override
        public Icon getPaletteIcon() {
            return monitorDownloadIcon; 
        }

        @Override
        public Icon getRolloverPaletteIcon() {
            return getPaletteIcon(); 
        }

        @Override
        public String getPaletteText() {
            return "Download GUI";
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

