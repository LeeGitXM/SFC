package com.ils.sfc.client.step;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.ils.sfc.common.step.PrintWindowStepDelegate;
import com.ils.sfc.util.IlsSfcCommonUtils;
import com.inductiveautomation.sfc.client.api.ClientStepFactory;
import com.inductiveautomation.sfc.client.api.StepUI;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

public class PrintWindowStepUI extends AbstractIlsStepUI {
	protected static Icon printWindowIcon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/printWindow.png"));
  
    public static final ClientStepFactory FACTORY = new PrintWindowStepFactory();

   	@Override
	protected Icon getIcon() { return null; }
	
	@Override
	protected String getText() { return "<html><center><b>Print<br>Window</html>"; }

    public static final class PrintWindowStepFactory extends PrintWindowStepDelegate implements ClientStepFactory {
    	private PrintWindowStepUI UI = new PrintWindowStepUI();

        @Override
        public StepUI createStepUI(ChartUIElement element) {
            return UI;
        }

        @Override
        public Icon getPaletteIcon() {
            return printWindowIcon; 
        }

        @Override
        public Icon getRolloverPaletteIcon() {
            return getPaletteIcon(); 
        }

        @Override
        public String getPaletteText() {
            return "Print Window";
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
			return PaletteTabs.Window.toString();
		}

    }

}

