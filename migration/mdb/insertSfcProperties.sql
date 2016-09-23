-- Map G2 block properties into Ignition block properties
-- Columns are: FactoryId, ignition propertyName, G2Property
-- NOTE: name, factoryId, uuid are universal attributes and are not included here.

-- GENERIC PROPERTIES --- these are not class-specific. 
insert into SfcPropertyMap values ('generic','callback','callback');
insert into SfcPropertyMap values ('generic','description','description');
insert into SfcPropertyMap values ('generic','message','messageText');
insert into SfcPropertyMap values ('generic','name','name');
insert into SfcPropertyMap values ('generic','priority','priority');
insert into SfcPropertyMap values ('generic','recipeLocation','recipeLocation');

-- UNMAPPED PROPERTIES --- these are not class-specific. If g2Property is empty, we just ignore.
insert into SfcPropertyMap values ('generic','buttonLabel','');
insert into SfcPropertyMap values ('generic','chart-path','');
insert into SfcPropertyMap values ('generic','collectDataConfig','');
insert into SfcPropertyMap values ('generic','confirmControllersConfig','');
insert into SfcPropertyMap values ('generic','g2Xml','');
insert into SfcPropertyMap values ('generic','id','');
insert into SfcPropertyMap values ('generic','manualDataConfig','');
insert into SfcPropertyMap values ('generic','monitorDownloadsConfig','');
insert into SfcPropertyMap values ('generic','pvMonitorConfig','');
insert into SfcPropertyMap values ('generic','verbose','');
insert into SfcPropertyMap values ('generic','writeOutputConfig','');

-- ABORT
insert into SfcPropertyMap values ('com.ils.abortStep','ackRequired','ackRequired');

-- CLOSE WINDOW
insert into SfcPropertyMap values ('com.ils.closeWindowStep','targetType','targetType');
insert into SfcPropertyMap values ('com.ils.closeWindowStep','window','window');

-- CONTROL PANEL MESSAGE
insert into SfcPropertyMap values ('com.ils.controlPanelMessageStep','ackRequired','ackRequired');
insert into SfcPropertyMap values ('com.ils.controlPanelMessageStep','messageText','message');
insert into SfcPropertyMap values ('com.ils.controlPanelMessageStep','postToQueue','postToQueue');
insert into SfcPropertyMap values ('com.ils.controlPanelMessageStep','timeout','timeout');
insert into SfcPropertyMap values ('com.ils.controlPanelMessageStep','timeoutUnit','timeoutUnit');

-- DIALOG MESSAGE
insert into SfcPropertyMap values ('com.ils.dialogMessageStep','ackRequired','acknowledgementRequired');
insert into SfcPropertyMap values ('com.ils.dialogMessageStep','key','recipeDataKey');
insert into SfcPropertyMap values ('com.ils.dialogMessageStep','position','');
insert into SfcPropertyMap values ('com.ils.dialogMessageStep','scale','');
insert into SfcPropertyMap values ('com.ils.dialogMessageStep','strategy','strategy');
insert into SfcPropertyMap values ('com.ils.dialogMessageStep','timeout','timeout');
insert into SfcPropertyMap values ('com.ils.dialogMessageStep','timeoutUnit','timeoutUnits');
insert into SfcPropertyMap values ('com.ils.dialogMessageStep','window','');
insert into SfcPropertyMap values ('com.ils.dialogMessageStep','windowTitle','');

-- GET INPUT
insert into SfcPropertyMap values ('com.ils.inputStep','key','key');
insert into SfcPropertyMap values ('com.ils.inputStep','recipeLocation','recipe-data-location');
insert into SfcPropertyMap values ('com.ils.inputStep','position','');
insert into SfcPropertyMap values ('com.ils.inputStep','prompt','prompt');
insert into SfcPropertyMap values ('com.ils.inputStep','scale','');
insert into SfcPropertyMap values ('com.ils.inputStep','timeout','');
insert into SfcPropertyMap values ('com.ils.inputStep','timeoutUnit','');
insert into SfcPropertyMap values ('com.ils.inputStep','windowTitle','');

