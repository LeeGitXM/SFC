insert into ClassMap values ('EM-GDA-ABSOLUTE-COMPARE','com.ils.block.CompareAbsolute');
insert into ClassMap values ('EM-GDA-BAD-DATA-HANDLER','com.ils.block.DataConditioner');
insert into ClassMap values ('EM-GDA-CLOCK-TIMER','com.ils.block.TruthValuePulse');
insert intClassMap values ('EM-GDA-COMPARE','com.ils.block.Compare');
insert into ClassMap values ('EM-GDA-DEADBAND-COMPARE','com.ils.block.CompareDeadband');
insert into ClassMap values ('EM-GDA-ELAPSED-TIME-VARIABLE','com.ils.block.Inhibitor');
insert into ClassMap values ('EM-GDA-EXPIRATION-FILTER','com.ils.block.Junction');
insert into ClassMap values ('EM-GDA-FINAL-DIAGNOSIS','xom.block.finaldiagnosis.FinalDiagnosis');
insert into ClassMap values ('EM-GDA-HIGH-LIMIT','com.ils.block.HighLimitObservation');
insert into ClassMap values ('EM-GDA-HIGH-LIMIT-WITH-DEADBAND','com.ils.block.HighLimitObservation');
insert into ClassMap values ('EM-GDA-LATCH','com.ils.block.LogicLatch');
insert into ClassMap values ('EM-GDA-LOGIC-FILTER','com.ils.block.LogicFilter');
insert into ClassMap values ('EM-GDA-LOGICAL-VARIABLE','com.ils.block.Parameter');
insert into ClassMap values ('EM-GDA-LOW-LIMIT','com.ils.block.LowLimitObservation');
insert into ClassMap values ('EM-GDA-LOW-LIMIT-WITH-DEADBAND','com.ils.block.LowLimitObservation');
insert into ClassMap values ('EM-GDA-SET-VAR-FALSE','com.ils.block.Junction');
insert into ClassMap values ('EM-GDA-SIMPLE-TREND-OBSERVATION','com.ils.block.TrendDetector');
insert into ClassMap values ('EM-GDA-SQC-DIAGNOSIS','xom.block.sqcdiagnosis.SQCDiagnosis');
insert into ClassMap values ('EM-GDA-SQC-LIMIT-OBSERVATION','com.ils.block.SQC');
insert into ClassMap values ('EM-GDA-SUBDIAGNOSIS','xom.block.subdiagnosis.SubDiagnosis');
insert into ClassMap values ('EM-GDA-SYMBOLIC-VARIABLE','com.ils.block.Parameter');
insert into ClassMap values ('EM-GDA-TEST-POINT','com.ils.block.Junction');
insert into ClassMap values ('GDL-AND-GATE','com.ils.block.And');
insert into ClassMap values ('GDL-ARITHMETIC-FUNCTION','xom.block.arithmetic.Arithmetic');
insert into ClassMap values ('GDL-BLOCK-EVALUATION','com.ils.block.Command');
insert into ClassMap values ('GDL-BLOCK-RESET','com.ils.block.Reset');
insert into ClassMap values ('GDL-CONCLUSION','com.ils.block.Junction');
insert into ClassMap values ('GDL-D.D-DISPLAY','com.ils.block.Readout');
insert into ClassMap values ('GDL-DATA-PATH-DISPLAY','com.ils.block.Readout');
insert into ClassMap values ('GDL-DATA-SHIFT','com.ils.block.DataShift');
insert into ClassMap values ('GDL-DATA-TIME-STAMP','com.ils.block.TimeFork');
insert into ClassMap values ('GDL-DIFFERENCE','com.ils.block.Difference');
insert into ClassMap values ('GDL-ENCAPSULATION','com.ils.block.Encapsulation');
insert into ClassMap values ('GDL-ENCAPSULATION-BLOCK','com.ils.block.Encapsulation');
insert into ClassMap values ('GDL-EQUALITY-OBSERVATION','com.ils.block.EqualityObservation');
insert into ClassMap values ('GDL-INFERENCE-DELAY','com.ils.block.Delay');
insert into ClassMap values ('GDL-INFERENCE-EVENT','com.ils.block.EdgeTrigger');
insert into ClassMap values ('GDL-GENERIC-ACTION','xom.block.action.Action');
insert into ClassMap values ('GDL-HIGH-VALUE-OBSERVATION','com.ils.block.HighLimitObservation');
insert into ClassMap values ('GDL-IN-RANGE-OBSERVATION','com.ils.block.RangeObservation');
insert into ClassMap values ('GDL-LOW-VALUE-OBSERVATION','com.ils.block.LowLimitObservation');
insert into ClassMap values ('GDL-MOVING-AVERAGE','com.ils.block.MovingAverageSample');
insert into ClassMap values ('GDL-NOT-GATE','com.ils.block.Not');
insert into ClassMap values ('GDL-NUMERIC-ENTRY-POINT','com.ils.block.Input');
insert into ClassMap values ('GDL-OR-GATE','com.ils.block.Or');
insert into ClassMap values ('GDL-PERSISTENCE-GATE','com.ils.block.PersistenceGate');
insert into ClassMap values ('GDL-SYMBOLIC-ENTRY-POINT','com.ils.block.Input');
insert into ClassMap values ('GDL-TIMER','com.ils.block.Timer');
insert into ClassMap values ('INTEGER-PARAMETER','com.ils.block.Parameter');
insert into ClassMap values ('LOGICAL-PARAMETER','com.ils.block.Parameter');
insert into ClassMap values ('LOGICAL-VARIABLE','com.ils.block.Parameter');
insert into ClassMap values ('SYMBOLIC-PARAMETER','com.ils.block.Parameter');
-- NOTE: We convert all connnection posts to "sinks"
--       later on we analyze and determine which are "sources"
insert into ClassMap values ('GDL-INFERENCE-PATH-CONNECTION-POST','com.ils.block.SinkConnection');
insert into ClassMap values ('GDL-DATA-PATH-CONNECTION-POST','com.ils.block.SinkConnection');
-- For calculation procedures
insert into ClassMap values('em-application','com.ils.blt.gateway.engine.ProcessApplication');
insert into ClassMap values('em-diagnosis-family','com.ils.blt.gateway.engine.ProcessFamily');
insert into ClassMap values('em-gda-final-diagnosis','com.ils.block.finaldiagnosis');
insert into ClassMap values('em-s88-rate-change-callback','java.util.Map');
insert into ClassMap values('em-s88-rate-change-current-data-callback','java.util.Map');
-- These are generic classes used for SFC translation
insert into ClassMap values ('chart','chart');
insert into ClassMap values ('error','java.util.Map');
insert into ClassMap values ('g2Variable','java.util.Map');
insert into ClassMap values ('float-variable','java.lang.Double');
insert into ClassMap values ('kb-workspace','java.util.List');
insert into ClassMap values ('procedure','java.util.Map');
insert into ClassMap values ('quantity-array','java.util.List');
insert into ClassMap values ('opc-text-conditional-text-output','java.util.Map');
insert into ClassMap values ('s88-get-input-task','java.util.Map');
insert into ClassMap values ('s88-review-data-with-advice-task','java.util.Map');
insert into ClassMap values ('s88-poly-rate-chg-display','java.util.Map');
insert into ClassMap values ('s88-pv-monitoring-task','java.util.Map');
insert into ClassMap values ('s88-recipe-entity','step');
insert into ClassMap values ('s88-recipe-output-data','com.ils.sfc.commmon.recipe.objects.Data');
insert into ClassMap values ('s88-recipe-value-data','com.ils.sfc.commmon.recipe.objects.Data');
insert into ClassMap values ('s88-review-data-task','java.util.Map');
insert into ClassMap values ('s88-termination-transition','java.util.Map');
insert into ClassMap values ('s88-time-delay','java.util.Map');
insert into ClassMap values ('s88-write-outputs-task','java.util.Map');
insert into ClassMap values ('sequence','java.util.List');
insert into ClassMap values ('structure','java.util.Map');
insert into ClassMap values ('uir-data-entry-for-vistalon','java.util.Map');
insert into ClassMap values ('value-array','java.util.List');
