-- Map G2 block properties into Ignition block properties
-- Columns are: G2Class, G2Property, ignition propertyName, datatype, editable, binding type
insert into BltPropertyMap values ('EM-GDA-ABSOLUTE-COMPARE','bandwidth','deadband','DOUBLE',1,'NONE');
insert into BltPropertyMap values ('EM-GDA-ABSOLUTE-COMPARE','label','','STRING',1,'NONE');
insert into BltPropertyMap values ('EM-GDA-BAD-DATA-HANDLER','label','','STRING',1,'NONE');
insert into BltPropertyMap values ('EM-GDA-BAD-DATA-HANDLER','propagateExpirationData','','BOOLEAN',1,'NONE');
insert into BltPropertyMap values ('EM-GDA-CLOCK-TIMER','label','','STRING',1,'NONE');
insert into BltPropertyMap values ('EM-GDA-COMPARE','bandwidth','','DOUBLE',1,'NONE');
insert into BltPropertyMap values ('EM-GDA-COMPARE','label','','STRING',1,'NONE');
insert into BltPropertyMap values ('EM-GDA-DEADBAND-COMPARE','bandwidth','','DOUBLE',1,'NONE');
insert into BltPropertyMap values ('EM-GDA-DEADBAND-COMPARE','label','','STRING',1,'NONE');
insert into BltPropertyMap values ('EM-GDA-ELAPSED-TIME-VARIABLE','elapsedTimeMinutes','','STRING',1,'NONE');
insert into BltPropertyMap values ('EM-GDA-ELAPSED-TIME-VARIABLE','label','','STRING',1,'NONE');
insert into BltPropertyMap values ('EM-GDA-EXPIRATION-FILTER','label','','STRING',1,'NONE');
insert into BltPropertyMap values ('EM-GDA-FINAL-DIAGNOSIS','explanation','Explanation','STRING',1,'NONE');
insert into BltPropertyMap values ('EM-GDA-FINAL-DIAGNOSIS','label','','STRING',1,'NONE');
-- NOTE: properties that are not used in Ignition map to empty strings.
insert into BltPropertyMap values ('EM-GDA-FINAL-DIAGNOSIS','logDiagnosisToDatabase','','BOOLEAN',1,'NONE');
insert into BltPropertyMap values ('EM-GDA-FINAL-DIAGNOSIS','manualMove','','BOOLEAN',1,'NONE');
insert into BltPropertyMap values ('EM-GDA-FINAL-DIAGNOSIS','manualMoveValue','','DOUBLE',1,'NONE');
insert into BltPropertyMap values ('EM-GDA-FINAL-DIAGNOSIS','manualTextRequired','','BOOLEAN',1,'NONE');
insert into BltPropertyMap values ('EM-GDA-FINAL-DIAGNOSIS','postTextRecommendation','PostRecommendation','BOOLEAN',1,'NONE');
insert into BltPropertyMap values ('EM-GDA-FINAL-DIAGNOSIS','priority','Priority','DOUBLE',1,'NONE');
insert into BltPropertyMap values ('EM-GDA-FINAL-DIAGNOSIS','recommendationCalculationMethod','CalculationMethod','STRING',1,'NONE');
insert into BltPropertyMap values ('EM-GDA-FINAL-DIAGNOSIS','recommendationMultiplier','','DOUBLE',1,'NONE');
insert into BltPropertyMap values ('EM-GDA-FINAL-DIAGNOSIS','recommendationRefreshRateInMinutes','RecommendationRefreshInterval','DOUBLE',1,'NONE');
insert into BltPropertyMap values ('EM-GDA-FINAL-DIAGNOSIS','targets','Targets','LIST',1,'NONE');
insert into BltPropertyMap values ('EM-GDA-FINAL-DIAGNOSIS','textRecommendation','Recommendation','STRING',1,'NONE');
insert into BltPropertyMap values ('EM-GDA-FINAL-DIAGNOSIS','textRecommendationCallback','RecommendationCallback','STRING',1,'NONE');
insert into BltPropertyMap values ('EM-GDA-FINAL-DIAGNOSIS','trapInsignificantRecommendationConditions','','BOOLEAN',1,'NONE');
insert into BltPropertyMap values ('EM-GDA-HIGH-LIMIT','label','','STRING',1,'NONE');
insert into BltPropertyMap values ('EM-GDA-HIGH-LIMIT','statusOnInitialization','','STRING',1,'NONE');
insert into BltPropertyMap values ('EM-GDA-HIGH-LIMIT','target','limit','DOUBLE',1,'NONE');
insert into BltPropertyMap values ('EM-GDA-HIGH-LIMIT-WITH-DEADBAND','bandwidth','deadband','DOUBLE',1,'NONE');
insert into BltPropertyMap values ('EM-GDA-HIGH-LIMIT-WITH-DEADBAND','label','','STRING',1,'NONE');
insert into BltPropertyMap values ('EM-GDA-HIGH-LIMIT-WITH-DEADBAND','statusOnInitialization','','STRING',1,'NONE');
insert into BltPropertyMap values ('EM-GDA-HIGH-LIMIT-WITH-DEADBAND','target','limit','DOUBLE',1,'NONE');
insert into BltPropertyMap values ('EM-GDA-LATCH','label','','STRING',1,'NONE');
insert into BltPropertyMap values ('EM-GDA-LOGIC-FILTER','bandwidth','','DOUBLE',1,'NONE');
insert into BltPropertyMap values ('EM-GDA-LOGIC-FILTER','label','','STRING',1,'NONE');
insert into BltPropertyMap values ('EM-GDA-LOGIC-FILTER','filterTimInMinutes','TimeWindow','DOUBLE',1,'NONE');
insert into BltPropertyMap values ('EM-GDA-LOGIC-FILTER','fractionTrueRequired','MinimumTrueFraction','DOUBLE',1,'NONE');
insert into BltPropertyMap values ('EM-GDA-LOGIC-FILTER','hysteresis','Hysteresis','STRING',1,'NONE');
insert into BltPropertyMap values ('EM-GDA-LOGIC-FILTER','recalculationIntervalInSeconds','scanInterval','DOUBLE',1,'NONE');
insert into BltPropertyMap values ('EM-GDA-LOGICAL-VARIABLE','label','','STRING',1,'NONE');
insert into BltPropertyMap values ('EM-GDA-LOW-LIMIT','label','','STRING',1,'NONE');
insert into BltPropertyMap values ('EM-GDA-LOW-LIMIT','statusOnInitialization','','STRING',1,'NONE');
insert into BltPropertyMap values ('EM-GDA-LOW-LIMIT','target','limit','DOUBLE',1,'NONE');
insert into BltPropertyMap values ('EM-GDA-LOW-LIMIT-WITH-DEADBAND','bandwidth','deadband','DOUBLE',1,'NONE');
insert into BltPropertyMap values ('EM-GDA-LOW-LIMIT-WITH-DEADBAND','label','','STRING',1,'NONE');
insert into BltPropertyMap values ('EM-GDA-LOW-LIMIT-WITH-DEADBAND','statusOnInitialization','','STRING',1,'NONE');
insert into BltPropertyMap values ('EM-GDA-LOW-LIMIT-WITH-DEADBAND','target','limit','DOUBLE',1,'NONE');
insert into BltPropertyMap values ('EM-GDA-SIMPLE-TREND-OBSERVATION','label','','STRING',1,'NONE');
insert into BltPropertyMap values ('EM-GDA-SIMPLE-TREND-OBSERVATION','numberoftrendpointsrequired','','INTEGER',1,'NONE');
insert into BltPropertyMap values ('EM-GDA-SIMPLE-TREND-OBSERVATION','relativetotarget','','DOUBLE',1,'NONE');
insert into BltPropertyMap values ('EM-GDA-SIMPLE-TREND-OBSERVATION','standarddeviationmultiplicativefactor','','DOUBLE',1,'NONE');
insert into BltPropertyMap values ('EM-GDA-SIMPLE-TREND-OBSERVATION','trendtestlabel','','STRING',1,'NONE');
insert into BltPropertyMap values ('EM-GDA-SIMPLE-TREND-OBSERVATION','slopecalculationoption','','STRING',1,'NONE');
insert into BltPropertyMap values ('EM-GDA-SIMPLE-TREND-OBSERVATION','trendcountthreshold','','INTEGER',1,'NONE');
insert into BltPropertyMap values ('EM-GDA-SIMPLE-TREND-OBSERVATION','trenddirection','','STRING',1,'NONE');
insert into BltPropertyMap values ('EM-GDA-SQC-DIAGNOSIS','label','Label','STRING',1,'NONE');
insert into BltPropertyMap values ('EM-GDA-SQC-LIMIT-OBSERVATION','dataSource','','STRING',1,'NONE');
insert into BltPropertyMap values ('EM-GDA-SQC-LIMIT-OBSERVATION','label','','STRING',1,'NONE');
insert into BltPropertyMap values ('EM-GDA-SQC-LIMIT-OBSERVATION','limitType','LimitType','STRING',1,'NONE');
insert into BltPropertyMap values ('EM-GDA-SQC-LIMIT-OBSERVATION','numberOfFilterPoints','SampleSize','INTEGER',1,'NONE');
insert into BltPropertyMap values ('EM-GDA-SQC-LIMIT-OBSERVATION','numberOfOutOfRangePointsRequired','MinimumOutOfRange','DOUBLE',1,'NONE');
insert into BltPropertyMap values ('EM-GDA-SQC-LIMIT-OBSERVATION','numberOfStandardDeviationsToTest','NumberOfStandardDeviations','DOUBLE',1,'NONE');
insert into BltPropertyMap values ('EM-GDA-SQC-LIMIT-OBSERVATION','optionalDesiredConfidence','','DOUBLE',1,'NONE');
insert into BltPropertyMap values ('EM-GDA-SQC-LIMIT-OBSERVATION','sqcTestLabel','TestLabel','STRING',1,'NONE');
insert into BltPropertyMap values ('EM-GDA-SubDIAGNOSIS','label','Label','STRING',1,'NONE');
insert into BltPropertyMap values ('EM-GDA-SYMBOLIC-VARIABLE','LABEL','','STRING',1,'NONE');
insert into BltPropertyMap values ('EM-GDA-TEST-POINT','label','','STRING',1,'NONE');
insert into BltPropertyMap values ('GDL-AND-GATE','logic','','STRING',1,'NONE');
insert into BltPropertyMap values ('GDL-AND-GATE','maximumUnknownInputs','','INTEGER',1,'NONE');
insert into BltPropertyMap values ('GDL-AND-GATE','outputUncertainty','','DOUBLE',1,'NONE');
insert into BltPropertyMap values ('GDL-AND-GATE','useExpiredInputs','','STRING',1,'NONE');
insert into BltPropertyMap values ('GDL-ARITHMETIC-FUNCTION','arithmeticFunction','Function','BOOLEAN',1,'NONE');
insert into BltPropertyMap values ('GDL-CONCLUSION','statusOnInitialization','','BOOLEAN',1,'NONE');
insert into BltPropertyMap values ('GDL-EQUALITY-OBSERVATION','equivalenceBand','Deadband','DOUBLE',1,'NONE');
insert into BltPropertyMap values ('GDL-EQUALITY-OBSERVATION','hysteresisWhen','','DOUBLE',1,'NONE');
insert into BltPropertyMap values ('GDL-EQUALITY-OBSERVATION','referenceValue','Nominal','DOUBLE',1,'NONE');
insert into BltPropertyMap values ('GDL-EQUALITY-OBSERVATION','outputUncertainty','','DOUBLE',1,'NONE');
insert into BltPropertyMap values ('GDL-GENERIC-ACTION','userDefinedProcedure','Script','BOOLEAN',1,'NONE');
insert into BltPropertyMap values ('GDL-HIGH-VALUE-OBSERVATION','hysteresisWhen','','DOUBLE',1,'NONE');
insert into BltPropertyMap values ('GDL-HIGH-VALUE-OBSERVATION','outputUncertainty','','DOUBLE',1,'NONE');
insert into BltPropertyMap values ('GDL-HIGH-VALUE-OBSERVATION','statusOnInitialization','','STRING',1,'NONE');
insert into BltPropertyMap values ('GDL-HIGH-VALUE-OBSERVATION','threshold','Limit','DOUBLE',1,'NONE');
insert into BltPropertyMap values ('GDL-HIGH-VALUE-OBSERVATION','thresholdUncertainty','Deadband','DOUBLE',1,'NONE');
insert into BltPropertyMap values ('GDL-IN-RANGE-OBSERVATION','lowerThreshold','LowerLimit','DOUBLE',1,'NONE');
insert into BltPropertyMap values ('GDL-IN-RANGE-OBSERVATION','lowerThresholdUncertainty','LowerDeadband','DOUBLE',1,'NONE');
insert into BltPropertyMap values ('GDL-IN-RANGE-OBSERVATION','upperThreshold','UpperLimit','DOUBLE',1,'NONE');
insert into BltPropertyMap values ('GDL-IN-RANGE-OBSERVATION','upperThresholdUncertainty','UpperDeadband','DOUBLE',1,'NONE');
insert into BltPropertyMap values ('GDL-IN-RANGE-OBSERVATION','hysteresisWhen','','DOUBLE',1,'NONE');
insert into BltPropertyMap values ('GDL-IN-RANGE-OBSERVATION','statusOnInitialization','','DOUBLE',1,'NONE');
insert into BltPropertyMap values ('GDL-INFERENCE-DELAY','countBy','','INTEGER',1,'NONE');
insert into BltPropertyMap values ('GDL-INFERENCE-DELAY','delay','sampleDelay','INTEGER',1,'NONE');
insert into BltPropertyMap values ('GDL-INFERENCE-DELAY','displayUnits','','STRING',1,'NONE');
insert into BltPropertyMap values ('GDL-INFERENCE-EVENT','holdFor','HoldInterval','INTEGER',1,'NONE');
insert into BltPropertyMap values ('GDL-INFERENCE-EVENT','hysteresisWhen','','STRING',1,'NONE');
insert into BltPropertyMap values ('GDL-INFERENCE-EVENT','outputUncertainty','','STRING',1,'NONE');
insert into BltPropertyMap values ('GDL-INFERENCE-EVENT','triggerOn','Trigger','STRING',1,'NONE');
insert into BltPropertyMap values ('GDL-LOW-VALUE-OBSERVATION','hysteresisWhen','','DOUBLE',1,'NONE');
insert into BltPropertyMap values ('GDL-LOW-VALUE-OBSERVATION','outputUncertainty','','DOUBLE',1,'NONE');
insert into BltPropertyMap values ('GDL-LOW-VALUE-OBSERVATION','statusOnInitialization','','STRING',1,'NONE');
insert into BltPropertyMap values ('GDL-LOW-VALUE-OBSERVATION','threshold','Limit','DOUBLE',1,'NONE');
insert into BltPropertyMap values ('GDL-LOW-VALUE-OBSERVATION','thresholdUncertainty','Deadband','DOUBLE',1,'NONE');
insert into BltPropertyMap values ('GDL-MOVING-AVERAGE','sampleSize','SampleSize','INTEGER',1,'NONE');
insert into BltPropertyMap values ('GDL-MOVING-AVERAGE','sampleType','','STRING',1,'NONE');
insert into BltPropertyMap values ('GDL-MOVING-AVERAGE','updateType','','STRING',1,'NONE');
insert into BltPropertyMap values ('GDL-MOVING-AVERAGE','updateSize','','STRING',1,'NONE');
insert into BltPropertyMap values ('GDL-MOVING-AVERAGE','eraseHistoryWhenReset','ClearOnReset','BOOLEAN',1,'NONE');
insert into BltPropertyMap values ('GDL-MOVING-AVERAGE','requireFullHistory','','BOOLEAN',1,'NONE');
insert into BltPropertyMap values ('GDL-MOVING-AVERAGE','valueOnInitialization','','STRING',1,'NONE');
insert into BltPropertyMap values ('GDL-NUMERIC-ENTRY-POINT','nameOfSensor','TagPath','STRING',1,'TAG_READ');
insert into BltPropertyMap values ('GDL-NOT-GATE','logic','','STRING',1,'NONE');
insert into BltPropertyMap values ('GDL-NOT-GATE','outputUncertainty','','DOUBLE',1,'NONE');
insert into BltPropertyMap values ('GDL-PERSISTENCE-GATE','countBy','','INTEGER',1,'NONE');
insert into BltPropertyMap values ('GDL-PERSISTENCE-GATE','delay','TimeWindow','DOUBLE',1,'NONE');
insert into BltPropertyMap values ('GDL-PERSISTENCE-GATE','displayUnits','','STRING',1,'NONE');
insert into BltPropertyMap values ('GDL-PERSISTENCE-GATE','triggerOn','Trigger','BOOLEAN',1,'NONE');
insert into BltPropertyMap values ('GDL-OR-GATE','logic','','STRING',1,'NONE');
insert into BltPropertyMap values ('GDL-OR-GATE','maximumUnknownInputs','','INTEGER',1,'NONE');
insert into BltPropertyMap values ('GDL-OR-GATE','outputUncertainty','','DOUBLE',1,'NONE');
insert into BltPropertyMap values ('GDL-OR-GATE','useExpiredInputs','','STRING',1,'NONE');
insert into BltPropertyMap values ('GDL-SYMBOLIC-ENTRY-POINT','nameOfSensor','TagPath','STRING',1,'TAG_READ');
insert into BltPropertyMap values ('GDL-TIMER','countBy','','STRING',1,'NONE');
insert into BltPropertyMap values ('GDL-TIMER','displayUnits','','STRING',1,'NONE');
insert into BltPropertyMap values ('GDL-TIMER','stopwhen','','STRING',1,'NONE');
insert into BltPropertyMap values ('GDL-TIMER','triggeron','','STRING',1,'NONE');
insert into BltPropertyMap values ('INTEGER-PARAMETER','label','','STRING',1,'NONE');
insert into BltPropertyMap values ('LOGICAL-PARAMETER','label','','STRING',1,'NONE');
insert into BltPropertyMap values ('LOGICAL-VARIABLE','label','','STRING',1,'NONE');
insert into BltPropertyMap values ('SYMBOLIC-PARAMETER','label','','STRING',1,'NONE');