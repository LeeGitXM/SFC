package com.ils.sfc.client.step;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.ils.sfc.common.step.ShowWindowStepDelegate;
import com.ils.sfc.util.IlsSfcCommonUtils;
import com.inductiveautomation.sfc.client.api.ClientStepFactory;
import com.inductiveautomation.sfc.client.api.StepUI;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

public class ShowWindowStepUI extends AbstractIlsStepUI {
	protected static Icon showWindowIcon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/showWindow.png"));
  
    public static final ClientStepFactory FACTORY = new ShowWindowStepFactory();

   	@Override
	protected Icon getIcon() { return null; }
	
	@Override
	protected String getText() { return "<html><b>Show<br>Window</html>"; }

    public static final class ShowWindowStepFactory extends ShowWindowStepDelegate implements ClientStepFactory {
    	private ShowWindowStepUI UI = new ShowWindowStepUI();
 
        @Override
        public StepUI createStepUI(ChartUIElement element) {
            return UI;
        }

        @Override
        public Icon getPaletteIcon() {
            return showWindowIcon;
        }

        @Override
        public Icon getRolloverPaletteIcon() {
            return showWindowIcon; 
        }

        @Override
        public String getPaletteText() {
            return "Show Window";
        }

        @Override
        public String getPaletteTooltip() {
            return "Show a Window";
        }

        @Override
        public void initializeStep(ChartUIElement element) {
        	element.merge(IlsSfcCommonUtils.createPropertySet(properties));
        }		
	
		@Override
		public String getCategory() {
			return PaletteTabs.Window.toString();
		}

    }

}

