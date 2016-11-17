package com.ils.sfc.client.step;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.ils.sfc.common.step.TimedDelayStepDelegate;
import com.inductiveautomation.sfc.client.api.ClientStepFactory;
import com.inductiveautomation.sfc.client.api.StepUI;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

public class TimedDelayStepUI extends AbstractIlsStepUI {
	protected static Icon timedDelayIcon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/timedDelay.png"));
  
    public static final ClientStepFactory FACTORY = new TimedDelayStepFactory();

   	@Override
	protected ImageIcon getIcon() { return clockIcon; }
	
	@Override
	protected String getHeading() { return null; }

    public static final class TimedDelayStepFactory extends TimedDelayStepDelegate implements ClientStepFactory {
    	private TimedDelayStepUI UI = new TimedDelayStepUI();
 
        @Override
        public StepUI createStepUI(ChartUIElement element) {
            return UI;
        }

        @Override
        public Icon getPaletteIcon() {
            return timedDelayIcon;
        }

        @Override
        public Icon getRolloverPaletteIcon() {
            return getPaletteIcon(); 
        }

        @Override
        public String getPaletteText() {
            return "Timed Delay";
        }

        @Override
        public String getPaletteTooltip() {
            return "Delay for the specified time";
        }

        @Override
        public void initializeStep(ChartUIElement element) {
        	element.merge(getPropertySet());
        }		
	
		@Override
		public String getCategory() {
			return PaletteTabs.Control.toString();
		}

    }

}