-- ENABLE-DISABLE
insert into SfcPropertyMap values ('com.ils.enableDisableStep','enableCancel','');
insert into SfcPropertyMap values ('com.ils.enableDisableStep','enablePause','');
insert into SfcPropertyMap values ('com.ils.enableDisableStep','enableResume','');

-- MANUAL DATA ENTRY
-- Note that spreadsheetPopulateMethod and spreadsheetSpecification are not directly translated
insert into SfcPropertyMap values ('com.ils.manualDataEntryStep','timeout','manualEntryTimeoutInSeconds');
insert into SfcPropertyMap values ('com.ils.manualDataEntryStep','requireInputs','requireAllInputs');
insert into SfcPropertyMap values ('com.ils.manualDataEntryStep','autoMode','mode');
insert into SfcPropertyMap values ('com.ils.manualDataEntryStep','window','dialog');
insert into SfcPropertyMap values ('com.ils.manualDataEntryStep','windowTitle','header');
insert into SfcPropertyMap values ('com.ils.manualDataEntryStep','buttonLabel','toolbarButtonLabel');
insert into SfcPropertyMap values ('com.ils.manualDataEntryStep','scale','workspaceScale');
insert into SfcPropertyMap values ('com.ils.manualDataEntryStep','position','workspaceLocation');
insert into SfcPropertyMap values ('com.ils.manualDataEntryStep','requireAllInputs','requireAllInputs');
insert into SfcPropertyMap values ('com.ils.manualDataEntryStep','timeoutUnit','timeoutUnit');

-- MONITOR-DOWNLOADS
insert into SfcPropertyMap values ('com.ils.monitorDownloadStep','position','');
insert into SfcPropertyMap values ('com.ils.monitorDownloadStep','scale','workspaceScale');
insert into SfcPropertyMap values ('com.ils.monitorDownloadStep','timerClear','clearTimer');
insert into SfcPropertyMap values ('com.ils.monitorDownloadStep','timerKey','');
insert into SfcPropertyMap values ('com.ils.monitorDownloadStep','timerLocation','timerSource');
insert into SfcPropertyMap values ('com.ils.monitorDownloadStep','timerSet','setTimer');
insert into SfcPropertyMap values ('com.ils.monitorDownloadStep','window','');
insert into SfcPropertyMap values ('com.ils.monitorDownloadStep','windowTitle','');

-- S88-OPERATION
insert into SfcPropertyMap values ('com.ils.operationStep','execution-mode','');
insert into SfcPropertyMap values ('com.ils.operationStep','hideControlPanelWhenComplete','hide-control-panel-when-complete');
insert into SfcPropertyMap values ('com.ils.operationStep','msgQueue','message-queue-name');
insert into SfcPropertyMap values ('com.ils.operationStep','passed-parameters','');
insert into SfcPropertyMap values ('com.ils.operationStep','publishStatusToControlPanel','publish-status-to-control-panel');
insert into SfcPropertyMap values ('com.ils.operationStep','return-parameters','');
-- S88-PHASE
insert into SfcPropertyMap values ('com.ils.phaseStep','execution-mode','');
insert into SfcPropertyMap values ('com.ils.phaseStep','passed-parameters','');
insert into SfcPropertyMap values ('com.ils.phaseStep','return-parameters','');

-- S88-TIME-DELAY
insert into SfcPropertyMap values ('com.ils.timedDelayStep','auditLevel','');
insert into SfcPropertyMap values ('com.ils.timedDelayStep','callback','callback');
insert into SfcPropertyMap values ('com.ils.timedDelayStep','delay','delay-time');
insert into SfcPropertyMap values ('com.ils.timedDelayStep','delayUnit','delay-units');
insert into SfcPropertyMap values ('com.ils.timedDelayStep','key','identifier-or-name');
insert into SfcPropertyMap values ('com.ils.timedDelayStep','position','');
insert into SfcPropertyMap values ('com.ils.timedDelayStep','postNotification','post-notification');
insert into SfcPropertyMap values ('com.ils.timedDelayStep','recipeLocation','recipe-location');
insert into SfcPropertyMap values ('com.ils.timedDelayStep','scale','');
insert into SfcPropertyMap values ('com.ils.timedDelayStep','strategy','strategy');
insert into SfcPropertyMap values ('com.ils.timedDelayStep','tagPath','');
insert into SfcPropertyMap values ('com.ils.timedDelayStep','windowTitle','');

