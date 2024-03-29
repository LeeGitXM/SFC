package com.ils.sfc.client.step;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.ils.sfc.common.step.PostDelayNotificationStepDelegate;
import com.inductiveautomation.sfc.client.api.ClientStepFactory;
import com.inductiveautomation.sfc.client.api.StepUI;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

public class PostDelayNotificationStepUI extends AbstractIlsStepUI {
	protected static Icon postDelayNotificationIcon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/working.png"));
  
    public static final ClientStepFactory FACTORY = new PostDelayNotificationStepFactory();

   	@Override
	protected ImageIcon getIcon() { return null; }
	
	@Override
	protected String getHeading() { return "Working..."; }

    public static final class PostDelayNotificationStepFactory extends PostDelayNotificationStepDelegate implements ClientStepFactory {
    	private PostDelayNotificationStepUI UI = new PostDelayNotificationStepUI();
 
        @Override
        public StepUI createStepUI(ChartUIElement element) {
            return UI;
        }

        @Override
        public Icon getPaletteIcon() {
            return postDelayNotificationIcon;
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
            return "Post up a delay notification window";
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

