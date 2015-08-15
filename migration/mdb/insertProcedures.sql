-- Map G2 procedures into a python module.
-- Synthesized
-- Most are names of procedures found as properties during the migration 
-- A return type of "dictionary" has special meaning to the procedure translator.


INSERT into ProcedureMap(G2Procedure,IgnitionProcedure) SELECT G2Value,IgnitionValue FROM PropertyValueMap WHERE Property='callback';

insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('IRTLQ-VIEW','ils.??');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('_em-s88-test-simulate-production','com.ils.sfc.python.simulateProduction.test');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('_s88-abort','com.ils.sfc.python.S88State.abort');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('_s88-post-message-to-queue','com.ils.queue.post.messageToQueue');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('average','com.ils.blt.functions.average');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('build-message-on-workspace','ils.blt.lib.noopint');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('bypass-output-limits','ils.blt.lib.bypassOutputLimits');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('calc-baler-temp-problem-output-gda','ils.vistalon.vfu.BalerTemperatureOutput.calculate');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('calc-baler-vol-output-gda','ils.vistalon.fd.BalerVolume.calculate');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('calc-baler-vol_problem-output-gda','ils.vistalon.vfu.BalerVolumeOutput.calculate');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('calc-c2_problem-output-gda','ils.vistalon.fd.C2.calculate');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('calc-crx_ca_problem-output-gda','project.vistalon.crx.CrxCA.calculate');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('calc-crx_stab_problem-output-gda','project.vistalon.crx.CrxStab.calculate');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('calc-cstr_c2_problem-output-gda','project.vistalon.cstr.CstrC2.calculate');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('calc-cstr_ca_problem-output-gda','project.vistalon.cstr.CstrCa.calculate');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('calc-cstr_dc2_problem-output-gda','project.vistalon.cstr.CstrDc2.calculate');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('calc-cstr_denb_problem-output-gda','project.vistalon.cstr.CstrDenb.calculate');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('calc-cstr_dml-problem-gda','project.vistalon.cstr.CstrDML.calculate');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('calc-cstr_dml_problem-output-gda','project.vistalon.cstr.CstrDML.calculate');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('calc-cstr_enb_problem-output-gda','project.vistalon.cstr.CstrEnb.calculate');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('calc-cstr_ml_problem-output-gda','project.vistalon.cstr.CstrMl.calculate');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('calc-cstr_mlr_problem-output-gda','project.vistalon.cstr.CstrMlr.calculate');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('calc-cstr_oil_problem-output-gda','project.vistalon.cstr.CstrOil.calculate');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('calc-cstr_polysplit-output-gda','project.vistalon.cstr.CstrPolysplit.calculate');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('calc-cstr_polysplit_problem-output-gda','project.vistalon.cstr.CstrPolysplitProblem.calculate');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('calc-cstr_stab_problem-output-gda','project.vistalon.cstr.CstrStab.calculate');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('calc-do_cat-outputs','project.vistalon.crx.CatOutputs.calculate');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('calc-do_cat-outputs-gda','project.vistalon.crx.CatOutputs.calculate');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('calc-do_gravity-outputs','project.vistalon.crx.GravityOutputs.calculate');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('calc-do_temp-outputs','project.vistalon.crx.TempOutputs.calculate');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('calc-do_temp-outputs-gda','xom.vistalon.fd.TempOutputs.calculate');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('calc-frnt_error_chng_feeds-outputs-gda','project.vistalon.crx.FrontChangeFeedOutputs.calculate');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('calc-frnt_short_temp_inhib-outputs-gda','project.vistalon.crx.FrontShortTempInhibitOutputs.calculate');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('calc-frnt_short_use_temp-outputs-gda','project.vistalon.crx.FrontShortUseTempOutputs.calculate');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('calc-high_c2_chng_feeds-outputs-gda','project.vistalon.crx.C2ChangeFeeds.calculate');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('calc-main_feed-outputs-gda','project.vistalon.crx.MainFeed.calculate');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('calc-no-output-gda','ils.vistalon.fd.NoOutput.calculate');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('calc-reslurry-problem-output-gda','ils.vistalon.fd.ReslurryOutput.calculate');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('calc-sdstrm_c3-outputs-gda','xom.vistalon.crx.SdstrmC3.calculate');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('calc-swap_cntr_feeds-outputs-gda','project.vistalon.crx.SwapCenterFeedsOutputs.calculate');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('calc-swap_frnt_feeds-outputs-gda','project.vistalon.crx.SwapFrontFeedsOutputs.calculate');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('change-attribute','__setattr__');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('change-element','ils.blt.lib.changeArrayElement');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('clear-quantity-list-gda','ils.blt.lib.clearQuantityList');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('constant','ils.vistalon.util.constant.calculate');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('determine-log-file','ils.diagToolkit.util.getLogFile');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('determine-post','ils.diagToolkit.util.getPost');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('diag-snapshot','ils.diagToolkit.util.snapshot');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('do_c_flying_switch-gda','ils.vistalon.fd.CFlyingSwitch.calculate');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('do_cat_outputs-gda','ils.vistalon.fd.CatOutputs.calculate');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('do_rate_chng-gda','ils.vistalon.fd.RateChange.calculate');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('do_series_flying_switch-gda','ils.vistalon.fd.SeriesFlyingSwitch.calculate');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('do_series_rate_chg-gda','ils.vistalon.fd.SeriesRateChange.calculate');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('do_single_flying_switch-gda','ils.vistalon.fd.SingleFlyingSwitch.calculate');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('do_single_rate_chg-gda','ils.vistalon.fd.SingleRateChange.calculate');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('do_split_flying_switch-gda','ils.vistalon.fd.SplitFlyingSwitch.calculate');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('do_split_rate_chg-gda','ils.vistalon.fd.SplitRateChange.calculate');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('do_temp-gda','ils.vistalon.crx.TempOutputs.calculate');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('dr-top-temp-monitor','drTopTempMonitor.onstop');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('em-check-sqc-fs','ils.diagToolkit.finalDiagnosis.checkSQC');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('em-check-integer-data-fs','ils.diagToolkit.finalDiagnosis.checkInt');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('em-check-float-data-fs','ils.diagToolkit.finalDiagnosis.checkFloat');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('em-check-truth-data-fs','ils.diagToolkit.finalDiagnosis.checkTruth');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('em-convert-value-to-text-or-quant','ils.diagToolkit.util.valueToText');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('em-create-message','ils.queue.message.insert');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('em-get-application','ils.diagToolkit.finalDiagnosis.getApplication');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('em-get-console','ils.diagToolkit.finalDiagnosis.getConsole');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('em-get-grade-value','xom.vistalon.util.getGrade');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('em-get-input-blocks','ils.diagToolkit.sqcDiagnosis.getInputBlocks');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('em-get-quant-recommendation','ils.diagToolkit.recommendation.calculateFinalRecommendation');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('em-get-target','ils.diagToolkit.finalDiagnosis.getUpstreamSQCTargetValue');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('em-make-sqc-limit-table','ils.diagToolkit.sqc.makeLimitTable');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('em-s88-calc-monomer-flows','com.ils.sfc.python.monomerFlows.calculate');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('em-s88-c-rx-calc-dil-flows','com.ils.sfc.python.DilFlows.calculate');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('em-s88-calc-delay-drivers-cold-stick','com.ils.sfc.python.DelayDriversColdStick.calculate');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('em-s88-get-rate-change-data-block','ils.sfc.gateway.api.s88GetRateChangeBlock');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('em-s88-mid-run-catout-uir','com.ils.sfc.python.uir.midRunCatout');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('em-s88-move-cold-stick-data-from-the-model-screen','com.ils.sfc.python.coldStickData.moveFromScreen');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('em-s88-set-recipe-data-cold-stick','ils.sfc.gateway.api.s88SetColdstick');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('em-s88-set-targets-cold-stick','ils.sfc.gateway.api.s88SetColdstickTargets');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('em-s88-store-general-setpoints-cold-stick','com.ils.sfc.python.GeneralSetpointsColdStick.store');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('em-set-catout-delays','com.ils.sfc.python.catoutDelays.set');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('em-set-crossover-time','com.ils.sfc.python.crossoverTime.set');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('em-start-clock-timer','ils.diagToolkit.timer.start');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('em-write-file','com.ils.sfc.python.File.write');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('freezer-sample-problem-reset','ils.diagToolkit.crx.freezer.reset');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('fs-r1-c2-conv-vs-pr','com.ils.sfc.python.block.fsR1C2ConvVsPr');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('fs-r1-c3-conv-vs-pr','com.ils.sfc.python.block.fsR1C3ConvVsPr');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('fs-r1-c9-conv-vs-pr','com.ils.sfc.python.block.fsR1C9ConvVsPr');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('fs-r1-cat-eff-vs-pr','com.ils.sfc.python.block.fsR1CatEffVsPr');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('fs-r1-h2-eff-vs-pr','com.ils.sfc.python.block.fsR1H2EffVsPr');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('fs-r2-c2-conv-vs-pr','com.ils.sfc.python.block.fsR2C2ConvVsPr');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('fs-r2-c3-conv-vs-pr','com.ils.sfc.python.block.fsR2C3ConvVsPr');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('fs-r2-c9-conv-vs-pr','com.ils.sfc.python.block.fsR2C9ConvVsPr');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('fs-r2-h2-eff-vs-pr-2','com.ils.sfc.python.block.fsRr21H2EffVsPri2');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('fs-r2-nh3-eff-vs-pr-2','com.ils.sfc.python.block.fsR1Nh3EffVsPr2');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('g2-array-min','com.ils.util.Array.minimum');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('g2-array-subtract','com.ils.util.Array.subtract');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('g2-get-port-number-or-name','ils.vistalon.util.Component.getPortName');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('g2-scalar-multiply','com.ils.util.Scalar.multiply');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('g2-tw-popup','ils.vistalon.util.Window.displayClient');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('getblockName','ils.blt.lib.getBlockName');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('getengineDebugMode','ils.blt.lib.getDebugMode');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('getengineName','ils.blt.lib.getName');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('get_list_datum-gda','ils.blt.lib.getListDatum');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('getConnected','ils.blt.lib.getConnected');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('getParent','ils.blt.lib.getParent');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('ils-collect-inferior-instances','ils.vistalon.util.getInferiorInstances');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('ils-get-window','ils.blt.lib.getWindow');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('ils-get-g2-name','ils.vistalon.util.getG2Name');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('ils-insert-into-sorted-sequence','ils.vistalon.util.insertIntoList');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('ils-inform-the-operator','com.ils.queue.post.inform');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('ils-update-structure-in-a-keyed-sequence','ils.vistalon.util.updateStructureInKeyedSequence');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('insert-at-end','append');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('instancesOfClass','com.ils.function.find');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('lab-baler-sqc-problem-start-uir','ils.vistalon.crx.uir.startLabBalerSqc');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('named-item-exists','ils.blt.lib.namedItemExists');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('not-specified','ils.vistalon.util.NoOutput.calculate');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('oc-alert-start-exe','ils.vistalon.alert.start');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('oc-alert-message','ils.queue.message.alert');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('onStart','onStart');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('output-msg','ils.diagToolkit.util.outputMessage.create');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('output-msg-core','ils.diagToolkit.util.outputMessageCore.create');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('post-error','ils.vistalon.fd.ProductMooney.calculate');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('print-text-file','com.ils.sfc.python.File.print');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('prod-g2-port','com.ils.sfc.python.Product.getPort');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('prod-mooney-gda','ils.vistalon.fd.ProductMooney.calculate');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('recalc-cat-sps-gda','ils.vistalon.fd.CatSps.recalculate');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('reset_fd-gda','ils.diagToolkit.fd.reset');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('rx-type','ils.sfc.python.rx.getType');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('sequence','');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('set_output-gda','ils.diagToolkit.util.setOutput');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('set-rx-nlc-problem','ils.diagToolkit.util.setRxNlcProblem');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('s88-catout-data-collector-c-reactor-ss','ils.sfc.python.s88CatoutCReactor.collect');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('s88-copy-recipe-data','ils.sfc.gateway.api.s88CopyData');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('s88-delete-recipe-data','ils.sfc.gateway.api.s88DeleteData');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('s88-feed-enthalpy','com.ils.sfc.python.Enthalphy.feed');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('s88-feed-enthalpy-ex-c6','com.ils.sfc.python.EnthalphyExC6.feed');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('s88-get','com.ils.sfc.python.ScopeData.get');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('s88-get-with-units','com.ils.sfc.python.ScopeData.getWithUnits');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('s88-get-block-in-hierarchy','ils.diagToolkit.finalDiagnosis.getBlock');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('s88-get-control-panel','ils.sfc.gateway.api.s88GetControlPanel');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('s88-get-data','ils.sfc.gateway.api.s88GetData');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('s88-get-superior-operation','com.ils.sfc.python.UnitProcedure.getSuperior');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('s88-get-unit-procedure','com.ils.sfc.python.UnitProcedure.get');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('s88-get-value','ils.sfc.gateway.api.s88GetValue');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('s88-heat','com.ils.sfc.python.heat.calculate');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('s88-ht-of-rx-t','com.ils.sfc.python.heatOfRxT.calculate');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('s88-post-error','ils.diagToolkit.finalDiagnosis.postError');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('s88-post-message','com.ils.queue.post.messageToQueue');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('s88-post-message-to-queue','com.ils.queue.post.messageToQueue');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('s88-set','com.ils.sfc.python.ScopeData.set');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('s88-set-data','ils.sfc.gateway.api.s88SetData');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('s88-set-data-with-units','ils.sfc.gateway.api.s88SetDataWithUnits');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('s88-sum-array','com.ils.sfc.python.Array.sum');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('s88-transfer','com.ils.sfc.python.util.transfer');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('s88-update-recipe-data','ils.sfc.gateway.api.s88UpdateData');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('s88-update-recipe-data-val','ils.sfc.gateway.api.s88UpdateValue');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('taper_list_build-gda','ils.blt.lib.buildTaperList');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('uil-post-delay-notification','ils.diagToolkit.util.outputMessage.create');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('unset-value','ils.vistalon.util.UnsetValue.calculate');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('write-recipe-data-to-file','ils.sfc.gateway.api.s88WriteData');
insert into ProcedureMap(G2Procedure,IgnitionProcedure,ReturnType) values ('em-get-quant-recommendation-def','ils.diagToolkit.recommendation.defineQuantOutput','dictionary');
-- Built-into Python
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('round-to-x-places','ils.vistalon.util.round');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('max','max');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('min','min');
insert into ProcedureMap(G2Procedure,IgnitionProcedure) values ('upper-case-text','toUpper');
