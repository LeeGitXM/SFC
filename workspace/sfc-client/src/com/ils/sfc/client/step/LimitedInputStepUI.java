package com.ils.sfc.client.step;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.ils.sfc.common.step.LimitedInputStepDelegate;
import com.ils.sfc.util.IlsSfcCommonUtils;
import com.inductiveautomation.sfc.client.api.ClientStepFactory;
import com.inductiveautomation.sfc.client.api.StepUI;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

public class LimitedInputStepUI extends AbstractIlsStepUI {
	protected static Icon limitedInputIcon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/limitedInput.png"));
	protected static Icon limitedQuestionIcon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/limitedQuestion.png"));

    public static final ClientStepFactory FACTORY = new LimitedInputStepFactory();

   	@Override
	protected Icon getIcon() { return limitedQuestionIcon; }
	
	@Override
	protected String getText() { return null; }

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
            return "Get Input with Limit Checking";
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

