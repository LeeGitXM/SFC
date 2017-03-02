package com.ils.sfc.client.step;

import java.awt.Color;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.ils.sfc.common.step.YesNoStepDelegate;
import com.inductiveautomation.sfc.client.api.ClientStepFactory;
import com.inductiveautomation.sfc.client.api.StepUI;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

public class YesNoStepUI extends AbstractIlsStepUI {
	// The yesNo icon contains the border for the palette
	protected static Icon yesNoIcon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/yesNo.png"));

    public static final ClientStepFactory FACTORY = new YesNoStepFactory();

   	@Override
	protected ImageIcon getIcon() { return null; }
	
	@Override
	protected String getHeading() { return "Yes No"; }
	
    @Override
    protected Color getHeadingColor() {return Color.red;}

    public static final class YesNoStepFactory extends YesNoStepDelegate implements ClientStepFactory {
    	private YesNoStepUI UI = new YesNoStepUI();

        @Override
        public StepUI createStepUI(ChartUIElement element) {
            return UI;
        }

        @Override
        public Icon getPaletteIcon() {
            return yesNoIcon; 
        }

        @Override
        public Icon getRolloverPaletteIcon() {
            return getPaletteIcon(); 
        }

        @Override
        public String getPaletteText() {
            return "Yes/No";
        }

        @Override
        public String getPaletteTooltip() {
            return "Obtain a yes or no response from the user";
        }

        @Override
        public void initializeStep(ChartUIElement element) {
        	element.merge(getPropertySet());
        }		
	
		@Override
		public String getCategory() {
			return PaletteTabs.Input.toString();
		}
    }

}

