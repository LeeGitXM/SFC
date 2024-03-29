package com.ils.sfc.client.step;

import java.awt.Color;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.ils.sfc.common.step.CollectDataStepDelegate;
import com.inductiveautomation.sfc.client.api.ClientStepFactory;
import com.inductiveautomation.sfc.client.api.StepUI;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

public class CollectDataStepUI extends AbstractIlsStepUI {
	protected static Icon collectDataIcon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/collectData.png"));
  
    public static final ClientStepFactory FACTORY = new CollectDataStepFactory();

   	@Override
	protected ImageIcon getIcon() { return null; }
	
	@Override
	protected String getHeading() { return "Collect Data"; }
	
	@Override
	protected Color getBorderColor() { return Color.blue; }

    public static final class CollectDataStepFactory extends CollectDataStepDelegate implements ClientStepFactory {
    	private CollectDataStepUI UI = new CollectDataStepUI();

        @Override
        public StepUI createStepUI(ChartUIElement element) {
            return UI;
        }

        @Override
        public Icon getPaletteIcon() {
            return collectDataIcon; 
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
            return "Read the value of tag(s)";
        }

        @Override
        public void initializeStep(ChartUIElement element) {
        	element.merge(getPropertySet());
        }		
	
		@Override
		public String getCategory() {
			return PaletteTabs.IO.toString();
		}

    }

}

