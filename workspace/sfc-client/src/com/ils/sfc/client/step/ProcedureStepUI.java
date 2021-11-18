package com.ils.sfc.client.step;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.ils.sfc.common.step.ProcedureStepDelegate;
import com.inductiveautomation.sfc.client.api.ClientStepFactory;
import com.inductiveautomation.sfc.client.api.StepUI;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

import system.ils.sfc.common.Constants;

public class ProcedureStepUI extends AbstractIlsStepUI {
    public static final ClientStepFactory FACTORY = new ProcedureStepFactory();
	protected static Icon procedureIcon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/procedure.png"));

    public ProcedureStepUI() {
	}
		
   	@Override
	protected ImageIcon getIcon() { return null; }
   	
   	@Override
	protected boolean isEncapsulation() { return true; }
	
	@Override
	protected String getHeading() { return "Procedure"; }

    public static final class ProcedureStepFactory extends ProcedureStepDelegate implements ClientStepFactory {
    	private ProcedureStepUI UI = new ProcedureStepUI();

        @Override
        public StepUI createStepUI(ChartUIElement element) {
            return UI;
        }

        @Override        
        public Icon getPaletteIcon() {
            return procedureIcon; 
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
            return "An S88 unit procedure encapsulation";
        }

        @Override
        public void initializeStep(ChartUIElement element) {
             element.merge(getPropertySet());
              initializeFoundationStepUI(element, Constants.GLOBAL);
        }
	
		@Override
		public String getCategory() {
			return PaletteTabs.Foundation.toString();
		}

    }

}

