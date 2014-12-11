package com.ils.sfc.client.step;

import java.awt.Dimension;
import java.awt.Image;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.ils.sfc.common.step.PhaseStepDelegate;
import com.inductiveautomation.sfc.client.api.ClientStepFactory;
import com.inductiveautomation.sfc.client.api.StepUI;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

public class PhaseStepUI extends AbstractIlsStepUI {
	protected static Icon phaseIcon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/phase.png"));

    public static final ClientStepFactory FACTORY = new PhaseStepFactory();

    public PhaseStepUI() {
	}
		
   	@Override
	protected Icon getIcon() { return null; }
	
	@Override
	protected String getText() { return "<html><b><c>Phase</html>"; }

    public static final class PhaseStepFactory extends PhaseStepDelegate implements ClientStepFactory {
    	private PhaseStepUI UI = new PhaseStepUI();

        @Override
        public StepUI createStepUI(ChartUIElement element) {
            return UI;
        }

        @Override        
        public Icon getPaletteIcon() {
            return phaseIcon; 
        }

        @Override
        public Icon getRolloverPaletteIcon() {
            return getPaletteIcon(); 
        }

        @Override
        public String getPaletteText() {
            return "Phase";
        }

        @Override
        public String getPaletteTooltip() {
            return getPaletteText();
        }

        @Override
        public void initializeStep(ChartUIElement element) {
        	
        }		
	
		@Override
		public String getCategory() {
			return PaletteTabs.Foundation.toString();
		}

    }

}

