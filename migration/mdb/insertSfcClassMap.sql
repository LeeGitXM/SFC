-- Fields aare:  G2 class name, Ignition factory-id, type (enclosure/transition/ )
-- insert into SfcClassMap values ('S88-UNIT-PROCEDURE','enclosing-step','enclosure');
insert into SfcClassMap values ('EM-S88-INITIALIZE-CONTAINER-DATA','action-step','');     -- target
insert into SfcClassMap values ('EM-S88-RATE-CHANGE-CURRENT-DATA-CALLBACK','action-step','');
insert into SfcClassMap values ('EM-S88-RATE-CHANGE-NEW-DATA-CALLBACK','action-step','');
insert into SfcClassMap values ('EM-S88-REFRESH-OPC-INTERFACE','action-step','');         -- target
insert into SfcClassMap values ('S88-ABORT-BLOCK', 'transition','transition');
insert into SfcClassMap values ('S88-ABORT-TASK', 'com.ils.abortStep','');
insert into SfcClassMap values ('S88-ACTIVATE-WORKSPACE','com.ils.showWindowStep','');         -- target, targetType,postDwellTime
insert into SfcClassMap values ('S88-BEGIN','begin-step','');
insert into SfcClassMap values ('S88-CALLBACK','action-step','');
insert into SfcClassMap values ('S88-CLEAR-QUEUE-TASK','com.ils.clearQueueStep','');
insert into SfcClassMap values ('S88-COLLECT-DATA-TASK','com.ils.collectDataStep','');
insert into SfcClassMap values ('S88-CONDITIONAL-TRANSITION','transition','transition');
insert into SfcClassMap values ('S88-CONFIRM-CONTROLLER-MODES-TASK','com.ils.confirmControllersStep','');
insert into SfcClassMap values ('S88-DATABASE-RAW-QUERY-TASK','com.ils.rawQueryStep','');
insert into SfcClassMap values ('S88-DATABASE-SIMPLE-QUERY-TASK','com.ils.simpleQueryStep','');
insert into SfcClassMap values ('S88-DEACTIVATE-WORKSPACE','com.ils.closeWindowStep','');
insert into SfcClassMap values ('S88-ENABLE-OR-DISABLE-COMMAND-TASK','com.ils.enableDisableStep','');
insert into SfcClassMap values ('S88-ENCAPSULATION-TASK', 'enclosing-step','enclosure');
insert into SfcClassMap values ('S88-END', 'end-step','');
insert into SfcClassMap values ('S88-GET-INPUT-TASK','com.ils.inputStep','');
insert into SfcClassMap values ('S88-GET-SIMPLE-QUANTITY-WITH-LIMITS-TASK','com.ils.inputStep','');
insert into SfcClassMap values ('S88-HIDE-WORKSPACE','com.ils.closeWindowStep','');
insert into SfcClassMap values ('S88-HOLD-TASK','com.ils.pauseStep','');
insert into SfcClassMap values ('S88-LIBRARY-CALLER','enclosing-step','');
insert into SfcClassMap values ('S88-LIBRARY-TASK','enclosing-step','');
insert into SfcClassMap values ('S88-MANUAL-DATA-ENTRY-TASK','com.ils.manualDataEntryStep',''); 
insert into SfcClassMap values ('S88-MESSAGE-CONSOLE-TASK','com.ils.controlPanelMessageStep','');
insert into SfcClassMap values ('S88-MESSAGE-QUEUE-TASK','com.ils.queueMessageStep',''); 
insert into SfcClassMap values ('S88-MONITOR-DOWNLOADS-TASK','com.ils.monitorDownloadStep','');
insert into SfcClassMap values ('S88-OPERATION','com.ils.operationStep','enclosure');
insert into SfcClassMap values ('S88-PARALLEL-TRANSITION','parallel','parallel');
insert into SfcClassMap values ('S88-PAUSE-TASK','com.ils.pauseStep','');
insert into SfcClassMap values ('S88-PHASE','com.ils.phaseStep','enclosure');
insert into SfcClassMap values ('S88-POST-BUSY-NOTIFICATION-TASK','com.ils.postDelayNotification','');
insert into SfcClassMap values ('S88-PRINT-OR-VIEW-FILE-TASK','com.ils.printFileStep','');
insert into SfcClassMap values ('S88-PRINT-WORKSPACE-TASK','com.ils.printWindowStep','');
insert into SfcClassMap values ('S88-PV-MONITORING-TASK','com.ils.pvMonitorStep','');
insert into SfcClassMap values ('S88-REMOVE-BUSY-NOTIFICATION-TASK','com.ils.deleteDelayNotification','');
insert into SfcClassMap values ('S88-RESET-TASK','action-step','');     -- unimplemented
insert into SfcClassMap values ('S88-REVIEW-DATA-TASK','com.ils.reviewDataStep','');
insert into SfcClassMap values ('S88-REVIEW-DATA-WITH-ADVICE-TASK', 'com.ils.reviewDataWithAdviceStep','');
insert into SfcClassMap values ('S88-REVIEW-FLOWS-TASK','com.ils.reviewFlowsStep','');
insert into SfcClassMap values ('S88-SAVE-QUEUE-TASK','com.ils.saveQueueStep',''); 
insert into SfcClassMap values ('S88-SAVE-RECIPE-DATA-TO-CSV','com.ils.saveDataStep','');
insert into SfcClassMap values ('S88-SELECT-INPUT-TASK','com.ils.selectInputStep','');
insert into SfcClassMap values ('S88-SET-QUEUE-TASK','com.ils.setQueueStep','');
insert into SfcClassMap values ('S88-SHOW-NOTIFICATION-DIALOG-TASK','com.ils.dialogMessageStep','');
insert into SfcClassMap values ('S88-SHOW-QUEUE-TASK','com.ils.showQueueStep','');
insert into SfcClassMap values ('S88-SHOW-WORKSPACE','com.ils.showWindowStep','');             -- target, targetType,toolbarButtonLabel,workspaceLocation, workspaceScale,security
insert into SfcClassMap values ('S88-STOP-BLOCK','transition','transition');
insert into SfcClassMap values ('S88-START-DATA-PUMP','action-step','');            -- pumpName
insert into SfcClassMap values ('S88-STOP-DATA-PUMPS','action-step','');            -- pumpName
insert into SfcClassMap values ('S88-STOP-TASK','com.ils.abortStep','');
insert into SfcClassMap values ('S88-TERMINATION-TRANSITION','transition','transition');
insert into SfcClassMap values ('S88-TIME-DELAY','com.ils.timedDelayStep','');
insert into SfcClassMap values ('S88-TIMEOUT-BLOCK','transition','transition');
insert into SfcClassMap values ('S88-UNIT-PROCEDURE','com.ils.procedureStep','enclosure');
insert into SfcClassMap values ('S88-WRITE-FILE-TASK','com.ils.writeFileStep','');
insert into SfcClassMap values ('S88-WRITE-OUTPUTS-TASK','com.ils.writeOutputStep','');
insert into SfcClassMap values ('S88-YES-NO-TASK','com.ils.yesNoStep','');

