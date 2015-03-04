package com.ils.sfc.client.step;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.ils.sfc.common.IlsSfcNames;
import com.ils.sfc.common.step.OperationStepDelegate;
import com.inductiveautomation.sfc.client.api.ClientStepFactory;
import com.inductiveautomation.sfc.client.api.StepUI;
import com.inductiveautomation.sfc.elements.steps.enclosing.EnclosingStepProperties;
import com.inductiveautomation.sfc.elements.steps.enclosing.ExecutionMode;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

public class OperationStepUI extends AbstractIlsStepUI {
    public static final ClientStepFactory FACTORY = new OperationStepFactory();

    public OperationStepUI() {
	}
		
   	@Override
	protected Icon getIcon() { return null; }
	
	@Override
	protected String getText() { return "<html><b>Operation</html>"; }

    public static final class OperationStepFactory extends OperationStepDelegate implements ClientStepFactory {
    	private OperationStepUI UI = new OperationStepUI();
    	protected static Icon operationIcon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/operation.png"));

        @Override
        public StepUI createStepUI(ChartUIElement element) {
            return UI;
        }

        @Override        
        public Icon getPaletteIcon() {
            return operationIcon; 
        }

        @Override
        public Icon getRolloverPaletteIcon() {
            return getPaletteIcon(); 
        }

        @Override
        public String getPaletteText() {
            return "Operation";
        }

        @Override
        public String getPaletteTooltip() {
            return getPaletteText();
        }

        @Override
        public void initializeStep(ChartUIElement element) {
            element.merge(getPropertySet());
            initializeFoundationStepUI(element, IlsSfcNames.OPERATION);
       }		
	
		@Override
		public String getCategory() {
			return PaletteTabs.Foundation.toString();
		}

    }

}

