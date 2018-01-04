-- Fields are:  G2 class name, Ignition factory-id, type (enclosure/transition/ ), requiresTransition (0,1)
-- insert into SfcClassMap values ('S88-UNIT-PROCEDURE','enclosing-step','enclosure',0);
insert into SfcClassMap values ('EM-S88-INITIALIZE-CONTAINER-DATA','action-step','',0);     -- target
insert into SfcClassMap values ('EM-S88-RATE-CHANGE-CURRENT-DATA-CALLBACK','action-step','',0);
insert into SfcClassMap values ('EM-S88-RATE-CHANGE-NEW-DATA-CALLBACK','action-step','',0);
insert into SfcClassMap values ('EM-S88-REFRESH-OPC-INTERFACE','action-step','',0);         -- target
insert into SfcClassMap values ('LONG-RUNNING-STEP-TRANSITION','transition','transition',0);
insert into SfcClassMap values ('S88-ABORT-BLOCK', 'transition','transition',0);
insert into SfcClassMap values ('S88-ABORT-TASK', 'com.ils.abortStep','',0);
insert into SfcClassMap values ('S88-ACTIVATE-WORKSPACE','com.ils.showWindowStep','',1);         -- target, targetType,postDwellTime
insert into SfcClassMap values ('S88-ADVANCED-CONDITIONAL-TRANSITION','transition','transition',0);
insert into SfcClassMap values ('S88-BEGIN','begin-step','',0);
insert into SfcClassMap values ('S88-CALLBACK','action-step','',0);
insert into SfcClassMap values ('S88-CLEAR-QUEUE-TASK','com.ils.clearQueueStep','',0);
insert into SfcClassMap values ('S88-COLLECT-DATA-TASK','com.ils.collectDataStep','',0);
insert into SfcClassMap values ('S88-CONDITIONAL-TRANSITION','transition','transition',0);
insert into SfcClassMap values ('S88-CONFIRM-CONTROLLER-MODES-TASK','com.ils.confirmControllersStep','',1);
insert into SfcClassMap values ('S88-DATABASE-RAW-QUERY-TASK','com.ils.rawQueryStep','',0);
insert into SfcClassMap values ('S88-DATABASE-SIMPLE-QUERY-TASK','com.ils.simpleQueryStep','',0);
insert into SfcClassMap values ('S88-DEACTIVATE-WORKSPACE','com.ils.closeWindowStep','',0);
insert into SfcClassMap values ('S88-ENABLE-OR-DISABLE-COMMAND-TASK','com.ils.enableDisableStep','',0);
insert into SfcClassMap values ('S88-ENCAPSULATION-TASK', 'enclosing-step','enclosure',0);
insert into SfcClassMap values ('S88-END', 'end-step','',0);
insert into SfcClassMap values ('S88-GET-INPUT-TASK','com.ils.inputStep','',0);
insert into SfcClassMap values ('S88-GET-SIMPLE-QUANTITY-WITH-LIMITS-TASK','com.ils.inputStep','',0);
insert into SfcClassMap values ('S88-HIDE-WORKSPACE','com.ils.closeWindowStep','',0);
insert into SfcClassMap values ('S88-HOLD-TASK','com.ils.pauseStep','',0);
insert into SfcClassMap values ('S88-LIBRARY-CALLER','enclosing-step','',0);
insert into SfcClassMap values ('S88-LIBRARY-TASK','enclosing-step','',0);
insert into SfcClassMap values ('S88-MANUAL-DATA-ENTRY-TASK','com.ils.manualDataEntryStep','',1); 
insert into SfcClassMap values ('S88-MESSAGE-CONSOLE-TASK','com.ils.controlPanelMessageStep','',0);
insert into SfcClassMap values ('S88-MESSAGE-QUEUE-TASK','com.ils.queueMessageStep','',0); 
insert into SfcClassMap values ('S88-MONITOR-DOWNLOADS-TASK','com.ils.monitorDownloadStep','',1);
insert into SfcClassMap values ('S88-OPERATION','com.ils.operationStep','enclosure',0);
insert into SfcClassMap values ('S88-PARALLEL-TRANSITION','parallel','parallel',0);
insert into SfcClassMap values ('S88-PAUSE-TASK','com.ils.pauseStep','',0);
insert into SfcClassMap values ('S88-PHASE','com.ils.phaseStep','enclosure',0);
insert into SfcClassMap values ('S88-POST-BUSY-NOTIFICATION-TASK','com.ils.postDelayNotification','',0);
insert into SfcClassMap values ('S88-PRINT-OR-VIEW-FILE-TASK','com.ils.printFileStep','',0);
insert into SfcClassMap values ('S88-PRINT-WORKSPACE-TASK','com.ils.printWindowStep','',1);
insert into SfcClassMap values ('S88-PV-MONITORING-TASK','com.ils.pvMonitorStep','',1);
insert into SfcClassMap values ('S88-REMOVE-BUSY-NOTIFICATION-TASK','com.ils.deleteDelayNotification','',0);
insert into SfcClassMap values ('S88-RESET-TASK','action-step','',0);     -- unimplemented
insert into SfcClassMap values ('S88-REVIEW-DATA-TASK','com.ils.reviewDataStep','',1);
insert into SfcClassMap values ('S88-REVIEW-DATA-WITH-ADVICE-TASK', 'com.ils.reviewDataWithAdviceStep','',1);
insert into SfcClassMap values ('S88-REVIEW-FLOWS-TASK','com.ils.reviewFlowsStep','',1);
insert into SfcClassMap values ('S88-SAVE-QUEUE-TASK','com.ils.saveQueueStep','',0); 
insert into SfcClassMap values ('S88-SAVE-RECIPE-DATA-TO-CSV','com.ils.saveDataStep','',0);
insert into SfcClassMap values ('S88-SELECT-INPUT-TASK','com.ils.selectInputStep','',1);
insert into SfcClassMap values ('S88-SET-QUEUE-TASK','com.ils.setQueueStep','',0);
insert into SfcClassMap values ('S88-SHOW-NOTIFICATION-DIALOG-TASK','com.ils.dialogMessageStep','',1);
insert into SfcClassMap values ('S88-SHOW-QUEUE-TASK','com.ils.showQueueStep','',0);
insert into SfcClassMap values ('S88-SHOW-WORKSPACE','com.ils.showWindowStep','',1); -- target, targetType,toolbarButtonLabel,workspaceLocation, workspaceScale,security
insert into SfcClassMap values ('S88-STOP-BLOCK','transition','transition',0);
insert into SfcClassMap values ('S88-START-DATA-PUMP','action-step','',0);            -- pumpName
insert into SfcClassMap values ('S88-STOP-DATA-PUMPS','action-step','',0);            -- pumpName
insert into SfcClassMap values ('S88-STOP-TASK','com.ils.abortStep','',0);
insert into SfcClassMap values ('S88-TERMINATION-TRANSITION','transition','transition',0);
insert into SfcClassMap values ('S88-TIME-DELAY','com.ils.timedDelayStep','',1);
insert into SfcClassMap values ('S88-TIMEOUT-BLOCK','transition','transition',0);
insert into SfcClassMap values ('S88-UNIT-PROCEDURE','com.ils.procedureStep','enclosure',0);
insert into SfcClassMap values ('S88-WRITE-FILE-TASK','com.ils.writeFileStep','',0);
insert into SfcClassMap values ('S88-WRITE-OUTPUTS-TASK','com.ils.writeOutputStep','',1);
insert into SfcClassMap values ('S88-YES-NO-TASK','com.ils.yesNoStep','',1);

