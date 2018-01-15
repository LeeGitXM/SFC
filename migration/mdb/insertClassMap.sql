-- G2Class, IgnitionClass
insert into ClassMap values ('EM-GDA-ABSOLUTE-COMPARE','com.ils.block.CompareAbsolute');
insert into ClassMap values ('EM-GDA-BAD-DATA-HANDLER','com.ils.block.DataConditioner');
insert into ClassMap values ('EM-GDA-CLOCK-TIMER','com.ils.block.TruthValuePulse');
insert into ClassMap values ('EM-GDA-COMPARE','com.ils.block.Compare');
insert into ClassMap values ('EM-GDA-DEADBAND-COMPARE','com.ils.block.CompareDeadband');
insert into ClassMap values ('EM-GDA-ELAPSED-TIME-VARIABLE','com.ils.block.Inhibitor');
insert into ClassMap values ('EM-GDA-EXPIRATION-FILTER','com.ils.block.Junction');
insert into ClassMap values ('EM-GDA-FINAL-DIAGNOSIS','xom.block.finaldiagnosis.FinalDiagnosis');
insert into ClassMap values ('EM-GDA-HIGH-LIMIT','com.ils.block.HighLimitObservation');
insert into ClassMap values ('EM-GDA-HIGH-LIMIT-WITH-DEADBAND','com.ils.block.HighLimitObservation');
insert into ClassMap values ('EM-GDA-LATCH','com.ils.block.LogicLatch');
insert into ClassMap values ('EM-GDA-LOGIC-FILTER','com.ils.block.LogicFilter');
insert into ClassMap values ('EM-GDA-LOGICAL-PARAMETER','com.ils.block.Parameter');
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
insert into ClassMap values ('GDL-BELIEF-ENTRY-POINT','com.ils.block.Input');
insert into ClassMap values ('GDL-BLOCK-EVALUATION','com.ils.block.Command');
insert into ClassMap values ('GDL-BLOCK-RESET','com.ils.block.Reset');
insert into ClassMap values ('GDL-CHANGE-SIGN','com.ils.block.ChangeSign');
insert into ClassMap values ('GDL-CONCLUSION','com.ils.block.Explanation');
insert into ClassMap values ('GDL-CONTROL-COUNTER','com.ils.block.ControlCounter');
insert into ClassMap values ('GDL-CONTROL-DELAY','com.ils.block.Delay');
insert into ClassMap values ('GDL-D.D-DISPLAY','com.ils.block.Readout');
insert into ClassMap values ('GDL-DATA-PATH-DISPLAY','com.ils.block.Readout');
insert into ClassMap values ('GDL-DATA-SELECT','com.ils.block.DataSelector');
insert into ClassMap values ('GDL-DATA-SHIFT','com.ils.block.DataShift');
insert into ClassMap values ('GDL-DATA-TIME-STAMP','com.ils.block.TimeFork');
insert into ClassMap values ('GDL-DISCRETE-RATE-OF-CHANGE','com.ils.block.DiscreteRateOfChange');
insert into ClassMap values ('GDL-DIFFERENCE','com.ils.block.Difference');
insert into ClassMap values ('GDL-ENCAPSULATION','com.ils.block.Encapsulation');
insert into ClassMap values ('GDL-ENCAPSULATION-BLOCK','com.ils.block.Encapsulation');
insert into ClassMap values ('GDL-EQUALITY-OBSERVATION','com.ils.block.EqualityObservation');
insert into ClassMap values ('GDL-FIXED-BIAS','com.ils.block.Bias');
insert into ClassMap values ('GDL-FIXED-GAIN','com.ils.block.Gain');
insert into ClassMap values ('GDL-HIGH-SELECTING','com.ils.block.HighSelector');
insert into ClassMap values ('GDL-HIGH-VALUE-PATTERN','com.ils.block.HighValuePattern');
insert into ClassMap values ('GDL-INFERENCE-COUNTER','com.ils.block.TruthCycleCounter');
insert into ClassMap values ('GDL-INFERENCE-DELAY','com.ils.block.Delay');
insert into ClassMap values ('GDL-INFERENCE-EVENT','com.ils.block.EdgeTrigger');
insert into ClassMap values ('GDL-INFERENCE-INHIBIT','com.ils.block.InferenceInhibitor');
insert into ClassMap values ('GDL-INFERENCE-MEMORY','com.ils.block.InferenceMemory');
insert into ClassMap values ('GDL-INFERENCE-OUTPUT-ACTION','xom.block.action.Action');
insert into ClassMap values ('GDL-INFERENCE-TIME-STAMP','com.ils.block.TimeFork');
insert into ClassMap values ('GDL-GENERIC-ACTION','xom.block.action.Action');
insert into ClassMap values ('GDL-HIGH-VALUE-OBSERVATION','com.ils.block.HighLimitObservation');
insert into ClassMap values ('GDL-IN-RANGE-OBSERVATION','com.ils.block.RangeObservation');
insert into ClassMap values ('GDL-LINEAR-FIT','com.ils.block.LinearFit');
insert into ClassMap values ('GDL-LOW-LIMITING','com.ils.block.LowLimit');
insert into ClassMap values ('GDL-LOW-VALUE-OBSERVATION','com.ils.block.LowLimitObservation');
insert into ClassMap values ('GDL-LOW-VALUE-PATTERN','com.ils.block.LowValuePattern');
insert into ClassMap values ('GDL-MOVING-AVERAGE','com.ils.block.MovingAverageSample');
insert into ClassMap values ('GDL-MULTI-STATE-OBSERVATION','com.ils.block.StateLookup');
insert into ClassMap values ('GDL-NOT-GATE','com.ils.block.Not');
insert into ClassMap values ('GDL-N-TRUE-GATE','com.ils.block.NTrue');
insert into ClassMap values ('GDL-NUMERIC-ENTRY-POINT','com.ils.block.Input');
insert into ClassMap values ('GDL-OR-GATE','com.ils.block.Or');
insert into ClassMap values ('GDL-OUT-OF-RANGE-OBSERVATION','com.ils.block.OutOfRangeObservation');
insert into ClassMap values ('GDL-PERSISTENCE-GATE','com.ils.block.PersistenceGate');
insert into ClassMap values ('GDL-QUOTIENT','com.ils.block.Quotient');
insert into ClassMap values ('GDL-SET-ATTRIBUTE','com.ils.block.SetAttribute');
insert into ClassMap values ('GDL-SUMMING','com.ils.block.Sum');
insert into ClassMap values ('GDL-SYMBOLIC-ENTRY-POINT','com.ils.block.Input');
insert into ClassMap values ('GDL-TEXT-ENTRY-POINT','com.ils.block.Input');
insert into ClassMap values ('GDL-TIMER','com.ils.block.Timer');
insert into ClassMap values ('GDL-TRUE-IF-UNKNOWN-GATE','com.ils.block.Unknown');
insert into ClassMap values ('GDL-TWO-OPTION-USER-QUERY-CONTROL-SWITCH','com.ils.block.DualOptionQueryControl');
insert into ClassMap values ('GDL-THREE-OPTION-CONTROL-SWITCH','com.ils.block.TripleOptionQueryControl');
insert into ClassMap values ('OPC-FLOAT-BAD-FLAG','com.ils.block.Parameter');
insert into ClassMap values ('OPC-FLOAT-OUTPUT','com.ils.block.Output');
insert into ClassMap values ('OPC-INT-BAD-FLAG','com.ils.block.Parameter');
insert into ClassMap values ('OPC-INT-OUTPUT','com.ils.block.Output');
insert into ClassMap values ('OPC-PKS-ACE-CONTROLLER','com.ils.block.Output');
insert into ClassMap values ('OPC-PKS-ACE-RAMP-CONTROLLER','com.ils.block.Output');
insert into ClassMap values ('OPC-PKS-EHG-CONTROLLER','com.ils.block.Output');
-- Variables and Parameters
insert into ClassMap values ('EM-GDA-SYMBOLIC-PARAMETER','com.ils.block.Parameter');
insert into ClassMap values ('FLOAT-PARAMETER','com.ils.block.Parameter');
insert into ClassMap values ('FLOAT-VARIABLE','com.ils.block.Parameter');
insert into ClassMap values ('INTEGER-PARAMETER','com.ils.block.Parameter');
insert into ClassMap values ('LOGICAL-PARAMETER','com.ils.block.Parameter');
insert into ClassMap values ('LOGICAL-VARIABLE','com.ils.block.Parameter');
insert into ClassMap values ('OC-MESSAGE-PARAMETER','com.ils.block.Parameter');
insert into ClassMap values ('SYMBOLIC-PARAMETER','com.ils.block.Parameter');
insert into ClassMap values ('TFA-SEQ-PERM-TRUTH-CHECK','com.ils.block.Parameter');
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
-- These are for procedure translation
insert into ClassMap values ('g2Window','java.awt.Window');
-- These are generic classes used for SFC translation
insert into ClassMap values ('chart','chart');
insert into ClassMap values ('error','java.util.Map');
insert into ClassMap values ('g2Variable','java.util.Map');
insert into ClassMap values ('float-variable','java.lang.Double');
insert into ClassMap values ('kb-workspace','java.util.List');
insert into ClassMap values ('procedure','java.util.Map');
insert into ClassMap values ('float-array','java.util.List');
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

