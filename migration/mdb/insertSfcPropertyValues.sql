-- Convert values found in G2 blocks or charts into proper Ignition equivalents.
-- Fields are: Ignition property, G2value, desired value

-- GENERIC DEFAULTS
insert into PropertyValueMap values ('timeoutUnits','','SEC');

insert into PropertyValueMap values ('recipeLocation','LOCAL','local');
insert into PropertyValueMap values ('recipeLocation','PREVIOUS','prior');
insert into PropertyValueMap values ('recipeLocation','SUPERIOR','superior');
insert into PropertyValueMap values ('recipeLocation','OPERATION','operation');
insert into PropertyValueMap values ('recipeLocation','PHASE','phase');
insert into PropertyValueMap values ('recipeLocation','PROCEDURE','global');
insert into PropertyValueMap values ('recipeLocation','GLOBAL','global');
insert into PropertyValueMap values ('recipeLocation','NAMED','named');

insert into PropertyValueMap values ('strategy','local','static');   -- there is no local strategy
insert into PropertyValueMap values ('strategy','LOCAL','static');   -- there is no local strategy
insert into PropertyValueMap values ('strategy','STATIC','static');
insert into PropertyValueMap values ('strategy','RECIPE-DATA','recipe');

insert into PropertyValueMap values ('priority','INFORMATION','Info');
insert into PropertyValueMap values ('priority','WARNING','Warning');
insert into PropertyValueMap values ('priority','ERROR','Error');