-- QUEUE MESSAGE
insert into SfcPropertyMap values ('com.ils.queueMessageStep','key','');
insert into SfcPropertyMap values ('com.ils.queueMessageStep','prompt','');
insert into SfcPropertyMap values ('com.ils.queueMessageStep','recipeLocation','recipeLocation');

-- PV MONITOR
insert into SfcPropertyMap values ('com.ils.pvMonitorStep','dataLocation','monitorRecipeLocation');
insert into SfcPropertyMap values ('com.ils.pvMonitorStep','key','monitorKey');
insert into SfcPropertyMap values ('com.ils.pvMonitorStep','recipeLocation','recipeLocation');
insert into SfcPropertyMap values ('com.ils.pvMonitorStep','strategy','monitorStrategy');
insert into SfcPropertyMap values ('com.ils.pvMonitorStep','timerKey','timerKey');
insert into SfcPropertyMap values ('com.ils.pvMonitorStep','timerLocation','timerSource');
insert into SfcPropertyMap values ('com.ils.pvMonitorStep','timerSet','setTimer');
insert into SfcPropertyMap values ('com.ils.pvMonitorStep','value','monitorLocalValue');

-- RAW QUERY
insert into SfcPropertyMap values ('com.ils.rawQueryStep','key','key');
insert into SfcPropertyMap values ('com.ils.rawQueryStep','sql','sql');

-- REVIEW DATA
insert into SfcPropertyMap values ('com.ils.reviewDataStep','autoMode','mode');
insert into SfcPropertyMap values ('com.ils.reviewDataStep','buttonKey','selectedButtonKey');
insert into SfcPropertyMap values ('com.ils.reviewDataStep','buttonKeyLocation','');
insert into SfcPropertyMap values ('com.ils.reviewDataStep','position','');
insert into SfcPropertyMap values ('com.ils.reviewDataStep','primaryReviewData','');
insert into SfcPropertyMap values ('com.ils.reviewDataStep','primaryTabLabel','');
insert into SfcPropertyMap values ('com.ils.reviewDataStep','secondaryReviewData','');
insert into SfcPropertyMap values ('com.ils.reviewDataStep','secondaryTabLabel','');
insert into SfcPropertyMap values ('com.ils.reviewDataStep','scale','workspaceScale');
insert into SfcPropertyMap values ('com.ils.reviewDataStep','timeout','');
insert into SfcPropertyMap values ('com.ils.reviewDataStep','timeoutUnit','');
insert into SfcPropertyMap values ('com.ils.reviewDataStep','window','');
insert into SfcPropertyMap values ('com.ils.reviewDataStep','windowTitle','header');

