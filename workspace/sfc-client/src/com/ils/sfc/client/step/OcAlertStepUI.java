package com.ils.sfc.client.step;

import java.awt.Color;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.ils.sfc.common.step.OcAlertStepDelegate;
import com.inductiveautomation.sfc.client.api.ClientStepFactory;
import com.inductiveautomation.sfc.client.api.StepUI;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

public class OcAlertStepUI extends AbstractIlsStepUI {
	protected static Icon ocAlertIcon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/ocAlert.png"));
	  
    public static final ClientStepFactory FACTORY = new OcAlertStepFactory();

   	@Override
	protected ImageIcon getIcon() { return null; }
	
	@Override
	protected String getHeading() { return "OC Alert"; }
	
	@Override
	protected Color getBorderColor() { return Color.blue; }
	
    public static final class OcAlertStepFactory extends OcAlertStepDelegate implements ClientStepFactory {
    	private OcAlertStepUI UI = new OcAlertStepUI();

        @Override
        public StepUI createStepUI(ChartUIElement element) {
            return UI;
        }

        @Override
        public Icon getPaletteIcon() {
            return ocAlertIcon; 
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
            return "Post an OC alert notification to appropriate clients";
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