-- Names of callbacks get read from here and inserted into ProcedureMap also
-- Formal test cases
insert into PropertyValueMap values ('callback','ph-test-callback-1','test1OnStart1.onStart');
insert into PropertyValueMap values ('callback','ph-callback-1'     ,'test2OnStart1.onStart');
insert into PropertyValueMap values ('callback','ph-callback-2'     ,'test2OnStart2.onStart');
insert into PropertyValueMap values ('callback','test-3-recipe-data','test3OnStartrd.onStart');
insert into PropertyValueMap values ('callback','s88-callback-template','s88OnstartTemplate.onStart');
insert into PropertyValueMap values ('callback','test-5-callback-1','test5OnStart1.onStart');
insert into PropertyValueMap values ('callback','test-5-callback-2','test5OnStart2.onStart');
insert into PropertyValueMap values ('callback','test-5-shared-callback','test5OnStart.onStart');
insert into PropertyValueMap values ('callback','test-6-callback',  'test6OnStart.onStart');
-- Vistalon
insert into PropertyValueMap values ('callback','dr-change-service','drChangeService.onStart');
insert into PropertyValueMap values ('callback','dr-cool-down-time-init','drCoolDownTimeInit.onStart');
insert into PropertyValueMap values ('callback','dr-fetch-and-set-temp-limits','drFetchAndSetTempLimits.onStart');
insert into PropertyValueMap values ('callback','dr-find-drier','drFindDrier.onStart');
insert into PropertyValueMap values ('callback','dr-gas-circulating-test','drGasCirculatingTest.onStart');
insert into PropertyValueMap values ('callback','dr-manage-ht-soak-cycles','drManageHtSoakCycles.onStart');
insert into PropertyValueMap values ('callback','dr-moisture-timer-init','drMoistureTimerInit.onStart');
insert into PropertyValueMap values ('callback','dr-regen-alert-operator','drRegenAlertOperator.onStart');
insert into PropertyValueMap values ('callback','dr-regen-duration-calc','drRegenDurationCalc.onStart');
insert into PropertyValueMap values ('callback','dr-termination-monitor','drTerminationMonitor.onStart');
insert into PropertyValueMap values ('callback','dr-time-monitor','drTimeMonitorc.onStart');
insert into PropertyValueMap values ('callback','dr-top-temp-monitor-cool-down','drTopTempMonitorCoolDown.onStart');
insert into PropertyValueMap values ('callback','dr-vmr801-close-counter','drVmr801CloseCounter.onStart');
insert into PropertyValueMap values ('callback','dr-vmr801-open-counter','drVmr801OpenCounter.onStart');
insert into PropertyValueMap values ('callback','dr-vmr803-close-counter','drVmr803CloseCounter.onStart');
insert into PropertyValueMap values ('callback','dr-vmr803-open-counter','drVmr803OpenCounter.onStart');
insert into PropertyValueMap values ('callback','dr-write-logfile-msg-for-operation','drWriteLogfileMsgForOperation.onStart');
insert into PropertyValueMap values ('callback','em-c6-flow','emC6Flow.onStart');
insert into PropertyValueMap values ('callback','em-calc-max-time','emCalcMaxTime.onStart');
insert into PropertyValueMap values ('callback','em-catout-r2-delay','emCatoutR2Delay.onStart');
insert into PropertyValueMap values ('callback','em-check-e204-control-modes','emCheckE204ControlModes.onStart');
insert into PropertyValueMap values ('callback','em-coldstick-monitor-outlet-temp-bias','emColdstickMonitorOutletTempBias.onStart');
insert into PropertyValueMap values ('callback','em-configure-model-input-blocks-fs','emConfigureModelInputBlocksFs.onStart');
insert into PropertyValueMap values ('callback','em-configure-model-input-blocks','emConfigureModelInputBlocks.onStart');
insert into PropertyValueMap values ('callback','em-configure-outlet-temp-crossover-times','emConfigureOutletTempCrossoverTimes.onStart');
insert into PropertyValueMap values ('callback','em-convert-poly-rate-fs','emConvertPolyRateFs.onStart');
insert into PropertyValueMap values ('callback','em-convert-poly-rate-series-fs','emConvertPolyRateSeriesFs.onStart');
insert into PropertyValueMap values ('callback','em-copy-rate-change-data','emCopyRateChangeData.onStart');
insert into PropertyValueMap values ('callback','em-count-download-errors','emCountDownloadErrors.onStart');
insert into PropertyValueMap values ('callback','em-get-grade','emGetGrade.onStart');
insert into PropertyValueMap values ('callback','em-get-poly-rate-data','emGetPolyRateData.onStart');
insert into PropertyValueMap values ('callback','em-initialize-split-top-feed-modes','emInitializeSplitTopFeedModes.onStart');
insert into PropertyValueMap values ('callback','em-misc-cold-stick-tasks','emMiscColdStickTasks.onStart');
insert into PropertyValueMap values ('callback','em-post-running-notification','emPostRunningNotification.onStart');
insert into PropertyValueMap values ('callback','em-r1-conv-vs-p','emR1ConvVsP.onStart');
insert into PropertyValueMap values ('callback','em-r2-conv-vs-p','emR2ConvVsP.onStart');
insert into PropertyValueMap values ('callback','em-rx-temperature-calculations','emRxTemperatureCalculations.onStart');
insert into PropertyValueMap values ('callback','em-s88-accept-new-rate','emS88AcceptNewRate.onStart');
insert into PropertyValueMap values ('callback','em-s88-alert-nlc-of-cold-stick','emS88.onStart');
insert into PropertyValueMap values ('callback','em-s88-c-rx-calc-delay-drivers-cold-stick','emS88.onStart');
insert into PropertyValueMap values ('callback','em-s88-c-rx-mat-enrgy-balance-cold-stick','emS88.onStart');
insert into PropertyValueMap values ('callback','em-s88-c-rx-store-GENERAL-setpoints-cold-stick','emS88.onStart');
insert into PropertyValueMap values ('callback','em-s88-calc-additive-flows-cold-stick','emS88.onStart');
insert into PropertyValueMap values ('callback','em-s88-calc-cat-flows-cold-stick','emS88CalcCatFlowsColdStick.onStart');
insert into PropertyValueMap values ('callback','em-s88-chk-valid-grade-fs','emS88ChkValidGradeFs.onStart');
insert into PropertyValueMap values ('callback','em-s88-copy-catout-data','emS88CopyCatoutData.onStart');
insert into PropertyValueMap values ('callback','em-s88-get-rx-data','emS88GetRxData.onStart');
insert into PropertyValueMap values ('callback','em-s88-initialize-checklist','emS88InitializeChecklist.onStart');
insert into PropertyValueMap values ('callback','em-s88-initialize-crx-ss-frct','emS88InitializeCrxSsFrct.onStart');
insert into PropertyValueMap values ('callback','em-s88-initialize-new-prod-data','emS88InitializeNewProdData.onStart');
insert into PropertyValueMap values ('callback','em-s88-mod-run-catout-dir','emS88ModRunCatoutDir.onStart');
insert into PropertyValueMap values ('callback','em-s88-move-cold-stick-data','emS88MoveColdStickData.onStart');
insert into PropertyValueMap values ('callback','em-s88-rc-set-h2-nh3-ctls-config','emS88RcSetH2Nh3CtlsConfig.onStart');
insert into PropertyValueMap values ('callback','em-s88-rx-calc-timings-cold-stick','emS88RxCalcTimingsColdStick.onStart');
insert into PropertyValueMap values ('callback','em-s88-series-rx-calc-monomer-flows-cold-stick','emS88SeriesRxCalCMonomerFlowsColdStick.onStart');
insert into PropertyValueMap values ('callback','em-s88-series-rx-chk-ht-of-rx-cold-stick','emS88SeriesRxChkHtOfRxColdStick.onStart');
insert into PropertyValueMap values ('callback','em-s88-series-rx-mat-enrgy-balance-cold-stick','emS88SeriesRxMatEnrgyBalanceColdStick.onStart');
insert into PropertyValueMap values ('callback','em-s88-series-store-setpoints-fs','emS88SeriesStoreSetpointsFs.onStart');
insert into PropertyValueMap values ('callback','em-s88-set-calcium-delay','emS88SetCalciumDelay.onStart');
insert into PropertyValueMap values ('callback','em-s88-set-current-grade','emS88SetCurrentGrade.onStart');
insert into PropertyValueMap values ('callback','em-s88-set-data-values-for-c-rx-review','emS88SetDataValuesForCRxReview.onStart');
insert into PropertyValueMap values ('callback','em-s88-set-data-values-for-review','emS88SetDataValuesForReview.onStart');
insert into PropertyValueMap values ('callback','em-s88-set-data-values-for-series-review','emS88SetDataValuesForSeriesReview.onStart');
insert into PropertyValueMap values ('callback','em-s88-set-data-values-for-single-review','emS88SetDataValuesForSingleReview.onStart');
insert into PropertyValueMap values ('callback','em-s88-set-data-values-for-split-review','emS88SetDataValuesForSplitReview.onStart');
insert into PropertyValueMap values ('callback','em-s88-set-download-flag-timings-sps-cold-stick','emS88SetDownloadFlagTimingsSpsColdStick.onStart');
insert into PropertyValueMap values ('callback','em-s88-set-irganox-delay','emS88SetIrganoxDelay.onStart');
insert into PropertyValueMap values ('callback','em-s88-set-misc-flows-download-stat','emS88SetMiscFlowsDownloadStat.onStart');
insert into PropertyValueMap values ('callback','em-s88-set-oil-delay','emS88SetOilDelay.onStart');
insert into PropertyValueMap values ('callback','em-s88-set-recipe-data-c-rx-calc-stick','emS88SetRecipeDataCRcCalcStick.onStart');
insert into PropertyValueMap values ('callback','em-s88-set-recipe-data-cold-stick','emS88SetRecipeDataColdStick.onStart');
insert into PropertyValueMap values ('callback','em-s88-set-recipe-data-series-rx-calc-stick','emS88SetRecipeDataSeriesRxCalcStick.onStart');
insert into PropertyValueMap values ('callback','em-s88-set-recipe-data-split-rx-calc-stick','emS88SetRecipeDataSplitRxCalcStick.onStart');
insert into PropertyValueMap values ('callback','em-s88-set-small-h2-meter','emS88SetSmallH2Meter.onStart');
insert into PropertyValueMap values ('callback','em-s88-set-sp-time-fs','emS88SetSpTimeFs.onStart');
insert into PropertyValueMap values ('callback','em-s88-set-sp-time-rc','emS88SetSpTimeRc.onStart');
insert into PropertyValueMap values ('callback','em-s88-set-targets-cold-stick','emS88SetTargetsColdStick.onStart');
insert into PropertyValueMap values ('callback','em-s88-set-targets-series-rx-cold-stick','emS88SetTargetsSeriesRxColdStick.onStart');
insert into PropertyValueMap values ('callback','em-s88-set-unit-start-time','emS88SetUnitStartTime.onStart');
insert into PropertyValueMap values ('callback','em-s88-setup-for-cast-removal','emS88SetupForCastRemoval.onStart');
insert into PropertyValueMap values ('callback','em-s88-single-ramp-time-fs','emS88SingleRampTimeFs.onStart');
insert into PropertyValueMap values ('callback','em-s88-single-rx-mat-enrgy-balance-cold-stick','emS88SingleRxMatEnrgyBalanceColdStick.onStart');
insert into PropertyValueMap values ('callback','em-s88-single-series-c-rx-poststick-setpoints-cold-stick','emS88SingleSeriesCRxPoststickSetpointsColdStick.onStart');
insert into PropertyValueMap values ('callback','em-s88-single-series-calc-delay-drivers-cold-stick','emS88SingleSeriesCalcDelayDriversColdStick.onStart');
insert into PropertyValueMap values ('callback','em-s88-single-series-store-general-setpoints-cold-stick','emS88SingleSeriesStoreGeneralSetpointsColdStick.onStart');
insert into PropertyValueMap values ('callback','em-s88-single-split-c-rx-chk-ht-of-rx-cold-stick','emS88SingleSplitCRxChkHtOfRxColdStick.onStart');
insert into PropertyValueMap values ('callback','em-s88-single-split-tubular-rx-calc-monomer-flows-cold-stick','emS88SingleSplitTubularRxCalcMonomerFlowsColdStick.onStart');
insert into PropertyValueMap values ('callback','em-s88-single-store-setpoints-fs','emS88SingleStoreSetpoints.onStart');
insert into PropertyValueMap values ('callback','em-s88-split-calc-delay-drivers-cold-stick','emS88SplitCalcDelayDriversColdStick.onStart');
insert into PropertyValueMap values ('callback','em-s88-split-rx-mat-enrgy-balance-cold-stick','emS88SplitRxMatEnrgyBalanceColdStick.onStart');
insert into PropertyValueMap values ('callback','em-s88-split-rx-poststick-setpoints-cold-stick','emS88SplitRxPoststickSetpointsColdStick.onStart');
insert into PropertyValueMap values ('callback','em-s88-split-rx-store-general-setpoints-cold-stick','emS88SplitRxStoreGeneralSetpointsColdStick.onStart');
insert into PropertyValueMap values ('callback','em-s88-start-flying-switch-final-diag','emS88StartFlyingSwitchFinalDiag.onStart');
insert into PropertyValueMap values ('callback','em-s88-take-snapshot','emS88TakeSnapshot.onStart');
insert into PropertyValueMap values ('callback','em-s88-test-simulate-production','emS88TestSimulateProduction.onStart');
insert into PropertyValueMap values ('callback','em-s88-transfer-data','emS88TransferData.onStart');
insert into PropertyValueMap values ('callback','em-seed-rate-change-from-catout','emSeedRateChangeFromCatout.onStart');
insert into PropertyValueMap values ('callback','em-set-maximum-pv-monitor-time','emSetMaximumPvMonitorTime.onStart');
insert into PropertyValueMap values ('callback','em-set-split-top-feed-temp','emSetSplitTopFeedTemp.onStart');
insert into PropertyValueMap values ('callback','em-store-recipe-ml-limits','emStoreRecipeMlLimits.onStart');
insert into PropertyValueMap values ('callback','em-write-logfile-msg-for-operation','emWriteLogfileMsgForOperation.onStart');
insert into PropertyValueMap values ('callback','fs-error-log-message','fsErrorLogMessage.onStart');
insert into PropertyValueMap values ('callback','fs-final-r1-c6-flow-check','fsFinalR1c6FlowCheck.onStart');
insert into PropertyValueMap values ('callback','fs-final-select-modes-to-check','fsFinalSlectModesToCheck.onStart');
insert into PropertyValueMap values ('callback','fs-final-series-r1-c6-flow-check','fsFinalSeriesR1C6FlowCheck.onStart');
insert into PropertyValueMap values ('callback','fs-final-series-r1-select-modes-to-check','fsFinalSeriesR1SelectModesToCheck.onStart');
insert into PropertyValueMap values ('callback','fs-final-series-r2-c6-flow-check','fsFinalSeriesR2C6FlowCheck.onStart');
insert into PropertyValueMap values ('callback','fs-move-data-new-to-cur','fsMoveDataToNewCur.onStart');
insert into PropertyValueMap values ('callback','fs-move-series-to-rc','fsMoveSeriesToRc.onStart');
insert into PropertyValueMap values ('callback','fs-move-single-to-rc','fsMoveSingleToRc.onStart');
insert into PropertyValueMap values ('callback','fs-new-poly-rate','fsNewPolyRate.onStart');
insert into PropertyValueMap values ('callback','fs-series-base-data-review-setup','fsSeriesBaseDataReviewSetup.onStart');
insert into PropertyValueMap values ('callback','fs-series-mod-data-review-setup','fsSeriesModDataReviewSetup.onStart');
insert into PropertyValueMap values ('callback','fs-single-base-data-review-setup','fsSingleBaseDataReviewSetup.onStart');
insert into PropertyValueMap values ('callback','fs-single-mod-data-review-setup','fsSingleModDataReviewSetup.onStart');
insert into PropertyValueMap values ('callback','s88-catout-data-collector-c-reactor','s88CatoutDataCollectorCReactor.onStart');
insert into PropertyValueMap values ('callback','s88-catout-data-collector-series-reactor','s88CatoutDataCollectorSeriesReactor.onStart');
insert into PropertyValueMap values ('callback','s88-catout-data-collector-single-reactor','s88CatoutDataCollectorSingleReactor.onStart');
insert into PropertyValueMap values ('callback','s88-catout-data-collector-split-reactor','s88CatoutDataCollectorSplitReactor.onStart');
insert into PropertyValueMap values ('callback','s88-catout-determine-c3-ramp','s88CatoutDetermineC3Ramp.onStart');
insert into PropertyValueMap values ('callback','s88-change-grade-to-int-fs','s88ChangeGradeToIntFs.onStart');
insert into PropertyValueMap values ('callback','s88-check-if-ok-to-proceed-with-a-rate-change','s88CheckIfOkToProceedWithARateChange.onStart');
insert into PropertyValueMap values ('callback','s88-configure-rate-change-review-block','s88ConfigureRateChangeReviewBlock.onStart');
insert into PropertyValueMap values ('callback','s88-external-values-flying-switch','s88ExternalValuesFlyingSwitch.onStart');
insert into PropertyValueMap values ('callback','s88-fd-oil','s88FdOil.onStart');
insert into PropertyValueMap values ('callback','s88-initialize-rate-change-data','s88InitializeRateChangeData.onStart');
insert into PropertyValueMap values ('callback','s88-product-irganox','s88ProductIrganox.onStart');
insert into PropertyValueMap values ('callback','s88-r1-eff-c2','s88R1EffC2.onStart');
insert into PropertyValueMap values ('callback','s88-r1-eff-c9','s88R1EffC9.onStart');
insert into PropertyValueMap values ('callback','s88-rx-eff-c2','s88RxEffC2.onStart');
insert into PropertyValueMap values ('callback','s88-rx-eff-c9','s88RxEffC9.onStart');
insert into PropertyValueMap values ('callback','s88-store-new-grade-info-fs','s88StoreNewGradeInfoFs.onStart');
insert into PropertyValueMap values ('callback','s88-store-r2-recipe-data-fs','s88StoreR2RecipeDataFs.onStart');
insert into PropertyValueMap values ('callback','s88-store-r2-recipe-slopes-fs','s88StoreR2RecipeSlopes.onStart');
insert into PropertyValueMap values ('callback','s88-store-recipe-data-fs','s88StoreRecipeDataFs.onStart');
insert into PropertyValueMap values ('callback','s88-store-recipe-slopes-fs','s88StoreRecipeSlopesFs.onStart');
insert into PropertyValueMap values ('callback','s88-test-generic-callback','s88TestGenericCallback.onStart');
insert into PropertyValueMap values ('callback','s88-test-post-user-respone','s88TestPostUserResponse.onStart');
insert into PropertyValueMap values ('callback','s88-test-post-user-response','s88TestPostUserResponse.onStart');
insert into PropertyValueMap values ('callback','s88-test-set-parameter-false','s88TestSetParameterFalse.onStart');
insert into PropertyValueMap values ('callback','test-8-initialization','test8Initialization.onStart');
insert into PropertyValueMap values ('callback','test-8-path-1','test8Path1.onStart');
insert into PropertyValueMap values ('callback','test-8-path-2','test8Path2.onStart');
insert into PropertyValueMap values ('callback','test-8-timeout-callback','test8TimeoutCallback.onStart');

