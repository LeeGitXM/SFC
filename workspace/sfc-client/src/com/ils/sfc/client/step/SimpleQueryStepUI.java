package com.ils.sfc.client.step;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.ils.sfc.common.step.SimpleQueryStepDelegate;
import com.inductiveautomation.sfc.client.api.ClientStepFactory;
import com.inductiveautomation.sfc.client.api.StepUI;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

public class SimpleQueryStepUI extends AbstractIlsStepUI {
	protected static Icon simpleQueryIcon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/simpleQuery.png"));
  
    public static final ClientStepFactory FACTORY = new SimpleQueryStepFactory();

   	@Override
	protected Icon getIcon() { return null; }
	
	@Override
	protected String getText() { return "<html><b><center>Simple<br>Query</html>"; }

    public static final class SimpleQueryStepFactory extends SimpleQueryStepDelegate implements ClientStepFactory {
    	private SimpleQueryStepUI UI = new SimpleQueryStepUI();

        @Override
        public StepUI createStepUI(ChartUIElement element) {
            return UI;
        }

        @Override
        public Icon getPaletteIcon() {
            return simpleQueryIcon; 
        }

        @Override
        public Icon getRolloverPaletteIcon() {
            return getPaletteIcon(); 
        }

        @Override
        public String getPaletteText() {
            return "Simple Query";
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

