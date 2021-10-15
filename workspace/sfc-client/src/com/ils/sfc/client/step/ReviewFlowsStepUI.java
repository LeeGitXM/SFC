package com.ils.sfc.client.step;

import java.awt.Color;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.ils.sfc.common.step.ReviewFlowsStepDelegate;
import com.inductiveautomation.sfc.client.api.ClientStepFactory;
import com.inductiveautomation.sfc.client.api.StepUI;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

public class ReviewFlowsStepUI extends AbstractIlsStepUI {
	protected static Icon reviewFlowsIcon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/reviewFlows.png"));

    public static final ClientStepFactory FACTORY = new ReviewFlowsStepFactory();

   	@Override
	protected ImageIcon getIcon() { return null; }
	
	@Override
	protected String getHeading() { return "Review Flows"; }
	
	@Override
	protected Color getBorderColor() { return Color.blue; }

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
            return "";
        }

        @Override
        public String getPaletteTooltip() {
            return "Show data flows with advice for review and confirmation";
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

