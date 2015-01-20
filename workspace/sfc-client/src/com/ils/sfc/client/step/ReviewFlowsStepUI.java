package com.ils.sfc.client.step;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.ils.sfc.common.step.ReviewFlowsStepDelegate;
import com.inductiveautomation.sfc.client.api.ClientStepFactory;
import com.inductiveautomation.sfc.client.api.StepUI;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

public class ReviewFlowsStepUI extends AbstractIlsStepUI {
	protected static Icon reviewFlowsIcon = null; // new ImageIcon(AbstractIlsStepUI.class.getResource("/images/ReviewFlows.png"));

    public static final ClientStepFactory FACTORY = new ReviewFlowsStepFactory();

   	@Override
	protected Icon getIcon() { return null; }
	
	@Override
	protected String getText() { return "<html><b>Review<br>Flows</html>"; }

    public static final class ReviewFlowsStepFactory extends ReviewFlowsStepDelegate implements ClientStepFactory {
    	private ReviewFlowsStepUI UI = new ReviewFlowsStepUI();
 
        @Override
        public StepUI createStepUI(ChartUIElement element) {
            return UI;
        }

        @Override
        public Icon getPaletteIcon() {
            return reviewFlowsIcon;
        }

        @Override
        public Icon getRolloverPaletteIcon() {
            return reviewFlowsIcon; 
        }

        @Override
        public String getPaletteText() {
            return "Review Flows";
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

