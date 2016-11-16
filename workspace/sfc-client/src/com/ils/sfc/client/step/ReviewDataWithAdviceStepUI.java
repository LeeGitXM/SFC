package com.ils.sfc.client.step;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.ils.sfc.common.step.ReviewDataWithAdviceStepDelegate;
import com.inductiveautomation.sfc.client.api.ClientStepFactory;
import com.inductiveautomation.sfc.client.api.StepUI;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

public class ReviewDataWithAdviceStepUI extends AbstractIlsStepUI {
	protected static Icon reviewDataWithAdviceIcon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/reviewDataStar.png"));

    public static final ClientStepFactory FACTORY = new ReviewDataWithAdviceStepFactory();

   	@Override
	protected Icon getIcon() { return null; }
	
	@Override
	protected String getHeading() { return "Review Data*"; }

    public static final class ReviewDataWithAdviceStepFactory extends ReviewDataWithAdviceStepDelegate implements ClientStepFactory {
    	private ReviewDataWithAdviceStepUI UI = new ReviewDataWithAdviceStepUI();
 
        @Override
        public StepUI createStepUI(ChartUIElement element) {
            return UI;
        }

        @Override
        public Icon getPaletteIcon() {
            return reviewDataWithAdviceIcon;
        }

        @Override
        public Icon getRolloverPaletteIcon() {
            return reviewDataWithAdviceIcon; 
        }

        @Override
        public String getPaletteText() {
            return "Review w/ Advice";
        }

        @Override
        public String getPaletteTooltip() {
            return "Show user the specified data";
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

