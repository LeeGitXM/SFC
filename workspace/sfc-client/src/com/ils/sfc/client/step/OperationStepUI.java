package com.ils.sfc.client.step;

import java.awt.Dimension;
import java.awt.Image;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.ils.sfc.common.step.OperationStepDelegate;
import com.inductiveautomation.sfc.client.api.ClientStepFactory;
import com.inductiveautomation.sfc.client.api.StepUI;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

public class OperationStepUI extends AbstractIlsStepUI {
	protected static Icon operationIcon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/operation.png"));

    public static final ClientStepFactory FACTORY = new OperationStepFactory();

    public OperationStepUI() {
	}
		
   	@Override
	protected Icon getIcon() { return null; }
	
	@Override
	protected String getText() { return "<html><b><c>Operation</html>"; }

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
            return "Operation";
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

