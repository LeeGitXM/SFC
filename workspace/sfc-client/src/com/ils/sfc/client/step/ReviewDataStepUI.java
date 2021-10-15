package com.ils.sfc.client.step;

import java.awt.Color;

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
	protected ImageIcon getIcon() { return null; }
	
	@Override
	protected String getHeading() { return "Review Data"; }

	@Override
	protected Color getBorderColor() { return Color.blue; }
	
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
            return "";
        }

        @Override
        public String getPaletteTooltip() {
            return "Show data for review and confirmation";
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

