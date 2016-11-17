package com.ils.sfc.client.step;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.ils.sfc.common.step.PauseStepDelegate;
import com.inductiveautomation.sfc.client.api.ClientStepFactory;
import com.inductiveautomation.sfc.client.api.StepUI;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

public class PauseStepUI extends AbstractIlsStepUI {
	protected static Icon pauseIcon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/pause.png"));
  
    public static final ClientStepFactory FACTORY = new PauseStepFactory();

   	@Override
	protected ImageIcon getIcon() { return null; }
	
	@Override
	protected String getHeading() { return "Pause!"; }
    @Override
    protected String getHeadingColor() {return "red";}

    public static final class PauseStepFactory extends PauseStepDelegate implements ClientStepFactory {
    	private PauseStepUI UI = new PauseStepUI();

        @Override
        public StepUI createStepUI(ChartUIElement element) {
            return UI;
        }

        @Override        
        public Icon getPaletteIcon() {
            return pauseIcon; 
        }

        @Override
        public Icon getRolloverPaletteIcon() {
            return pauseIcon; 
        }

        @Override
        public String getPaletteText() {
            return "Pause Recipe";
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
			return PaletteTabs.Control.toString();
		}

    }

}

