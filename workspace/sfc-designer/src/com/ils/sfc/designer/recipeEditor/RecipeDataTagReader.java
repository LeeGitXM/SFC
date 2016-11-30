package com.ils.sfc.designer.recipeEditor;

import java.util.Date;
import java.util.List;

import javax.swing.SwingWorker;

import system.ils.sfc.common.Constants;

import com.ils.sfc.common.IlsSfcCommonUtils;
import com.ils.sfc.common.recipe.objects.Data;
import com.ils.sfc.common.recipe.objects.Group;
import com.inductiveautomation.ignition.common.config.PropertyValue;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;

/** A helper class to read tag values associated with recipe data. Since
 *  this can take awhile, it is done in the background and is cancellable
 *  if a second request comes in before the first is finished.
 *
 */
public class RecipeDataTagReader {
	private static LoggerEx logger = LogUtil.getLogger(RecipeDataTagReader.class.getSimpleName());
	public static final RecipeDataTagReader reader = new RecipeDataTagReader();
	private volatile boolean readingTagValues;
	private volatile boolean cancelTagRead;
	
	/** Read recipe data tag values in the background, as it can be rather 
	 *  time consuming and we don't want to freeze the UI. 
	 *  NOTE: The logic here insures that we will never read the tags.
	 *        The canonical source of recipe data is the serialized chart.
	 */
	public void readRecipeDataTagValues(RecipeEditorController controller) {		
		if(controller.getRecipeData().size() == 0 && !readingTagValues) {
			// nothing to do, just open the browser
			controller.getBrowser().activate(-1);
		}
		else {
			controller.slideTo(RecipeEditorController.LOADING_PANE);
			SwingWorker<Void,Void> loadWorker = createSwingWorker(controller);
			loadWorker.execute();
		}
	}

	/** create a SwingWorker to do the tag reading work in the background */
	private SwingWorker<Void,Void> createSwingWorker(RecipeEditorController controller) {
		return new SwingWorker<Void,Void>() {
			@Override
			public Void doInBackground() throws Exception {
				try {
					readData(controller.getRecipeData());
				}
				catch(Exception e) {
					logger.error("Error reading recipe data tags", e);
				}
				return null;
			}	
			
			@Override
			public void done() {
				if(!cancelTagRead) {
					controller.getBrowser().activate(-1);	
				}
				readingTagValues = false;
			}
		};
	}

	private void cancelPreviousRead() {
		logger.debug("Previous recipe tag read still in progress--cancelling...");
		cancelTagRead = true;
		int maxTries = 10;
		int tryCount = 0;
		while(readingTagValues) {
			if(++tryCount > maxTries) break;
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {}
		}
		if(readingTagValues) {
			logger.warn("Failed to cancel previous recipe tag read--starting new one");
		}
		else {
			logger.debug("Previous recipe tag read cancelled");
		}
	}

	/** Read into a collection of data */
	private void readData(List<Data> recipeData) {
		// If a previous read is still in progress, cancel it before starting the new one
		if(readingTagValues) {
			cancelPreviousRead();
		}
		readingTagValues = true;
		cancelTagRead = false;

		logger.debug("Reading recipe data tags...");
		long startMillis = System.currentTimeMillis();
		for(Data data: recipeData) {
			if(cancelTagRead) {
				break;
			}
			read(data);
		}
		long endMillis = System.currentTimeMillis() - startMillis;
		logger.debugf("Recipe data tags read in %d seconds ", endMillis/1000);
	}

	/** Read a single datum. If the UDT tag exists and has a different 
	 *  value, use the value from the tag. */
	private void readSingle(Data data) {
		if(data.isGroup() || !data.tagExists()) return;
		for(PropertyValue<?> pv: data.getProperties()) {
			if(cancelTagRead) {
				break;
			}
			Object pvalue = pv.getValue();
						
			Object tagValue = data.getTagValue(pv.getProperty());
			if(tagValue instanceof Date) {
				tagValue = Constants.DATE_FORMAT.format((Date)tagValue);
			}
			if(!IlsSfcCommonUtils.equal(pvalue, tagValue)) {
				data.setValue(pv.getProperty(), tagValue);
			}
		}
	}

	/** Read a hierarchy of data. */
	private void read(Data data) {
		if(data.isGroup()) {
			for(Data childData: ((Group)data).getChildren()) {
				read(childData);
			}
		}
		else {
			readSingle(data);
		}
	}
}
