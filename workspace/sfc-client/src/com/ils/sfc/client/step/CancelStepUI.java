package com.ils.sfc.client.step;

import java.awt.Color;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.ils.sfc.common.step.CancelStepDelegate;
import com.inductiveautomation.sfc.client.api.ClientStepFactory;
import com.inductiveautomation.sfc.client.api.StepUI;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

public class CancelStepUI extends AbstractIlsStepUI {
	protected static Icon cancelIcon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/cancel.png"));
  
    public static final ClientStepFactory FACTORY = new AbortStepFactory();

   	@Override
	protected ImageIcon getIcon() { return null; }
	
	@Override
	protected String getHeading() { return "Cancel!"; }
    
    @Override
    protected Color getHeadingColor() {return Color.red;  }

    public static final class AbortStepFactory extends CancelStepDelegate implements ClientStepFactory {
    	private CancelStepUI UI = new CancelStepUI();

        @Override
        public StepUI createStepUI(ChartUIElement element) {
            return UI;
        }

        @Override
        public Icon getPaletteIcon() {
            return cancelIcon; 
        }

        @Override
        public Icon getRolloverPaletteIcon() {
            return cancelIcon; 
        }

        @Override
        public String getPaletteText() {
            return "";
        }

        @Override
        public String getPaletteTooltip() {
            return "Cancel the unit procedure from the bottom up";
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

