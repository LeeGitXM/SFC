-- Map G2 block properties into Ignition block properties
-- Columns are: FactoryId, ignition propertyName, G2Property
-- NOTE: name, factoryId, uuid are universal attributes and are not included here.
insert into SfcPropertyMap values ('enclosing-step','description','description');
-- S88-PHASE
insert into SfcPropertyMap values ('com.ils.phaseStep','description','description');
-- S88-OPERATION
insert into SfcPropertyMap values ('com.ils.operationStep','callback','callback');
insert into SfcPropertyMap values ('com.ils.operationStep','description','description');
insert into SfcPropertyMap values ('com.ils.operationStep','hideControlPanelWhenComplete','UNKNOWN');
insert into SfcPropertyMap values ('com.ils.operationStep','publishStatusToControlPanel','UNKNOWN');
-- S88-UNIT-PROCEDURE
insert into SfcPropertyMap values ('com.ils.procedureStep','conditionalBlockRecheckIntervalSeconds','description');
insert into SfcPropertyMap values ('com.ils.procedureStep','description','description');
insert into SfcPropertyMap values ('com.ils.procedureStep','messageQueueName','queue');
insert into SfcPropertyMap values ('com.ils.procedureStep','showControlPanel','??');
-- S88-TIME-DELAY
insert into SfcPropertyMap values ('com.ils.timedDelayStep','auditLevel','');
insert into SfcPropertyMap values ('com.ils.timedDelayStep','callback','callback');
insert into SfcPropertyMap values ('com.ils.timedDelayStep','delay','delay-time');
insert into SfcPropertyMap values ('com.ils.timedDelayStep','delayUnit','delay-units');
insert into SfcPropertyMap values ('com.ils.timedDelayStep','description','description');
insert into SfcPropertyMap values ('com.ils.timedDelayStep','key','identifier-or-name');
insert into SfcPropertyMap values ('com.ils.timedDelayStep','postNotification','post-notification');
insert into SfcPropertyMap values ('com.ils.timedDelayStep','recipeLocation','recipe-location');
insert into SfcPropertyMap values ('com.ils.timedDelayStep','strategy','strategy');
-- MISC STEPS
insert into SfcPropertyMap values ('action-step','description','description');
insert into SfcPropertyMap values ('com.ils.inputStep','description','description');
insert into SfcPropertyMap values ('com.ils.pauseStep','description','description');
insert into SfcPropertyMap values ('com.ils.clearQueueStep','description','description');
-- CONTROL PANEL MESSAGE
insert into SfcPropertyMap values ('com.ils.controlPanelMessageStep','ackRequired','ackRequired');
insert into SfcPropertyMap values ('com.ils.controlPanelMessageStep','description','description');
insert into SfcPropertyMap values ('com.ils.controlPanelMessageStep','messageText','message');
insert into SfcPropertyMap values ('com.ils.controlPanelMessageStep','postToQueue','postToQueue');
insert into SfcPropertyMap values ('com.ils.controlPanelMessageStep','priority','priority');
insert into SfcPropertyMap values ('com.ils.controlPanelMessageStep','timeout','timeout');
insert into SfcPropertyMap values ('com.ils.controlPanelMessageStep','timeoutUnit','timeoutUnit');
-- QUEUE MESSAGE
insert into SfcPropertyMap values ('com.ils.queueMessageStep','description','description');
insert into SfcPropertyMap values ('com.ils.queueMessageStep','messageText','message');
insert into SfcPropertyMap values ('com.ils.queueMessageStep','UNKNOWN','key');
insert into SfcPropertyMap values ('com.ils.queueMessageStep','UNKNOWN','prompt');
insert into SfcPropertyMap values ('com.ils.queueMessageStep','UNKNOWN','recipeLocation');
-- SAVE DATA
insert into SfcPropertyMap values ('com.ils.saveDataStep','description','description');
insert into SfcPropertyMap values ('com.ils.saveDataStep','directory','directory');
insert into SfcPropertyMap values ('com.ils.saveDataStep','extension','extension');
insert into SfcPropertyMap values ('com.ils.saveDataStep','filename','filename');
insert into SfcPropertyMap values ('com.ils.saveDataStep','recipeLocation','recipeLocation');
insert into SfcPropertyMap values ('com.ils.saveDataStep','appendTimestamp','timestamp');
insert into SfcPropertyMap values ('com.ils.saveDataStep','printFile','printFile');
insert into SfcPropertyMap values ('com.ils.saveDataStep','viewFile','viewFile');
-- WRITE OUTPUT
insert into SfcPropertyMap values ('com.ils.writeOutputStep','description','description');
insert into SfcPropertyMap values ('com.ils.writeOutputStep','recipeLocation','recipeLocation');
insert into SfcPropertyMap values ('com.ils.writeOutputStep','timerKey','timerKey');
insert into SfcPropertyMap values ('com.ils.writeOutputStep','timerSource','timerLocation');
insert into SfcPropertyMap values ('com.ils.writeOutputStep','setTimer','timerSet');
insert into SfcPropertyMap values ('com.ils.writeOutputStep','description','writeOutputConfig');
-- YES NO
insert into SfcPropertyMap values ('com.ils.yesNoStep','description','description');
insert into SfcPropertyMap values ('com.ils.yesNoStep','key','key');
insert into SfcPropertyMap values ('com.ils.yesNoStep','recipeDataLocation','recipeLocation');
insert into SfcPropertyMap values ('com.ils.yesNoStep','prompt','prompt');
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

