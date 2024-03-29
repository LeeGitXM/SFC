package com.ils.sfc.common.step;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.inductiveautomation.ignition.common.config.Property;

/** A place to hold info about all ILS step class properties. */
public class AllSteps {
	
	private static Map<String, Property<?>[]> propertiesByFactoryId = new HashMap<String, Property<?>[]>();
	//private ChartManagerService chartManager;
	public static String[] editorFactoryIds = {
		CancelStepProperties.FACTORY_ID,
		ClearQueueStepProperties.FACTORY_ID,
		CloseWindowStepProperties.FACTORY_ID,
		CollectDataStepProperties.FACTORY_ID,
		ConfirmControllersStepProperties.FACTORY_ID, 
		ControlPanelMessageStepProperties.FACTORY_ID,
		DeleteDelayNotificationStepProperties.FACTORY_ID,
		DialogMessageStepProperties.FACTORY_ID,
		EnableDisableStepProperties.FACTORY_ID,
		InputStepProperties.FACTORY_ID,
		LimitedInputStepProperties.FACTORY_ID,
		MonitorDownloadStepProperties.FACTORY_ID,   
        ManualDataEntryStepProperties.FACTORY_ID,
        OperationStepProperties.FACTORY_ID,
        OcAlertStepProperties.FACTORY_ID,  
        PauseStepProperties.FACTORY_ID,
        PhaseStepProperties.FACTORY_ID,  
        PostDelayNotificationStepProperties.FACTORY_ID,
        PrintFileStepProperties.FACTORY_ID,
       	PrintWindowStepProperties.FACTORY_ID,
        ProcedureStepProperties.FACTORY_ID,
        PVMonitorStepProperties.FACTORY_ID,
        QueueMessageStepProperties.FACTORY_ID,
        RawQueryStepProperties.FACTORY_ID,
        ReviewDataStepProperties.FACTORY_ID,   
        ReviewDataWithAdviceStepProperties.FACTORY_ID,   
        ReviewFlowsStepProperties.FACTORY_ID, 
        SaveDataStepProperties.FACTORY_ID,
        SaveQueueStepProperties.FACTORY_ID,
       	SelectInputStepProperties.FACTORY_ID,
       	SetQueueStepProperties.FACTORY_ID,
       	ShowQueueStepProperties.FACTORY_ID,
       	ShowWindowStepProperties.FACTORY_ID,
       	SimpleQueryStepProperties.FACTORY_ID,
        TimedDelayStepProperties.FACTORY_ID,
        WriteOutputStepProperties.FACTORY_ID,   
        YesNoStepProperties.FACTORY_ID
	};

	public static List<String> longRunningFactoryIds = Arrays.asList(new String[]{
		// Steps that may require input or an acknowledgement from the user:
        InputStepProperties.FACTORY_ID,
       	SelectInputStepProperties.FACTORY_ID,
       	LimitedInputStepProperties.FACTORY_ID,
    	YesNoStepProperties.FACTORY_ID,
    	ControlPanelMessageStepProperties.FACTORY_ID,
       	DialogMessageStepProperties.FACTORY_ID,
        ReviewDataStepProperties.FACTORY_ID,   
        ReviewDataWithAdviceStepProperties.FACTORY_ID,   
        ReviewFlowsStepProperties.FACTORY_ID,   
        ManualDataEntryStepProperties.FACTORY_ID,   
        // Steps whose own processing runs long:
    	TimedDelayStepProperties.FACTORY_ID,
        PVMonitorStepProperties.FACTORY_ID,     
        WriteOutputStepProperties.FACTORY_ID,    
        OcAlertStepProperties.FACTORY_ID
	});
	
	public static List<String> foundationFactoryIds = Arrays.asList(new String[]{
			// The foundation steps which are really encapsulations
			ProcedureStepProperties.FACTORY_ID,   
	        OperationStepProperties.FACTORY_ID,   
	        PhaseStepProperties.FACTORY_ID,   
		});
	
	public static Class<?>[] propertyClasses = {
    	QueueMessageStepProperties.class,
    	SetQueueStepProperties.class,
    	ShowQueueStepProperties.class,
    	ClearQueueStepProperties.class,
       	SaveQueueStepProperties.class,
    	YesNoStepProperties.class,
    	CancelStepProperties.class,
    	PauseStepProperties.class,
    	ControlPanelMessageStepProperties.class,
    	TimedDelayStepProperties.class,
    	DeleteDelayNotificationStepProperties.class,
    	PostDelayNotificationStepProperties.class,
       	EnableDisableStepProperties.class,
       	SelectInputStepProperties.class,
       	LimitedInputStepProperties.class,
       	DialogMessageStepProperties.class,
       	CollectDataStepProperties.class,
       	InputStepProperties.class,
       	RawQueryStepProperties.class,
       	SimpleQueryStepProperties.class,
       	SaveDataStepProperties.class,
       	PrintFileStepProperties.class,
       	PrintWindowStepProperties.class,
       	CloseWindowStepProperties.class,
       	ShowWindowStepProperties.class,
        ReviewDataStepProperties.class,   
        ReviewDataWithAdviceStepProperties.class,   
        ReviewFlowsStepProperties.class,   
        ProcedureStepProperties.class,   
        OperationStepProperties.class,   
        PhaseStepProperties.class,   
        ConfirmControllersStepProperties.class,   
        WriteOutputStepProperties.class,   
        PVMonitorStepProperties.class,   
        MonitorDownloadStepProperties.class,   
        ManualDataEntryStepProperties.class,   
        OcAlertStepProperties.class  
	};
	
	static {
		for(Class<?> clazz: propertyClasses) {
			try {
				Field factoryIdField = clazz.getField("FACTORY_ID");
				String factoryId = (String)factoryIdField.get(null);
				Field propertyField = clazz.getField("properties"); 
				Property<?>[] properties = (Property<?>[])propertyField.get(null);
				Property<?>[] combinedProperties = new Property<?>[properties.length+AbstractIlsStepDelegate.commonProperties.length];
				System.arraycopy(properties, 0, combinedProperties, 0, properties.length);
				System.arraycopy(AbstractIlsStepDelegate.commonProperties,0,combinedProperties,properties.length,AbstractIlsStepDelegate.commonProperties.length);
				propertiesByFactoryId.put(factoryId, combinedProperties);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	/** Get the declared properties of an Ils step. This will not include the
	 * Ignition step properties like id, name, etc. It does include the generic
	 * properties NAME and DESCRIPTION. 
	 */
	public static Property<?>[] getIlsProperties(String factoryId) {
		return propertiesByFactoryId.get(factoryId);
	}
	
	public static void main(String[] args) {
		for(Property<?> p : getIlsProperties("com.ils.timedDelayStep")) {
			System.out.println(p.getName());
		}
	}
}
