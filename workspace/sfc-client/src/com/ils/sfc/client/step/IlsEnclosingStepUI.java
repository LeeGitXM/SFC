package com.ils.sfc.client.step;

import java.awt.Dimension;
import java.awt.Image;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.ils.sfc.common.step.IlsEnclosingStepDelegate;
import com.ils.sfc.util.IlsSfcCommonUtils;
import com.inductiveautomation.ignition.client.images.ImageLoader;
import com.inductiveautomation.ignition.designer.gui.IconUtil;
import com.inductiveautomation.sfc.client.api.ClientStepFactory;
import com.inductiveautomation.sfc.client.api.StepUI;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

public class IlsEnclosingStepUI extends AbstractIlsStepUI {
	static ImageIcon icon;

    public static final ClientStepFactory FACTORY = new IlsEnclosingStepFactory();

    public IlsEnclosingStepUI() {
    	Dimension size = new Dimension(16,16);
    	String path = "sfc/asterisk.png";
		Image img = ImageLoader.getInstance().loadImage(path,size);
		if( img !=null) icon = new ImageIcon(img);
		if( icon==null ) {
			System.out.println("No image!!");
		}
	}
		
   	@Override
	protected Icon getIcon() { return icon; }
	
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
            return icon; 
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
        	
        }		
	
		@Override
		public String getCategory() {
			return PaletteTabs.Control.toString();
		}

    }

}

