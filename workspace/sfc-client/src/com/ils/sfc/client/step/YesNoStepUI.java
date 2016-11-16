package com.ils.sfc.client.step;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.ils.sfc.common.step.YesNoStepDelegate;
import com.inductiveautomation.sfc.client.api.ClientStepFactory;
import com.inductiveautomation.sfc.client.api.StepUI;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

public class YesNoStepUI extends AbstractIlsStepUI {
	protected static Icon yesNoIcon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/yesNo.png"));
  
    public static final ClientStepFactory FACTORY = new YesNoStepFactory();

   	@Override
	protected Icon getIcon() { return questionIcon; }
	
	@Override
	protected String getHeading() { return "Yes/No"; }
    @Override
    protected String getHeadingColor() {return "red"; }

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
            return yesNoIcon; 
        }

        @Override
        public String getPaletteText() {
            return "Yes/No";
        }

        @Override
        public String getPaletteTooltip() {
            return "Get Yes/No input from user";
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