-- REVIEW DATA (with ADVICE)
insert into SfcPropertyMap values ('com.ils.reviewDataWithAdviceStep','autoMode','mode');
insert into SfcPropertyMap values ('com.ils.reviewDataWithAdviceStep','buttonKey','selectedButtonKey');
insert into SfcPropertyMap values ('com.ils.reviewDataWithAdviceStep','buttonKeyLocation','');
insert into SfcPropertyMap values ('com.ils.reviewDataWithAdviceStep','position','');
insert into SfcPropertyMap values ('com.ils.reviewDataWithAdviceStep','primaryReviewData','');
insert into SfcPropertyMap values ('com.ils.reviewDataWithAdviceStep','primaryReviewDataWithAdvice','');
insert into SfcPropertyMap values ('com.ils.reviewDataWithAdviceStep','primaryTabLabel','');
insert into SfcPropertyMap values ('com.ils.reviewDataWithAdviceStep','secondaryReviewData','');
insert into SfcPropertyMap values ('com.ils.reviewDataWithAdviceStep','secondaryReviewDataWithAdvice','');
insert into SfcPropertyMap values ('com.ils.reviewDataWithAdviceStep','secondaryTabLabel','');
insert into SfcPropertyMap values ('com.ils.reviewDataWithAdviceStep','scale','workspaceScale');
insert into SfcPropertyMap values ('com.ils.reviewDataWithAdviceStep','timeout','');
insert into SfcPropertyMap values ('com.ils.reviewDataWithAdviceStep','timeoutUnit','');
insert into SfcPropertyMap values ('com.ils.reviewDataWithAdviceStep','window','');
insert into SfcPropertyMap values ('com.ils.reviewDataWithAdviceStep','windowTitle','header');

-- REVIEW FLOWS
insert into SfcPropertyMap values ('com.ils.reviewFlowsStep','autoMode','mode');
insert into SfcPropertyMap values ('com.ils.reviewFlowsStep','buttonKey','selectedButtonKey');
insert into SfcPropertyMap values ('com.ils.reviewFlowsStep','buttonKeyLocation','');
insert into SfcPropertyMap values ('com.ils.reviewFlowsStep','heading1','flow-1-column-header');
insert into SfcPropertyMap values ('com.ils.reviewFlowsStep','heading2','flow-2-column-header');
insert into SfcPropertyMap values ('com.ils.reviewFlowsStep','heading3','flow3ColumnHeader');
insert into SfcPropertyMap values ('com.ils.reviewFlowsStep','position','');
insert into SfcPropertyMap values ('com.ils.reviewFlowsStep','primaryReviewData','');
insert into SfcPropertyMap values ('com.ils.reviewFlowsStep','primaryTabLabel','');
insert into SfcPropertyMap values ('com.ils.reviewFlowsStep','reviewFlows','');
insert into SfcPropertyMap values ('com.ils.reviewFlowsStep','secondaryReviewData','');
insert into SfcPropertyMap values ('com.ils.reviewFlowsStep','secondaryTabLabel','');
insert into SfcPropertyMap values ('com.ils.reviewFlowsStep','scale','workspaceScale');
insert into SfcPropertyMap values ('com.ils.reviewFlowsStep','timeout','');
insert into SfcPropertyMap values ('com.ils.reviewFlowsStep','timeoutUnit','');
insert into SfcPropertyMap values ('com.ils.reviewFlowsStep','window','');
insert into SfcPropertyMap values ('com.ils.reviewFlowsStep','windowTitle','header');

-- SAVE DATA
insert into SfcPropertyMap values ('com.ils.saveDataStep','directory','directory');
insert into SfcPropertyMap values ('com.ils.saveDataStep','extension','extension');
insert into SfcPropertyMap values ('com.ils.saveDataStep','filename','filename');
insert into SfcPropertyMap values ('com.ils.saveDataStep','position','');
insert into SfcPropertyMap values ('com.ils.saveDataStep','printFile','printFile');
insert into SfcPropertyMap values ('com.ils.saveDataStep','recipeLocation','recipeLocation');
insert into SfcPropertyMap values ('com.ils.saveDataStep','scale','');
insert into SfcPropertyMap values ('com.ils.saveDataStep','showPrintDialog','');
insert into SfcPropertyMap values ('com.ils.saveDataStep','timestamp','appendTimestamp');
insert into SfcPropertyMap values ('com.ils.saveDataStep','viewFile','viewFile');
insert into SfcPropertyMap values ('com.ils.saveDataStep','windowTitle','');

-- SAVE QUEUE
insert into SfcPropertyMap values ('com.ils.saveQueueStep','directory','directory');
insert into SfcPropertyMap values ('com.ils.saveQueueStep','filename','filename');

