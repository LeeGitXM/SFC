package com.ils.sfc.client.step;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.ils.sfc.common.step.RawQueryStepDelegate;
import com.inductiveautomation.sfc.client.api.ClientStepFactory;
import com.inductiveautomation.sfc.client.api.StepUI;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

public class RawQueryStepUI extends AbstractIlsStepUI {
	protected static Icon rawQueryIcon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/rawQuery.png"));
  
    public static final ClientStepFactory FACTORY = new RawQueryStepFactory();

   	@Override
	protected ImageIcon getIcon() { return null; }
	
	@Override
	protected String getHeading() { return "Raw Query"; }

    public static final class RawQueryStepFactory extends RawQueryStepDelegate implements ClientStepFactory {
    	private RawQueryStepUI UI = new RawQueryStepUI();

        @Override
        public StepUI createStepUI(ChartUIElement element) {
            return UI;
        }

        @Override
        public Icon getPaletteIcon() {
            return rawQueryIcon; 
        }

        @Override
        public Icon getRolloverPaletteIcon() {
            return getPaletteIcon(); 
        }

        @Override
        public String getPaletteText() {
            return "Raw Query";
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
			return PaletteTabs.Query.toString();
		}

    }

}

