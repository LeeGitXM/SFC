-- Fields aare:  G2 class name, Ignition factory-id, encloses
insert into ClassMap values ('S88-BEGIN','begin-step','false');
insert into ClassMap values ('S88-TIME-DELAY','com.ils.TimedDelayStep','false');
insert into ClassMap values ('S88-ABORT-TASK', 'com.ils.abortStep','false');
insert into ClassMap values ('S88-CALLBACK','action-step','false');
insert into ClassMap values ('S88-CLEAR-QUEUE-TASK','com.ils.clearQueueStep','false');
insert into ClassMap values ('S88-COLLECT-DATA-TASK','com.ils.collectDataStep','false');
insert into ClassMap values ('S88-CONFIRM-CONTROLLER-MODES','com.ils.confirmControllerModesStep','false');
insert into ClassMap values ('S88-MESSAGE-CONSOLE-TASK','com.ils.controlPanelMessageStep','false');
insert into ClassMap values ('S88-REMOVE-BUSY-NOTIFICATION-TASK','com.ils.deleteDelayNotification','false');
insert into ClassMap values ('S88-SHOW-NOTIFICATION-DIALOG-TASK','com.ils.dialogMessageStep','false');
insert into ClassMap values ('S88-ENABLE-OR-DISABLE-COMMAND-TASK','com.ils.enableDisableStep','false');
insert into ClassMap values ('S88-ENCAPSULATION-TASK', 'enclosing-step','true');
insert into ClassMap values ('S88-GET-INPUT-TASK','com.ils.inputStep','false');
insert into ClassMap values ('S88-HOLD-TASK','com.ils.pauseStep','false');
insert into ClassMap values ('S88-LIBRARY-TASK','enclosing-step','false');
insert into ClassMap values ('S88-LIBRARY-CALLER','enclosing-step','false');
insert into ClassMap values ('S88-MANUAL-DATA-ENTRY-TASK','com.ils.dataEntryStep','false');
insert into ClassMap values ('S88-MONITOR-DOWNLOADS-TASK','com.ils.monitorDownloadsStep','false');
insert into ClassMap values ('S88-PV-MONITORING-TASK','com.ils.pvMonitoringStep','false');
insert into ClassMap values ('S88-MESSAGE_QUEUE-TASK','com.ils.queueMessageStep','false');
insert into ClassMap values ('S88-PAUSE-TASK','com.ils.pauseStep','false');
insert into ClassMap values ('S88-POST-BUSY-NOTIFICATION-TASK','com.ils.postDelayNotification','false');
insert into ClassMap values ('S88-PRINT-OR-VIEW-FILE-TASK','com.ils.printFileStep','false');
insert into ClassMap values ('S88-PRINT-WORKSPACE-TASK','com.ils.printWindowStep','false');
insert into ClassMap values ('S88-DATABASE-RAW-QUERY-TASK','com.ils.rawQueryStep','false');
insert into ClassMap values ('S88-REVIEW-DATA-TASK','com.ils.reviewDataStep','false');
insert into ClassMap values ('S88-REVIEW-DATA-WITH-ADVICE-TASK', 'com.ils.reviewDataWithAdviceStep','false');
insert into ClassMap values ('S88-REVIEW-FLOWS-TASK','com.ils.reviewFlowsStep','false');
insert into ClassMap values ('S88-SAVE-RECIPE-DATA-TO-CSV','com.ils.saveDataStep','false');
insert into ClassMap values ('S88-SELECT-INPUT-TASK','com.ils.selectInputStep','false');
insert into ClassMap values ('S88-SET-QUEUE-TASK','com.ils.setQueueStep','false');
insert into ClassMap values ('S88-SHOW-QUEUE-TASK','com.ils.showQueueStep','false');
insert into ClassMap values ('S88-STOP-TASK','com.ils.abortStep','false');
insert into ClassMap values ('S88-DATABASE-SIMPLE-QUERY-TASK','com.ils.simpleQueryStep','false');
insert into ClassMap values ('S88-WRITE-FILE-TASK','com.ils.writeFileStep','false');
insert into ClassMap values ('S88-WRITE-OUTPUTS-TASK','com.ils.writeOutputsStep','false');
insert into ClassMap values ('S88-YES-NO-TASK','com.ils.yesNoStep','false');
insert into ClassMap values ('S88-UNIT-PROCEDURE','enclosing-step','true');
insert into ClassMap values ('S88-OPERATION','enclosing-step','true');
