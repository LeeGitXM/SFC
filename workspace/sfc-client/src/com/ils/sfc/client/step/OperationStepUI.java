package com.ils.sfc.client.step;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.ils.sfc.common.step.OperationStepDelegate;
import com.inductiveautomation.sfc.client.api.ClientStepFactory;
import com.inductiveautomation.sfc.client.api.StepUI;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

import system.ils.sfc.common.Constants;

public class OperationStepUI extends AbstractIlsStepUI {
	protected static Icon operationIcon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/operation.png"));
	protected static ImageIcon operationImageIcon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/operation.png"));
	
    public static final ClientStepFactory FACTORY = new OperationStepFactory();

    public OperationStepUI() {
	}
		
   	@Override
	protected ImageIcon getIcon() { return null; }
   	
   	@Override
	protected boolean isEncapsulation() { return true; }
	
	@Override
	protected String getHeading() { return "Operation"; }

    public static final class OperationStepFactory extends OperationStepDelegate implements ClientStepFactory {
    	private OperationStepUI UI = new OperationStepUI();

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
            return "";
        }

        @Override
        public String getPaletteTooltip() {
            return "An S88 operation encapsulation";
        }

        @Override
        public void initializeStep(ChartUIElement element) {
            element.merge(getPropertySet());
            initializeFoundationStepUI(element, Constants.OPERATION);
       }		
	
		@Override
		public String getCategory() {
			return PaletteTabs.Foundation.toString();
		}

    }

}

