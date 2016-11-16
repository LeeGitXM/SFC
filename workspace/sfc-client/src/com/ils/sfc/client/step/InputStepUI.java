package com.ils.sfc.client.step;

import java.awt.Color;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.ils.sfc.common.step.InputStepDelegate;
import com.inductiveautomation.sfc.client.api.ClientStepFactory;
import com.inductiveautomation.sfc.client.api.StepUI;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

public class InputStepUI extends AbstractIlsStepUI {
	protected static Icon inputIcon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/input.png"));

    public static final ClientStepFactory FACTORY = new InputStepFactory();

   	@Override
	protected Icon getIcon() { return questionIcon; }
	
	@Override
	protected String getHeading() { return null; }

    public static final class InputStepFactory extends InputStepDelegate implements ClientStepFactory {
    	private InputStepUI UI = new InputStepUI();

        @Override
        public StepUI createStepUI(ChartUIElement element) {
            return UI;
        }

        @Override
        public Icon getPaletteIcon() {
            return inputIcon; 
        }

        @Override
        public Icon getRolloverPaletteIcon() {
            return getPaletteIcon(); 
        }

        @Override
        public String getPaletteText() {
            return "Get Input";
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
			return PaletteTabs.Input.toString();
		}

    }

}