-- SELECT INPUT
insert into SfcPropertyMap values ('com.ils.selectInputStep','choicesKey','key');
insert into SfcPropertyMap values ('com.ils.selectInputStep','choicesRecipeLocation','key');
insert into SfcPropertyMap values ('com.ils.selectInputStep','key','key');
insert into SfcPropertyMap values ('com.ils.selectInputStep','position','');
insert into SfcPropertyMap values ('com.ils.selectInputStep','prompt','prompt');
insert into SfcPropertyMap values ('com.ils.selectInputStep','recipeLocation','recipe-data-location');
insert into SfcPropertyMap values ('com.ils.selectInputStep','scale','');
insert into SfcPropertyMap values ('com.ils.selectInputStep','timeout','');
insert into SfcPropertyMap values ('com.ils.selectInputStep','timeoutUnit','');
insert into SfcPropertyMap values ('com.ils.selectInputStep','windowTitle','');

-- SHOW WINDOW
insert into SfcPropertyMap values ('com.ils.showWindowStep','position','');
insert into SfcPropertyMap values ('com.ils.showWindowStep','scale','');
insert into SfcPropertyMap values ('com.ils.showWindowStep','security','security');
insert into SfcPropertyMap values ('com.ils.showWindowStep','targetType','targetType');
insert into SfcPropertyMap values ('com.ils.showWindowStep','window','window');
insert into SfcPropertyMap values ('com.ils.showWindowStep','windowTitle','');

-- SIMPLE QUERY
insert into SfcPropertyMap values ('com.ils.simpleQueryStep','fetchMode','fetchMode');
insert into SfcPropertyMap values ('com.ils.simpleQueryStep','key','key');
insert into SfcPropertyMap values ('com.ils.simpleQueryStep','keyMode','keyMode');
insert into SfcPropertyMap values ('com.ils.simpleQueryStep','resultsMode','resultsMode');
insert into SfcPropertyMap values ('com.ils.simpleQueryStep','sql','sql');
insert into SfcPropertyMap values ('com.ils.simpleQueryStep','updateMode','updateMode');

-- S88-UNIT-PROCEDURE
insert into SfcPropertyMap values ('com.ils.procedureStep','conditionalBlockRecheckIntervalSeconds','conditional-block-recheck-interval-seconds');
insert into SfcPropertyMap values ('com.ils.procedureStep','execution-mode','');
insert into SfcPropertyMap values ('com.ils.procedureStep','msgQueue','message-queue-name');
insert into SfcPropertyMap values ('com.ils.procedureStep','passed-parameters','');
insert into SfcPropertyMap values ('com.ils.procedureStep','showControlPanel','show-control-panel');
insert into SfcPropertyMap values ('com.ils.procedureStep','return-parameters','');


-- WRITE OUTPUT
insert into SfcPropertyMap values ('com.ils.writeOutputStep','recipeLocation','recipeLocation');
insert into SfcPropertyMap values ('com.ils.writeOutputStep','timerKey','timerKey');
insert into SfcPropertyMap values ('com.ils.writeOutputStep','timerLocation','timerSource');
insert into SfcPropertyMap values ('com.ils.writeOutputStep','timerSet','setTimer');
insert into SfcPropertyMap values ('com.ils.writeOutputStep','description','writeOutputConfig');

-- YES NO
insert into SfcPropertyMap values ('com.ils.yesNoStep','key','key');
insert into SfcPropertyMap values ('com.ils.yesNoStep','recipeDataLocation','recipe-data-location');
insert into SfcPropertyMap values ('com.ils.yesNoStep','position','');
insert into SfcPropertyMap values ('com.ils.yesNoStep','prompt','prompt');
insert into SfcPropertyMap values ('com.ils.yesNoStep','scale','');
insert into SfcPropertyMap values ('com.ils.yesNoStep','timeout','');
insert into SfcPropertyMap values ('com.ils.yesNoStep','timeoutUnit','');
insert into SfcPropertyMap values ('com.ils.yesNoStep','windowTitle','');
