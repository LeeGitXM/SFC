package com.ils.sfc.client.step;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.ils.sfc.common.step.DialogMessageStepDelegate;
import com.inductiveautomation.sfc.client.api.ClientStepFactory;
import com.inductiveautomation.sfc.client.api.StepUI;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

public class DialogMessageStepUI extends AbstractIlsStepUI {
	protected static Icon dialogMessageIcon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/dialogMessage.png"));
  
    public static final ClientStepFactory FACTORY = new DialogMessageStepFactory();

   	@Override
	protected Icon getIcon() { return asteriskIcon; }
	
	@Override
	protected String getText() { return "<html><b>Notify<br>Dialog</html>"; }

    public static final class DialogMessageStepFactory extends DialogMessageStepDelegate implements ClientStepFactory {
    	private DialogMessageStepUI UI = new DialogMessageStepUI();

        @Override
        public StepUI createStepUI(ChartUIElement element) {
            return UI;
        }

        @Override
        public Icon getPaletteIcon() {
            return dialogMessageIcon; 
        }

        @Override
        public Icon getRolloverPaletteIcon() {
            return getPaletteIcon(); 
        }

        @Override
        public String getPaletteText() {
            return "Post Dialog";
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
			return PaletteTabs.Notification.toString();
		}

    }

}

