package com.ils.sfc.client.step;

import java.awt.Color;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.ils.sfc.common.step.LimitedInputStepDelegate;
import com.inductiveautomation.sfc.client.api.ClientStepFactory;
import com.inductiveautomation.sfc.client.api.StepUI;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

public class LimitedInputStepUI extends AbstractIlsStepUI {
	protected static Icon limitedInputIcon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/limitedInput.png"));
	protected static ImageIcon limitedQuestionIcon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/limitedQuestion.png"));

    public static final ClientStepFactory FACTORY = new LimitedInputStepFactory();

   	@Override
	protected ImageIcon getIcon() { return null; }
	
	@Override
	protected String getHeading() { return "Input Limit"; }
	
	@Override
	protected Color getBorderColor() { return Color.blue; }
	
    public static final class LimitedInputStepFactory extends LimitedInputStepDelegate implements ClientStepFactory {
    	private LimitedInputStepUI UI = new LimitedInputStepUI();

        @Override
        public StepUI createStepUI(ChartUIElement element) {
            return UI;
        }

        @Override
        public Icon getPaletteIcon() {
            return limitedInputIcon; 
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
            return "Get input from a client within specified limits";
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

