package com.ils.icc2.designer.workspace.ui;

import com.ils.common.block.BlockStyle;
import com.ils.icc2.designer.workspace.ProcessBlockView;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;


/**
 * Create a proper UI rendering class given the block style.
 */
public class UIFactory {
	private final static String TAG = "UIFactory";
	private final LoggerEx log;
	public UIFactory() {
		log = LogUtil.getLogger(getClass().getPackage().getName());
	}
	
	public BlockViewUI getUI(BlockStyle style,ProcessBlockView block) {
      
		BlockViewUI ui = null;
		switch(style) {
			case ARROW:
				ui= new ArrowUIView(block);
				break;
			case DIAMOND:
				ui= new DiamondUIView(block);
				break;
			case SQUARE:
			default:
				log.warnf("%s: getUI: Unrecognized style (%s)",TAG,style.toString());
				ui= new SquareUIView(block);   // Default
		}
		log.tracef("%s.getUI: Created style (%s)",TAG,style.toString());
		return ui;
	}
}
