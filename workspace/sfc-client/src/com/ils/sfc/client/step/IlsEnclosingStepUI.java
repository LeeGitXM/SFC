package com.ils.sfc.client.step;

import javax.swing.Icon;

import com.ils.sfc.common.step.IlsEnclosingStepDelegate;
import com.inductiveautomation.sfc.client.api.ClientStepFactory;
import com.inductiveautomation.sfc.client.api.StepUI;
import com.inductiveautomation.sfc.elements.steps.enclosing.EnclosingStepProperties;
import com.inductiveautomation.sfc.elements.steps.enclosing.ExecutionMode;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

public class IlsEnclosingStepUI extends AbstractIlsStepUI {
    public static final ClientStepFactory FACTORY = new IlsEnclosingStepFactory();

    public IlsEnclosingStepUI() {
	}
		
   	@Override
	protected Icon getIcon() { return null; }
	
	@Override
	protected String getText() { return "<html><b><font color=red>IlsEnclosing!</html>"; }

    public static final class IlsEnclosingStepFactory extends IlsEnclosingStepDelegate implements ClientStepFactory {
    	private IlsEnclosingStepUI UI = new IlsEnclosingStepUI();

        @Override
        public StepUI createStepUI(ChartUIElement element) {
            return UI;
        }

        @Override        
        public Icon getPaletteIcon() {
            return null; 
        }

        @Override
        public Icon getRolloverPaletteIcon() {
            return getPaletteIcon(); 
        }

        @Override
        public String getPaletteText() {
            return "IlsEnclosing";
        }

        @Override
        public String getPaletteTooltip() {
            return getPaletteText();
        }

        @Override
        public void initializeStep(ChartUIElement element) {
        	element.set(EnclosingStepProperties.EXECUTION_MODE, ExecutionMode.RunUntilCompletion);
        }		
	
		@Override
		public String getCategory() {
			return PaletteTabs.Control.toString();
		}

    }

}

