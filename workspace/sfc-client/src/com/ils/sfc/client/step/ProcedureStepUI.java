package com.ils.sfc.client.step;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.ils.sfc.common.IlsSfcNames;
import com.ils.sfc.common.step.ProcedureStepDelegate;
import com.inductiveautomation.sfc.client.api.ClientStepFactory;
import com.inductiveautomation.sfc.client.api.StepUI;
import com.inductiveautomation.sfc.elements.steps.ChartStepProperties;
import com.inductiveautomation.sfc.elements.steps.ExpressionParamCollection;
import com.inductiveautomation.sfc.elements.steps.enclosing.EnclosingStepProperties;
import com.inductiveautomation.sfc.elements.steps.enclosing.ExecutionMode;
import com.inductiveautomation.sfc.elements.steps.enclosing.ReturnParamCollection;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

public class ProcedureStepUI extends AbstractIlsStepUI {
    public static final ClientStepFactory FACTORY = new ProcedureStepFactory();
	protected static Icon procedureIcon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/procedure.png"));

    public ProcedureStepUI() {
	}
		
   	@Override
	protected Icon getIcon() { return null; }
	
	@Override
	protected String getText() { return "<html><b>Procedure</html>"; }

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
             element.merge(getPropertySet());
              initializeFoundationStepUI(element, IlsSfcNames.GLOBAL);
        }
	
		@Override
		public String getCategory() {
			return PaletteTabs.Foundation.toString();
		}

    }

}
