package com.ils.sfc.client;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.ils.sfc.common.TestStepDelegate;
import com.ils.sfc.util.IlsSfcCommonUtils;
import com.inductiveautomation.sfc.client.api.ClientStepFactory;
import com.inductiveautomation.sfc.client.api.StepUI;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

public class TestStepUI extends AbstractIlsStepUI {
  
    public static final ClientStepFactory FACTORY = new TestStepFactory();

    public static final class TestStepFactory extends TestStepDelegate implements ClientStepFactory {
    	private TestStepUI UI = new TestStepUI();
    	private Icon icon = new ImageIcon(getClass().getResource("/images/auction_hammer.png"));
    	private Icon rollover = new ImageIcon(getClass().getResource("/images/auction_hammer.png"));

        @Override
        public StepUI createStepUI(ChartUIElement element) {
            return UI;
        }

        @Override
        public Icon getPaletteIcon() {
            return null; //icon;
        }

        @Override
        public Icon getRolloverPaletteIcon() {
            return null; // rollover;
        }

        @Override
        public String getPaletteText() {
            return "Test Step";
        }

        @Override
        public String getPaletteTooltip() {
            return "A test step";
        }

        @Override
        public void initializeStep(ChartUIElement element) {
        	element.merge(IlsSfcCommonUtils.createPropertySet(properties));
        }		
	
		@Override
		public String getCategory() {
			return "EMC";
		}

    }

}

