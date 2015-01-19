package com.ils.sfc.client.step;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.ils.sfc.common.step.ReviewDataStepDelegate;
import com.inductiveautomation.sfc.client.api.ClientStepFactory;
import com.inductiveautomation.sfc.client.api.StepUI;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

public class ReviewDataStepUI extends AbstractIlsStepUI {
	protected static Icon reviewDataIcon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/reviewData.png"));

    public static final ClientStepFactory FACTORY = new ReviewDataStepFactory();

   	@Override
	protected Icon getIcon() { return null; }
	
	@Override
	protected String getText() { return "<html><b>Review<br>Data</html>"; }

    public static final class ReviewDataStepFactory extends ReviewDataStepDelegate implements ClientStepFactory {
    	private ReviewDataStepUI UI = new ReviewDataStepUI();
 
        @Override
        public StepUI createStepUI(ChartUIElement element) {
            return UI;
        }

        @Override
        public Icon getPaletteIcon() {
            return reviewDataIcon;
        }

        @Override
        public Icon getRolloverPaletteIcon() {
            return reviewDataIcon; 
        }

        @Override
        public String getPaletteText() {
            return "Review Data";
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

