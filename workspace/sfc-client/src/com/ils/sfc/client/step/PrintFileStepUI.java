package com.ils.sfc.client.step;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.ils.sfc.common.step.PrintFileStepDelegate;
import com.inductiveautomation.sfc.client.api.ClientStepFactory;
import com.inductiveautomation.sfc.client.api.StepUI;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

public class PrintFileStepUI extends AbstractIlsStepUI {
	protected static Icon printFileIcon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/printFile.png"));
  
    public static final ClientStepFactory FACTORY = new PrintFileStepFactory();

   	@Override
	protected ImageIcon getIcon() { return null; }
	
	@Override
	protected String getHeading() { return "Print/View File"; }

    public static final class PrintFileStepFactory extends PrintFileStepDelegate implements ClientStepFactory {
    	private PrintFileStepUI UI = new PrintFileStepUI();

        @Override
        public StepUI createStepUI(ChartUIElement element) {
            return UI;
        }

        @Override
        public Icon getPaletteIcon() {
            return printFileIcon; 
        }

        @Override
        public Icon getRolloverPaletteIcon() {
            return getPaletteIcon(); 
        }

        @Override
        public String getPaletteText() {
            return "Print/View";
        }

        @Override
        public String getPaletteTooltip() {
            return getPaletteText();
        }

        @Override
        public void initializeStep(ChartUIElement element) {
        	element.merge(getPropertySet());
        }		
	
		@Override
		public String getCategory() {
			return PaletteTabs.File.toString();
		}

    }

}