-- Convert onStart methods that are class-specific
insert into PropertyValueMap values ('class','EM-S88-INITIALIZE-CONTAINER-DATA','emS88InitializeContainerData.onStart');
insert into PropertyValueMap values ('class','EM-S88-REFRESH-OPC-INTERFACE','emGenericTask.onStart');
insert into PropertyValueMap values ('class','S88-START-DATA-PUMP','emGenericTask.onStart');
insert into PropertyValueMap values ('class','S88-STOP-DATA-PUMPS','emGenericTask.onStart');
insert into PropertyValueMap values ('class','S88-RESET-TASK','emGenericTask.onStart');

-- Convert values found in G2 blocks to proper Ignition equivalents.
-- Fields are: Ignition property, G2value, desired value
insert into PropertyValueMap values ('msgQueue','DRIER-REGEN-MESSAGES','DRIERREGEN');
insert into PropertyValueMap values ('stopOn','.TRUE','TRUE');
insert into PropertyValueMap values ('stopOn','.FALSE','FALSE');
insert into PropertyValueMap values ('trigger','.TRUE','TRUE');
insert into PropertyValueMap values ('trigger','.FALSE','FALSE');

insert into PropertyValueMap values ('position','CENTER','center');
insert into PropertyValueMap values ('position','TOP-LEFT','topLeft');
insert into PropertyValueMap values ('position','TOP-CENTER','topCenter');
insert into PropertyValueMap values ('position','TOP-RIGHT','topRight');
insert into PropertyValueMap values ('position','BOTTOM-LEFT','bottomLeft');
insert into PropertyValueMap values ('position','BOTTOM-CENTER','bottomCenter');
insert into PropertyValueMap values ('position','BOTTOM-RIGHT','bottomRight');

insert into PropertyValueMap values ('autoMode','SEMI-AUTOMATIC','semiAutomatic');
insert into PropertyValueMap values ('autoMode','AUTOMATIC','automatic');
