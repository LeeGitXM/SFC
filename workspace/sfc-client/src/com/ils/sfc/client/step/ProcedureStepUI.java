package com.ils.sfc.client.step;

import java.awt.Dimension;
import java.awt.Image;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.ils.sfc.common.step.ProcedureStepDelegate;
import com.inductiveautomation.sfc.client.api.ClientStepFactory;
import com.inductiveautomation.sfc.client.api.StepUI;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

public class ProcedureStepUI extends AbstractIlsStepUI {
	protected static Icon procedureIcon =  new ImageIcon(AbstractIlsStepUI.class.getResource("/images/procedure.png"));

    public static final ClientStepFactory FACTORY = new ProcedureStepFactory();

    public ProcedureStepUI() {
	}
		
   	@Override
	protected Icon getIcon() { return null; }
	
	@Override
	protected String getText() { return "<html><b><c>Procedure</html>"; }

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
            return "Unit Procedure";
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

