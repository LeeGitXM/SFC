-- NOTE: Tag names must be unique, folder placement is immaterial.
-- Columns are GSI name, tag path
--
insert into TagMap values ('A-BALER-TEMP-LAB-DATA','[]LabData/VFU/A-BALER-TEMP-LAB-DATA/value','DOUBLE');
insert into TagMap values ('AB-BALER-TEMP-LAB-DATA','[]LabData/VFU/AB-BALER-TEMP/value','DOUBLE');
insert into TagMap values ('AB-BALER-VOL-FTNIR-DATA','[]LabData/VFU/AB-BALER-VOL-FTNIR-DATA/value','DOUBLE');
insert into TagMap values ('AB-BALER-VOL-FTNIR-SQC-PATH','[]DiagnosticToolkit/Connections/ABBalerVolFtnir','STRING');
insert into TagMap values ('AB-BALER-VOL-LAB-DATA','[]LabData/VFU/AB-BALER-VOL-LAB-DATA/value','DOUBLE');
insert into TagMap values ('AB-BALER-VOL-LAB-SQC-PATH','[]DiagnosticToolkit/Connections/ABBalerVolLab','STRING');
insert into TagMap values ('ALKYL_FLOW','[]DiagnosticToolkit/CSTR/VCF262S/value','DOUBLE');
insert into TagMap values ('B-BALER-TEMP-LAB-DATA','[]LabData/VFU/B-BALER-TEMP-LAB-DATA/value','DOUBLE');
insert into TagMap values ('C-BALER-TEMP-LAB-DATA','[]LabData/VFU/C-BALER-TEMP-LAB-DATA/value','DOUBLE');
insert into TagMap values ('C-D-E-BALER-TEMP-SQC','[]DiagnosticToolkit/Connections/CDEBalerTemp','STRING');
insert into TagMap values ('C-RX_GRD_PATH-GDA','[]DiagnosticToolkit/Connections/CRxGrd','STRING');


insert into TagMap values ('C2-LAB-DATA','[]LabData/RLA3/C2-LAB-DATA/value','DOUBLE');
insert into TagMap values ('C2-LAB-DATA-FOR-R1-NLC','[]LabData/RLA3/C2-LAB-DATA-FOR-R1-NLC/value','DOUBLE');
insert into TagMap values ('C2_FLOW','[]DiagnosticToolkit/CSTR/VRF002S/value','DOUBLE');
insert into TagMap values ('C2_SQC_TO_C2_CSTR-GDA','[]DiagnosticToolkit/Connections/C2SqcToC2Cstr','STRING');


insert into TagMap values ('C3CONV_LOW_LIMIT','[]Site/CRX/Limits Targets and Gains/Limits/C3CONV-LOW-LIMIT','FLOAT8');
insert into TagMap values ('C3_CONVERSION','[]DiagnosticToolkit/CRx/VRG521Z/value','DOUBLE');
insert into TagMap values ('C6_RX_FEED-VNB','[]DiagnosticToolkit/CRx/C6-RX-IN-FEED','DOUBLE');
insert into TagMap values ('C9-GRADE-FLAG','[]DiagnosticToolkit/CSTR/C9-Grade-Flag/value','DOUBLE');
insert into TagMap values ('C9-IN-CRUMB','[]LabData/RLA3/C9-IN-CRUMB/value','DOUBLE');
insert into TagMap values ('C9-LAB-DATA','[]LabData/RLA3/C9-LAB-DATA/value','DOUBLE');
insert into TagMap values ('C9-SPEC-LIMIT-IN-FEED','[]LabData/C9-SPEC-LIMIT-IN-FEED','DOUBLE');
insert into TagMap values ('CA-LAB-DATA','[]LabData/RLA3/CA-LAB-DATA/value','DOUBLE');
insert into TagMap values ('CA-SQC-TO-CRX-GDA','[]DiagnosticToolkit/Connections/CaSqcToCrx','STRING');
insert into TagMap values ('CATEFF_HIGH_LIMIT','[]Site/CRX/Limits Targets and Gains/Limits/CATEFF-HIGH-LIMIT','FLOAT8');
insert into TagMap values ('CATIN-GDA-COUNTER','[]Site/CSTR/CATIN-COUNTER','INTEGER');
insert into TagMap values ('CATIN-GDA-PROD-ML-ACTIVE','[]Site/CSTR/CATIN-PROD-ML-ACTIVE','BOOLEAN');
insert into TagMap values ('CAT_EFFICIENCY','[]DiagnosticToolkit/CRx/VRG531Z-1/value','DOUBLE');
insert into TagMap values ('CAT_EFF_HIGH_LIMIT','[]Site/CRX/Limits Targets and Gains/Limits/CATEFF-HIGH-LIMIT','FLOAT8');
insert into TagMap values ('CAT_IS_IN','[]DiagnosticToolkit/Connections/CatIsIn-a','STRING');
insert into TagMap values ('CAT_IS_IN_PATH-GDA','[]DiagnosticToolkit/Connections/CatIsIn-b','STRING');
insert into TagMap values ('CAT_IS_OUT_PATH-GDA','[]DiagnosticToolkit/Connections/CatIsOut','STRING');
insert into TagMap values ('CAT_PREMIX_TEMP','[]DiagnosticToolkit/CRx/VCT205X/value','DOUBLE');
insert into TagMap values ('CD-BALER-TEMP-LAB-DATA','[]LabData/VFU/CD-BALER-TEMP-LAB-DATA/value','DOUBLE');
insert into TagMap values ('CD-BALER-VOL-FTNIR-DATA','[]LabData/VFU/CD-BALER-VOL-FTNIR-DATA/value','DOUBLE');
insert into TagMap values ('CD-BALER-VOL-FTNIR-SQC-PATH','[]DiagnosticToolkit/Connections/CDBalerVolFtnir','STRING');
insert into TagMap values ('CD-BALER-VOL-LAB-DATA','[]LabData/VFU/CD-BALER-VOL-LAB-DATA/value','DOUBLE');
insert into TagMap values ('CD-BALER-VOL-LAB-SQC-PATH','[]DiagnosticToolkit/Connections/CDBalerVolLab','STRING');
insert into TagMap values ('CD-BALER-VOL-ftnir-DATA','[]LabData/VFU/CD-BALER-VOL-FTNIR-DATA','DOUBLE');

insert into TagMap values ('CNTR_AVG_TPR_TIP_HT','[]Site/CRX/Calculated Variables/CNTR-AVG-TPR-TIP-HT/filteredValue','FLOAT8');
insert into TagMap values ('CNTR_AVG_TPR_TIP_HT_MAX_DEADBAND','[]Site/CRX/Limits Targets and Gains/Structure Molecule KBS/CNTR-AVG-TPR-TIP-HT-MAX-DEADBAND','FLOAT8');
insert into TagMap values ('CNTR_AVG_TPR_TIP_HT_TARGET','[]Site/CRX/Limits Targets and Gains/Structure Molecule KBS/CNTR-AVG-TPR-TIP-HT-TARGET','FLOAT8');
insert into TagMap values ('CRX-BLOCK-POLYMER-FLAG','[]DiagnosticToolkit/CRx/CRX-BLOCK-POLYMER-FLAG/value','DOUBLE');


insert into TagMap values ('C_FLYING_SWITCH-GDA-INPUT','[]Site/FlyingSwitch/CRX-FLYING-SWITCH-INPUT','BOOLEAN');
insert into TagMap values ('C_FLYING_SWITCH_OUTPUT','[]DiagnosticToolkit/Connections/CFlyingSwitchOutput','STRING');
insert into TagMap values ('C-RX_GRADE_FLAG','[]Recipe/Local/C-RX-GRADE','BOOLEAN');
insert into TagMap values ('CS-H2-MTR-IDEAL','[]Site/RLA3/CS-H2-MTR-IDEAL','DOUBLE');
insert into TagMap values ('CS-H2-MTR-USED','[]Site/RLA3/CS-H2-MTR-USED','DOUBLE');
insert into TagMap values ('CS-VRF006','[]Site/RLA3/CS-VRF006','DOUBLE');
insert into TagMap values ('CS-VXL204-CTS','[]Site/RLA3/CS-VXL204-CTS','STRING');
insert into TagMap values ('CS-VRF206','[]Site/RLA3/CS-VRF206','DOUBLE');
insert into TagMap values ('D-BALER-TEMP-LAB-DATA','[]LabData/VFU/D-BALER-TEMP-LAB-DATA/value','DOUBLE');
insert into TagMap values ('DC2-LAB-DATA','[]LabData/RLA3/DC2-LAB-DATA/value','DOUBLE');
insert into TagMap values ('DC9-LAB-DATA','[]LabData/RLA3/DC9-LAB-DATA/value','DOUBLE');
insert into TagMap values ('DML-LAB-DATA','[]LabData/RLA3/DML-LAB-DATA/value','DOUBLE');
insert into TagMap values ('DML-SQC-FLAG','[]LabData/DML-SQC-FLAG','DOUBLE');
insert into TagMap values ('DML-SQC-FLAG','[]Recipe/Local/DML-SQC-FLAG','DOUBLE');
insert into TagMap values ('dr-cool-down-start-time','[]SFC Parameters/Drier Regeneration/DR-COOLDOWN-START-TIME','STRING');
insert into TagMap values ('DR-D128A-REGEN-TIME','[]SFC IO/Drier Regeneration/VMJ132Z1-1/value','DOUBLE');
insert into TagMap values ('DR-D128a-ACT','[]SFC IO/Drier Regeneration/VMS128ME-1/value','DOUBLE');
insert into TagMap values ('DR-D128B-REGEN-TIME','[]SFC IO/Drier Regeneration/VMJ132Z2-1/value','DOUBLE');
insert into TagMap values ('DR-D128b-ACT','[]SFC IO/Drier Regeneration/VMS129ME-1/value','DOUBLE');
insert into TagMap values ('DR-D128C-REGEN-TIME','[]SFC IO/Drier Regeneration/VMJ132Z3-1/value','DOUBLE');
insert into TagMap values ('DR-D128C-ACT','[]SFC IO/Drier Regeneration/VMS130ME-1/value','DOUBLE');
insert into TagMap values ('DR-HEATER-BYPASS-ROV-PV','[]SFC IO/Drier Regeneration/VMR803X-1/value','DOUBLE');
insert into TagMap values ('DR-HEATER-IN-ROV-PV','[]SFC IO/Drier Regeneration/VMR801X-1/value','DOUBLE');
insert into TagMap values ('DR-MOISTURE-REMOVAL-START-TIME','[]SFC Parameters/Drier Regeneration/DR-MOISTURE-REMOVAL-START-TIME','DOUBLE');
insert into TagMap values ('DR-PE124B-PV','[]SFC IO/Drier Regeneration/VMS702X-1/value','DOUBLE');
insert into TagMap values ('E202-BYPASS-DELTA-TIME','[]Recipe/Local/RX-RECIPE/E202-BYPASS-DELTA-TIME','DOUBLE');
insert into TagMap values ('E-BALER-TEMP-LAB-DATA','[]LabData/VFU/E-BALER-TEMP-LAB-DATA/value','DOUBLE');
insert into TagMap values ('E-BALER-VOL-FTNIR-DATA','[]LabData/VFU/E-BALER-VOL-FTNIR-DATA/value','DOUBLE');
insert into TagMap values ('E-BALER-VOL-FTNIR-SQC-PATH','[]DiagnosticToolkit/Connections/EBalerVolFtnir','STRING');
insert into TagMap values ('E-BALER-VOL-LAB-DATA','[]LabData/VFU/E-BALER-VOL-LAB-DATA/value','DOUBLE');
insert into TagMap values ('E-BALER-VOL-LAB-SQC-PATH','[]DiagnosticToolkit/Connections/EBalerVolLab','STRING');
insert into TagMap values ('EM-COLD-STICK-RX-OUTLET-TEMP-MONITOR-BLOCK-1','[]Site/VFU/COLDSTICK-OUTLET-TEMP-MONITOR','STRING');
insert into TagMap values ('EM-MONITOR-COLDSTICK-DCS-SETPOINTS-BLOCK','[]Site/VFU/COLDSTICK-DCS-SETPOINT','STRING');

insert into TagMap values ('ML_GRAVITY_TIMER_WAITING_STATE','[]Site/CRx/ML-GRAVITY-TIMER-STATE','BOOLEAN');

-- tag not built
insert into TagMap values ('EM-MONITOR-COLDSTICK-DCS-SETPOINTS-BLOCK','[]Site/DCS-SETPOINTS/monitorColdstick','STRING');    --- found in s88


insert into TagMap values ('FREEZER-SAMPLE-SWITCH','[]Site/VFU/FREEZER-SAMPLE-SWITCH','BOOLEAN');
insert into TagMap values ('FRNT_AVG_C2','[]Site/CRX/OPC Input Variables/Front of Molecule/CRX-HB-9/value','FLOAT8');
insert into TagMap values ('FRNT_AVG_C2_HIGH_LIMIT','[]Site/CRX/Limits Targets and Gains/Physical Properties/FRNT-AVG-C2-HIGH-LIMIT','FLOAT8');
insert into TagMap values ('FRNT_AVG_C2_NOT_HIGH_PATH-GDA','[]DiagnosticToolkit/Connections/FrntAvgC2NotHigh','STRING');
insert into TagMap values ('FRNT_AVG_C2_TARGET','[]Site/CRX/Limits Targets and Gains/Physical Properties/FRNT-AVG-C2-TARGET','FLOAT8');
insert into TagMap values ('FRNT_FEED_DIFF','[]Site/CRX/Calculated Variables/FRNT-FEED-DIFF/filteredValue','FLOAT8');
insert into TagMap values ('FRNT_LNGTH','[]Site/CRX/OPC Input Variables/Front of Molecule/CRX-HB-8/value','FLOAT8');
insert into TagMap values ('FRNT_LNGTH_ABOVE_SP_PATH-GDA','[]DiagnosticToolkit/Connections/FrntLngthAboveSP','STRING');
insert into TagMap values ('FRNT_LNGTH_BELOW_SETPOINT_TO_SF-4-GDA','[]DiagnosticToolkit/Connections/FrntLngthBelowSetpointSF4','STRING');
insert into TagMap values ('FRNT_LNGTH_HIGH_LIMIT','[]Site/CRX/Limits Targets and Gains/Structure Molecule KBS/FRNT-LNGTH-HIGH-LIMIT','FLOAT8');
insert into TagMap values ('FRNT_LNGTH_LOW_LIMIT','[]Site/CRX/Limits Targets and Gains/Structure Molecule KBS/FRNT-LNGTH-LOW-LIMIT','FLOAT8');
insert into TagMap values ('FRNT_LNGTH_SHORT_TO_MOONEY_LOGIC-GDA','[]DiagnosticToolkit/Connections/FrntLngthShortToMooneyLogic','STRING');
insert into TagMap values ('FRNT_LNGTH_TARGET','[]Site/CRX/Limits Targets and Gains/Structure Molecule KBS/FRNT-LNGTH-TARGET','FLOAT8');
insert into TagMap values ('FRNT_SDSTRM_MAX_DIFF','[]Site/CRX/Limits Targets and Gains/Structure Molecule KBS/FRNT-SDSTRM-MAX-DIFF','FLOAT8');
insert into TagMap values ('FRNT_TPR_TIP_HT_DIFF','[]Site/CRX/Calculated Variables/FRNT-TPR-TIP-HT-DIFF/value','FLOAT8');
insert into TagMap values ('FRNT_TPR_TIP_HT_HIGH_LIMIT','[]Site/CRX/Limits Targets and Gains/Structure Molecule KBS/FRNT-TPR-TIP-HT-HIGH-LIMIT','FLOAT8');
insert into TagMap values ('FRNT_TPR_TIP_HT_MAX_DIFF','[]Site/CRX/Limits Targets and Gains/Structure Molecule KBS/FRNT-TPR-TIP-HT-MAX-DIFF','FLOAT8');
insert into TagMap values ('FS-C3STRM_C2','[]DiagnosticToolkit/CRx/FS-C3STRM-C2','DOUBLE');    --- found in s88
insert into TagMap values ('FS-C3STRM_C3','[]DiagnosticToolkit/CRx/FS-C3STRM-C3','DOUBLE');    --- found in s88
insert into TagMap values ('FS-C3STRM_C2o','[]DiagnosticToolkit/CRx/FS-C3STRM-C2o','DOUBLE');    --- found in s88
insert into TagMap values ('FS-C3STRM_C3o','[]DiagnosticToolkit/CRx/FS-C3STRM-C3o','DOUBLE');    --- found in s88
insert into TagMap values ('g2-latency-time','[]Site/G2-LATENCY-TIME','DOUBLE');
insert into TagMap values ('GAIN_AVG-C2_MAIN-C3','[]Site/CRX/Limits Targets and Gains/Gains/GAIN-AVG-C2-MAIN-C3','FLOAT8');
insert into TagMap values ('GAIN_CAT_C3','[]Site/CRX/Limits Targets and Gains/Gains/GAIN-CAT-C3','FLOAT8');
insert into TagMap values ('GAIN_CAT_TEMP','[]Site/CRX/Limits Targets and Gains/Gains/GAIN-CAT-TEMP','FLOAT8');
insert into TagMap values ('GAIN_CNTR-TPR-TIP_SS-C3-to-C2','[]Site/CRX/Limits Targets and Gains/Gains/GAIN-CNTR-TPR-TIP-SS-C3-TO-C2','FLOAT8');
insert into TagMap values ('GAIN_FRNT-C2_MAIN-FD','[]Site/CRX/Limits Targets and Gains/Gains/GAIN-FRNT-C2-MAIN-FD','FLOAT8');
insert into TagMap values ('GAIN_FRNT-C2_SDSTRM-FD','[]Site/CRX/Limits Targets and Gains/Gains/GAIN-FRNT-C2-SDSTRM-FD','FLOAT8');
insert into TagMap values ('GAIN_LNGTH_TEMP','[]Site/CRX/Limits Targets and Gains/Gains/GAIN-LNGTH-TEMP','FLOAT8');
insert into TagMap values ('GAIN_ML_TEMP','[]Site/CRX/Limits Targets and Gains/Gains/GAIN-ML-TEMP','FLOAT8');








insert into TagMap values ('MAIN-C2','[]Site/CRx/MAIN-C2','DOUBLE');
insert into TagMap values ('MAIN-C3','[]Site/CRx/MAIN-C3','DOUBLE');
insert into TagMap values ('MAX_CNTR_TPR_TIP_DELTA_FM_AVG','[]Site/CRX/Calculated Variables/MAX-CNTR-TPR-TIP-DELTA-FM-AVG/filteredValue','FLOAT8');
insert into TagMap values ('MIXTEE_IN_USE_0_EAST_1_WEST','[]DiagnosticToolkit/CRx/VCT205X-2/value','DOUBLE');
insert into TagMap values ('MLR-GRADE-FLAG','[]Recipe/Local/MLR-GRADE-FLAG','DOUBLE');
insert into TagMap values ('MLR-LAB-DATA','[]LabData/RLA3/MLR-LAB-DATA/value','DOUBLE');
insert into TagMap values ('ML_HIGH-GDA','[]DiagnosticToolkit/Connections/MlHigh','STRING');
insert into TagMap values ('ML_LOW_PATH-GDA','[]DiagnosticToolkit/Connections/MlLow','STRING');
insert into TagMap values ('MOONEY-LAB-DATA','[]LabData/RLA3/MOONEY-LAB-DATA/value','DOUBLE');
insert into TagMap values ('MOONEY_RESET_TIME_FOR_SF-3','[]Site/CRX/MOONEY-RESET-TIME-FOR-SF-3/value','DOUBLE');
insert into TagMap values ('MOONEY_SQC_TO_CSTR_MOONEY-GDA','[]DiagnosticToolkit/Connections/MooneySqcToCstrMooney','STRING');
insert into TagMap values ('MY-CONNECTION-POST','[]DiagnosticToolkit/Connections/MyConnectionPost','STRING');
insert into TagMap values ('OK_TO_FETCH_SC-4_DATA-GDA','[]Site/CRx/OK-TO-FETCH-SC4','BOOLEAN');
insert into TagMap values ('OIL-GRADE-FLAG','[]DiagnosticToolkit/CSTR/OIL-GRADE-FLAG/value','DOUBLE');
insert into TagMap values ('OIL-LAB-DATA','[]LabData/RLA3/OIL-LAB-DATA/value','DOUBLE');
insert into TagMap values ('POLYSPLIT-DATA','[]LabData/RLA3/POLYSPLIT-DATA-SQC','DOUBLE');
insert into TagMap values ('POLYSPLIT-DATA','[]LabData/RLA3/POLYSPLIT-DATA/value','DOUBLE');
insert into TagMap values ('POLYSPLIT-SQC-FLAG','[]Recipe/Local/POLYSPLIT-SQC-FLAG','DOUBLE');
insert into TagMap values ('POLY_RATE_CHANGE-GDA-INPUT','[]Site/RateChange/CRX-RATE-CHANGE-INPUT','BOOLEAN');
insert into TagMap values ('POLY_RATE_CHANGE_OUTPUT','[]DiagnosticToolkit/Connections/PolyRateChangeOutput','STRING');
insert into TagMap values ('PREMIX_LINE_DATA_OK-GDA','[]Site/CRx/PREMIX-LINE-DATA-OK ','STRING');
insert into TagMap values ('PREMIX_LINE_FRESH-GDA-PARAMETER','[]Site/CRx/PREMIX-LINE-FRESH-PARAMETER','STRING');
insert into TagMap values ('PREMIX_LINE_FRESH-PATH-GDA','[]DiagnosticToolkit/Connections/PremixLineFresh','STRING');
insert into TagMap values ('PREMIX_TEMP_AVAIL_PATH-GDA','[]DiagnosticToolkit/Connections/PremixTempAvail','STRING');
insert into TagMap values ('PREMIX_TEMP_CHG_PERMITTED-PATH-GDA','[]DiagnosticToolkit/Connections/PremixTempChgPermitted','STRING');
insert into TagMap values ('PREMIX_TEMP_HIGH_LIMIT','[]Site/CRX/Limits Targets and Gains/Limits/PREMIX-TEMP-HIGH-LIMIT/value','FLOAT8');
insert into TagMap values ('PREMIX_TEMP_LESS_THAN_MAX_PATH-GDA','[]DiagnosticToolkit/Connections/PremixTempLessThanMax','STRING');
insert into TagMap values ('PROD-CA-SQC-FLAG','[]Recipe/Local/PROD-CA-SQC-FLAG','DOUBLE');
insert into TagMap values ('PROD-ML-LAB-DATA','[]LabData/PROD-ML-LAB-DATA/highLImit','DOUBLE');
insert into TagMap values ('PROD-ML-LAB-DATA','[]LabData/RLA3/PROD-ML-LAB-DATA/value','DOUBLE');
insert into TagMap values ('PROD-ML-VFU-OK','[]Site/VFU/Prod-ML-VFU-OK','BOOLEAN');
insert into TagMap values ('RATE_CHANGE_OUTPUT','[]DiagnosticToolkit/Connections/RateChangeOutput','STRING');
insert into TagMap values ('RLA3-CURRENT-GRADE','[]DiagnosticToolkit/CSTR/RLA3-Current-Grade/value','DOUBLE');
insert into TagMap values ('rla3-run-hours','[]Site/RLA3-RUN-HOURS','DOUBLE');
insert into TagMap values ('RX-ML-OK-PATH-GDA','[]DiagnosticToolkit/Connections/RxMlOk','STRING');
insert into TagMap values ('RX_CONFIGURATION','[]DiagnosticToolkit/CSTR/RX-CONFIGURATION/value','DOUBLE');
insert into TagMap values ('SD-STRM-C2_FLOW','[]DiagnosticToolkit/CRX/VRF202S/value','DOUBLE');
insert into TagMap values ('SDSTRM-1','[]DiagnosticToolkit/CRx/SDSTRM-1','DOUBLE');
insert into TagMap values ('SDSTRM-2','[]DiagnosticToolkit/CRx/SDSTRM-2','DOUBLE');
insert into TagMap values ('SDSTRM-3','[]DiagnosticToolkit/CRx/SDSTRM-3','DOUBLE');
insert into TagMap values ('SDSTRM-4','[]DiagnosticToolkit/CRx/SDSTRM-4','DOUBLE');
insert into TagMap values ('SDSTRM-5','[]DiagnosticToolkit/CRx/SDSTRM-5','DOUBLE');
insert into TagMap values ('SDSTRM-6','[]DiagnosticToolkit/CRx/SDSTRM-6','DOUBLE');
insert into TagMap values ('SDSTRM-C2','[]DiagnosticToolkit/CRx/SDSTRM-C2','DOUBLE');
insert into TagMap values ('SDSTRM-C3C2-RATIO','[]DiagnosticToolkit/CRx/VRF503R-2/value','DOUBLE');
insert into TagMap values ('SDSTRM_C3-TO-C2_RATIO_HIGH_LIMIT','[]Site/CRX/Limits Targets and Gains/Limits/SDSTRM-C3-TO-C2-RATIO-HIGH-LIMIT','FLOAT8');
insert into TagMap values ('SDSTRM_C3-TO-C2_RATIO_LOW_LIMIT','[]Site/CRX/Limits Targets and Gains/Limits/SDSTRM-C3-TO-C2-RATIO-LOW-LIMIT','FLOAT8');
insert into TagMap values ('SDSTRM_C3C2_RATIO','[]DiagnosticToolkit/Connections/SdstrmC3C2Ratio','STRING');
insert into TagMap values ('SERIES_FLYING_SWITCH-GDA-INPUT','[]Site/FlyingSwitch/SERIES-FLYING-SWITCH-INPUT','BOOLEAN');
insert into TagMap values ('SERIES_FLYING_SWITCH_OUTPUT','[]DiagnosticToolkit/Connections/SeriesFlyingSwitchOutput','STRING');
insert into TagMap values ('SERIES_RATE_CHANGE-GDA-INPUT','[]Site/RateChange/SERIES-RATE-CHANGE-INPUT','BOOLEAN');
insert into TagMap values ('SERIES_RATE_CHANGE_OUTPUT','[]DiagnosticToolkit/Connections/SeriesRateChangeOutput','STRING');
insert into TagMap values ('SERIES_RX_GRADE','[]Recipe/Local/SERIES-RX-GRADE','BOOLEAN');
insert into TagMap values ('SINGLE_FLYING_SWITCH-GDA-INPUT','[]Site/FlyingSwitch/SINGLE-FLYING-SWITCH-INPUT','BOOLEAN');
insert into TagMap values ('SINGLE_FLYING_SWITCH_OUTPUT','[]DiagnosticToolkit/Connections/SingleFlyingSwitchOutput','STRING');
insert into TagMap values ('SINGLE_RATE_CHANGE-GDA-INPUT','[]Site/RateChange/SINGLE-RATE-CHANGE-INPUT','BOOLEAN');
insert into TagMap values ('SINGLE_RATE_CHANGE_OUTPUT','[]DiagnosticToolkit/Connections/SingleRateChangeOutput','STRING');
insert into TagMap values ('SINGLE_RX_GRADE','[]Recipe/Local/SINGLE-RX-GRADE','BOOLEAN');
insert into TagMap values ('SPLIT_FLYING_SWITCH-GDA-INPUT','[]Site/FlyingSwitch/SPLIT-FLYING-SWITCH-INPUT','BOOLEAN');
insert into TagMap values ('SPLIT_FLYING_SWITCH_OUTPUT','[]DiagnosticToolkit/Connections/SplitFlyingSwitchOuput','STRING');
insert into TagMap values ('SPLIT_RATE_CHANGE-GDA-INPUT','[]Site/RateChange/SPLIT-RATE-CHANGE-INPUT','BOOLEAN');
insert into TagMap values ('SPLIT_RATE_CHANGE_OUTPUT','[]DiagnosticToolkit/Connections/SplitRateChangeOutput','STRING');
insert into TagMap values ('SS1_TAPER_TIP_HEIGHT','[]Site/CRX/Calculated Variables/SS1-TAPER-TIP-HEIGHT/filteredValue','FLOAT8');
insert into TagMap values ('SS2_TAPER_TIP_HEIGHT','[]Site/CRX/Calculated Variables/SS2-TAPER-TIP-HEIGHT/filteredValue','FLOAT8');
insert into TagMap values ('STAB-LAB-DATA','[]LabData/RLA3/STAB-LAB-DATA/value','DOUBLE');
insert into TagMap values ('STAB_SQC_TO_CRX-GDA','[]DiagnosticToolkit/Connections/StabSqcToCrx','STRING');
insert into TagMap values ('SWAP_CNTR_FEEDS_TIMER-GDA','[]Site/Parameters/SwapCenterFeedsTimer','STRING');
insert into TagMap values ('UNIFORM_CNTR_TPR_TIPS_PATH-GDA','[]DiagnosticToolkit/Connections/UniformCntrTprTips','STRING');
insert into TagMap values ('VFU-BALER-TEMP-CHK','[]Recipe/Local/VFU-BALER-TEMP-CHK','DOUBLE');
insert into TagMap values ('VFU-CURRENT-GRADE','[]DiagnosticToolkit/CRx/RLA3-Current-Grade','DOUBLE');
insert into TagMap values ('VFU-FTNIR-GRADE','[]Recipe/Local/VFU-FTNIR-GRADE','DOUBLE');
insert into TagMap values ('vrc023_sp','[]Site/CRx/VRC023/sp','DOUBLE');
insert into TagMap values ('VRC062_SP','[]Site/CRx/VRC062/sp','DOUBLE');
insert into TagMap values ('vrc032_sp','[]Site/CRx/VRC032/sp','DOUBLE');
insert into TagMap values ('vrc253_sp','[]Site/CRx/VRC253/sp','DOUBLE');
insert into TagMap values ('vrf214_sp','[]Site/CRx/VRF214/sp','DOUBLE');
insert into TagMap values ('vrf224_sp','[]Site/CRx/VRF224/sp','DOUBLE');
insert into TagMap values ('VRC262_SP','[]Site/CRx/VRC262/sp','DOUBLE');
insert into TagMap values ('the action of the action-button upon the subworkspace of dr-oc-alert-msg','[]Site/DR-OC-ALERT-MSG/action','BOOLEAN');
insert into TagMap values ('the arrow icon-color of the em-setpoint-display-button upon the subworkspace of the item named by console-list [post-index]','[]Site/SETPOINT-DISPLAY-BUTTON/arrowColor','BOOLEAN');
insert into TagMap values ('the btm-message-text of dr-oc-alert-msg','[]Site/DR-OC-ALERT-MSG/bottomText','BOOLEAN');
insert into TagMap values ('the dr-top-temp-reached-timer','[]Site/DRYER-REGEN/topTempReachedTimer','STRING');    --- found in s88
insert into TagMap values ('the message-text of FS-DOWNLOAD-REMINDER-OPERATOR-DISPLAY','[]DiagnosticToolkit/CRx/FS-DOWNLOAD-REMONDER-OPERATOR-DISPLAY','STRING');    --- found in s88
insert into TagMap values ('the cast-time-to-closed of CATOUT-RECIPE-STATUS','[]Site/CATOUT-RECIPE-STATUS/castTimeToCLosed','STRING');    --- found in s88
insert into TagMap values ('the mid-temp of the driers named by drier-to-regen','[]Site/DRYER-REGEN/midTemp','STRING');    --- found in s88
insert into TagMap values ('the outlet-temp of the driers named by drier-to-regen','[]Site/DRYER-REGEN/outletTemp','STRING');    --- found in s88
insert into TagMap values ('the irg-time-to-closed of CATOUT-RECIPE-STATUS','[]Site/CATOUT-RECIPE-STATUS/irgTimeToClosed','STRING');    --- found in s88
insert into TagMap values ('the rx-temp of RX-RECIPE','[]Site/RX-RECIPE/rxTemp','STRING'); 
insert into TagMap values ('the oil-time-to-closed of CATOUT-RECIPE-STATUS','[]Site/CATOUT-RECIPE-STATUS/oilTimeToCLosed','STRING');    --- found in s88
insert into TagMap values ('the lower_limit of DC9-LAB-DATA]','[]LabData/RLA3/DC9-LAB-DATA/lowerLimit','DOUBLE');
insert into TagMap values ('the upper_limit of DC9-LAB-DATA]','[]LabData/RLA3/DC9-LAB-DATA/upperLimit','DOUBLE');
insert into TagMap values ('the state of cat-and-c2-is-in-gda','[]Site/CAT-AND-C2/state','STRING');
insert into TagMap values ('the state of opr-no-reply','[]Site/OPR-NO-REPLY/state','STRING');
insert into TagMap values ('the state of opr-stop','[]Site/OPR-STOP/state','STRING');
insert into TagMap values ('the state of done','[]Site/DONE/state','STRING');
insert into TagMap values ('the subworkspace of UIR-TABLES','[]Site/UIR-TABLES','STRING');
insert into TagMap values ('the target-value of e204-level-ok-conditional','[]Site/E204-LEVEL-OK','BOOLEAN');
insert into TagMap values ('the target-value of e204-level-not-ok-conditional','[]Site/E204-LEVEL-NOT-OK','BOOLEAN');
insert into TagMap values ('the top-message-text of dr-oc-alert-msg','[]Site/DR-OC-ALERT-MSG/topText','BOOLEAN');
insert into TagMap values ('the uir-button-label of UIRdata','[]Site/UIR-DATA/buttonLabel','STRING');
insert into TagMap values ('the uir-date of UIRdata','[]Site/UIR-DATA/date','STRING');
insert into TagMap values ('the uir-email-config of UIRdata','[]Site/UIR-DATA/emailConfig','STRING');
insert into TagMap values ('the uir-form-name of UIRdata','[]Site/UIR-DATA/formName','STRING');
insert into TagMap values ('the uir-identifier of UIRdata','[]Site/UIR-DATA/identifier','STRING');
insert into TagMap values ('the uir-incident-start of UIRdata','[]Site/UIR-DATA/incidentStart','STRING');
insert into TagMap values ('the uir-operator-post of UIRdata','[]Site/UIR-DATA/operatorPost','STRING');
insert into TagMap values ('the uir-quality-18 of UIRdata','[]Site/UIR-DATA/quality18','STRING');
insert into TagMap values ('the uir-root-15 of UIRdata','[]Site/UIR-DATA/root15','STRING');
insert into TagMap values ('the uir-summary of UIRdata','[]Site/UIR-DATA/summary','STRING');
insert into TagMap values ('the uir-title of UIRdata','[]Site/UIR-DATA/title','STRING');
insert into TagMap values ('the uir-unit of UIRdata','[]Site/UIR-DATA/unit','STRING');
insert into TagMap values ('the uir-window-name of UIRdata','[]Site/UIR-DATA/windowName','STRING');
insert into TagMap values ('[the bad-value of ALKYL_FLOW]','[]DiagnosticToolkit/CSTR/VCF262S/badValue','BOOLEAN');
insert into TagMap values ('[the bad-value of C2_FLOW]','[]DiagnosticToolkit/CSTR/VRF002S/badValue','BOOLEAN');
insert into TagMap values ('[the bad-value of C3_CONVERSION]','[]DiagnosticToolkit/CRx/VRG521Z/badValue','BOOLEAN');
insert into TagMap values ('[the bad-value of C9-GRADE-FLAG]','[]DiagnosticToolkit/CSTR/C9-Grade-Flag/badValue','BOOLEAN');
insert into TagMap values ('[the bad-value of CAT_EFFICIENCY]','[]DiagnosticToolkit/CRx/VRG531Z-1/badValue','BOOLEAN');
insert into TagMap values ('[the bad-value of CAT_PREMIX_TEMP]','[]DiagnosticToolkit/CRx/VCT205X-2/badValue','BOOLEAN');
insert into TagMap values ('[the bad-value of CNTR_AVG_TPR_TIP_HT]','[]Site/CRx/CNTR-AVG-TPR-TIP-HT/badValue','BOOLEAN');
insert into TagMap values ('[the bad-value of CRX-BLOCK-POLYMER-FLAG]','[]DiagnosticToolkit/CRx/CRX-BLOCK-POLYMER-FLAG/badValue','BOOLEAN');
insert into TagMap values ('[the bad-value of FRNT_AVG_C2]','[]Site/CRX/OPC Input Variables/Front of Molecule/CRX-HB-9/badValue','BOOLEAN'); 
insert into TagMap values ('[the bad-value of FRNT_FEED_DIFF]','[]Site/CRx/FRNT-FEED-DIFF/badValue','BOOLEAN');
insert into TagMap values ('[the bad-value of FRNT_LNGTH]','[]Site/CRX/OPC Input Variables/Front of Molecule/CRX-HB-8/badValue','BOOLAB-BALER-VOL-FTNIR-DATAEAN');
insert into TagMap values ('[the bad-value of FRNT_TPR_TIP_HT_DIFF]','[]Site/CRx/FRNT-TPR-TIP-HT-DIFF/badValue','BOOLEAN');
insert into TagMap values ('[the bad-value of MAX_CNTR_TPR_TIP_DELTA_FM_AVG]','[]Site/CRx/MAX-CNTR-TPR-TIP-DELTA-FM-AVG/badValue','BOOLEAN');
insert into TagMap values ('[the bad-value of MIXTEE_IN_USE_0_EAST_1_WEST]','[]DiagnosticToolkit/CRx/VCT205X-2/badValue','BOOLEAN');
insert into TagMap values ('[the bad-value of MOONEY_RESET_TIME_FOR_SF-3]','[]Site/CRX/Calculated Variables/MOONEY-RESET-TIME-FOR-SF-3/filteredValue','BOOLEAN');
insert into TagMap values ('[the bad-value of OIL-GRADE-FLAG]','[]DiagnosticToolkit/CSTR/OIL-GRADE-FLAG/badValue','BOOLEAN');
insert into TagMap values ('[the bad-value of RLA3-CURRENT-GRADE]','[]DiagnosticToolkit/CSTR/RLA3-CURRENT-GRADE/badValue','BOOLEAN');
insert into TagMap values ('[the bad-value of RX_CONFIGURATION ]','[]DiagnosticToolkit/CSTR/RX-CONFIGURATION/badValue','BOOLEAN');
insert into TagMap values ('[the bad-value of RX_CONFIGURATION]','[]DiagnosticToolkit/CSTR/RX-CONFIGURATION/badValue','BOOLEAN');
insert into TagMap values ('[the bad-value of SD-STRM-C2_FLOW]','[]DiagnosticToolkit/CRX/VRF202S/badValue','BOOLEAN');
insert into TagMap values ('[the bad-value of SDSTRM-C3C2-RATIO]','[]DiagnosticToolkit/CRX/VRF503R-2/badValue','BOOLEAN');
insert into TagMap values ('[the bad-value of SS1_TAPER_TIP_HEIGHT]','[]Site/CRx/SS1-TAPER-TIP-HEIGHT/badValue','BOOLEAN');
insert into TagMap values ('[the bad-value of SS2_TAPER_TIP_HEIGHT]','[]Site/CRx/SS2-TAPER-TIP-HEIGHT/badValue','BOOLEAN');




















insert into TagMap values ('[the depth-at-outlet of crx-zone-1]','[]Site/CRx/ZONE1/DepthAtOutlet','DOUBLE');
insert into TagMap values ('[the depth-at-outlet of crx-zone-2]','[]Site/CRx/ZONE2/DepthAtOutlet','DOUBLE');
insert into TagMap values ('[the height-at-inlet of crx-zone-2]','[]Site/CRx/ZONE2/HeightAtInlet','DOUBLE');
insert into TagMap values ('[the height-at-inlet of crx-zone-3]','[]Site/CRx/ZONE3/HeightAtInlet','DOUBLE');
insert into TagMap values ('[the sdstrm-monomer-flow of crx-zone-2]','[]Site/CRx/ZONE2/SdstrmMonomerFlow','DOUBLE');
insert into TagMap values ('[the sdstrm-monomer-flow of crx-zone-3]','[]Site/CRx/ZONE3/SdstrmMonomerFlow','DOUBLE');
insert into TagMap values ('[the lower_limit of MOONEY-LAB-DATA]','[]LabData/RLA3/MOONEY-LAB-DATA/lowerLimit','DOUBLE');
insert into TagMap values ('[the lower_limit of STAB-LAB-DATA]','[]LabData/RLA3/STAB-LAB-DATA/lowerLimit','DOUBLE');











insert into TagMap values ('[the standard-deviation of A-BALER-TEMP-LAB-DATA]','[]LabData/VFU/A-BALER-TEMP-LAB-DATA-SQC/standardDeviation','DOUBLE');
insert into TagMap values ('[the standard-deviation of AB-BALER-VOL-ftnir-DATA]','[]LabData/VFU/AB-BALER-VOL-FTNIR-DATA-SQC/standardDeviation','DOUBLE');
insert into TagMap values ('[the standard-deviation of AB-BALER-VOL-lab-DATA]','[]LabData/VFU/AB-BALER-VOL-LAB-DATA-SQC/standardDeviation','DOUBLE');
insert into TagMap values ('[the standard-deviation of B-BALER-TEMP-LAB-DATA]','[]LabData/VFU/B-BALER-TEMP-LAB-DATA-SQC/standardDeviation','DOUBLE');
insert into TagMap values ('[the standard-deviation of C-BALER-TEMP-LAB-DATA]','[]LabData/VFU/C-BALER-TEMP-LAB-DATA-SQC/standardDeviation','DOUBLE');
insert into TagMap values ('[the standard-deviation of C2-LAB-DATA]','[]LabData/RLA3/C2-LAB-DATA-SQC/standardDeviation','DOUBLE');
insert into TagMap values ('[the standard-deviation of CA-LAB-DATA]','[]LabData/RLA3/CA-LAB-DATA-SQC/standardDeviation','DOUBLE');
insert into TagMap values ('[the standard-deviation of CD-BALER-VOL-ftnir-DATA]','[]LabData/VFU/CD-BALER-VOL-FTNIR-DATA-SQC/standardDeviation','DOUBLE');
insert into TagMap values ('[the standard-deviation of CD-BALER-VOL-lab-DATA]','[]LabData/VFU/CD-BALER-VOL-LAB-DATA-SQC/standardDeviation','DOUBLE');
insert into TagMap values ('[the standard-deviation of D-BALER-TEMP-LAB-DATA]','[]LabData/VFU/D-BALER-TEMP-LAB-DATA-SQC/standardDeviation','DOUBLE');
insert into TagMap values ('[the standard-deviation of DC2-LAB-DATA]','[]LabData/DC2-LAB-DATA/standardDeviation','DOUBLE');
insert into TagMap values ('[the standard-deviation of DC2-LAB-DATA]','[]LabData/RLA3/DC2-LAB-DATA-SQC/standardDeviation','DOUBLE');
insert into TagMap values ('[the standard-deviation of DML-LAB-DATA]','[]LabData/DML-LAB-DATA/standardDeviation','DOUBLE');
insert into TagMap values ('[the standard-deviation of DML-LAB-DATA]','[]LabData/RLA3/DML-LAB-DATA-SQC/standardDeviation','DOUBLE');
insert into TagMap values ('[the standard-deviation of Dc9-lab-data]','[]LabData/RLA3/DC9-LAB-DATA-SQC/standardDeviation','DOUBLE');
insert into TagMap values ('[the standard-deviation of E-BALER-TEMP-LAB-DATA]','[]LabData/VFU/E-BALER-TEMP-LAB-DATA-SQC/standardDeviation','DOUBLE');
insert into TagMap values ('[the standard-deviation of E-BALER-VOL-ftnir-DATA]','[]LabData/E-BALER-VOL-FTNIR-DATA/standardDeviation','DOUBLE');
insert into TagMap values ('[the standard-deviation of E-BALER-VOL-ftnir-DATA]','[]LabData/VFU/E-BALER-VOL-FTNIR-DATA-SQC/standardDeviation','DOUBLE');
insert into TagMap values ('[the standard-deviation of E-BALER-VOL-lab-DATA]','[]LabData/VFU/E-BALER-VOL-LAB-DATA-SQC/standardDeviation','DOUBLE');
insert into TagMap values ('[the standard-deviation of MLR-LAB-DATA]','[]LabData/RLA3/MLR-LAB-DATA-SQC/standardDeviation','DOUBLE');
insert into TagMap values ('[the standard-deviation of MOONEY-LAB-DATA]','[]LabData/RLA3/MOONEY-LAB-DATA-SQC/standardDeviation','DOUBLE');
insert into TagMap values ('[the standard-deviation of OIL-LAB-DATA]','[]LabData/RLA3/OIL-LAB-DATA-SQC/standardDeviation','DOUBLE');
insert into TagMap values ('[the standard-deviation of POLYSPLIT-DATA]','[]LabData/RLA3/POLYSPLIT-DATA-SQC/standardDeviation','DOUBLE');
insert into TagMap values ('[the standard-deviation of PROD-ML-LAB-DATA]','[]LabData/RLA3/PROD-ML-LAB-DATA-SQC/standardDeviation','DOUBLE');
insert into TagMap values ('[the standard-deviation of STAB-LAB-DATA]','[]LabData/RLA3/STAB-LAB-DATA-SQC/standardDeviation','DOUBLE');
insert into TagMap values ('[the standard-deviation of c9-lab-data]','[]LabData/RLA3/C9-LAB-DATA-SQC/standardDeviation','DOUBLE');
insert into TagMap values ('[the target of A-BALER-TEMP-LAB-DATA]','[]LabData/VFU/A-BALER-TEMP-LAB-DATA-SQC/target','DOUBLE');
insert into TagMap values ('[the target of AB-BALER-TEMP-LAB-DATA]','[]LabData/AB-BALER-TEMP/target','DOUBLE');
insert into TagMap values ('[the target of AB-BALER-VOL-ftnir-DATA]','[]LabData/VFU/AB-BALER-VOL-FTNIR-DATA-SQC/target','DOUBLE');
insert into TagMap values ('[the target of AB-BALER-VOL-lab-DATA]','[]LabData/VFU/AB-BALER-VOL-LAB-DATA-SQC/target','DOUBLE');
insert into TagMap values ('[the target of B-BALER-TEMP-LAB-DATA]','[]LabData/VFU/B-BALER-TEMP-LAB-DATA-SQC/target','DOUBLE');
insert into TagMap values ('[the target of C-BALER-TEMP-LAB-DATA]','[]LabData/VFU/C-BALER-TEMP-LAB-DATA-SQC/target','DOUBLE');
insert into TagMap values ('[the target of C2-LAB-DATA]','[]LabData/RLA3/C2-LAB-DATA-SQC/target','DOUBLE');
insert into TagMap values ('[the target of CA-LAB-DATA]','[]LabData/RLA3/CA-LAB-DATA-SQC/target','DOUBLE');
insert into TagMap values ('[the target of CD-BALER-VOL-ftnir-DATA]','[]LabData/VFU/CD-BALER-VOL-FTNIR-DATA-SQC/target','DOUBLE');
insert into TagMap values ('[the target of CD-BALER-VOL-lab-DATA]','[]LabData/VFU/CD-BALER-VOL-LAB-DATA-SQC/target','DOUBLE');
insert into TagMap values ('[the target of D-BALER-TEMP-LAB-DATA]','[]LabData/VFU/D-BALER-TEMP-LAB-DATA-SQC/target','DOUBLE');
insert into TagMap values ('[the target of DC2-LAB-DATA]','[]LabData/RLA3/DC2-LAB-DATA-SQC/target','DOUBLE');
insert into TagMap values ('[the target of DML-LAB-DATA]','[]LabData/RLA3/DML-LAB-DATA-SQC/target','DOUBLE');
insert into TagMap values ('[the target of Dc9-lab-data]','[]LabData/RLA3/DC9-LAB-DATA-SQC/target','DOUBLE');
insert into TagMap values ('[the target of E-BALER-TEMP-LAB-DATA]','[]LabData/VFU/E-BALER-TEMP-LAB-DATA-SQC/target','DOUBLE');
insert into TagMap values ('[the target of E-BALER-VOL-ftnir-DATA]','[]LabData/VFU/E-BALER-VOL-FTNIR-DATA-SQC/target','DOUBLE');
insert into TagMap values ('[the target of E-BALER-VOL-lab-DATA]','[]LabData/VFU/E-BALER-VOL-LAB-DATA-SQC/target','DOUBLE');
insert into TagMap values ('[the target of MLR-LAB-DATA]','[]LabData/RLA3/MLR-LAB-DATA-SQC/target','DOUBLE');
insert into TagMap values ('[the target of MOONEY-LAB-DATA]','[]LabData/RLA3/MOONEY-LAB-DATA-SQC/target','DOUBLE');
insert into TagMap values ('[the target of OIL-LAB-DATA]','[]LabData/RLA3/OIL-LAB-DATA-SQC/target','DOUBLE');
insert into TagMap values ('[the target of POLYSPLIT-DATA]','[]LabData/RLA3/POLYSPLIT-DATA-SQC/target','DOUBLE');
insert into TagMap values ('[the target of PROD-ML-LAB-DATA]','[]LabData/RLA3/PROD-ML-LAB-DATA-SQC/target','DOUBLE');
insert into TagMap values ('[the target of STAB-LAB-DATA]','[]LabData/RLA3/STAB-LAB-DATA-SQC/target','DOUBLE');
insert into TagMap values ('[the target of c9-lab-data]','[]LabData/RLA3/C9-LAB-DATA-SQC/target','DOUBLE');
insert into TagMap values ('[the time-of-most-recent-grade-change of rla3-run-hours]','[]Site/RLA3/Grade/timeOfMostRecentGradeChange','INTEGER');
insert into TagMap values ('[the time-of-most-recent-recommendation-implementation of frnt_short_use_temp-gda]','[]Site/CRX/FRNT-SHORT-USE-TEMP-IMP-TIME','INTEGER');
insert into TagMap values ('[the top-message-text of dr-oc-alert-msg]','[]DR/OC-ALERT-MSG','STRING');
insert into TagMap values ('[the _result of heat-soak-timeout]','[]DR/HEAT-SOAK-TIMEOUT/result','BOOLEAN');
insert into TagMap values ('[the upper_limit of MOONEY-LAB-DATA]','[]LabData/RLA3/MOONEY-LAB-DATA/upperLimit','DOUBLE');
insert into TagMap values ('ca_filtered_value','[]LabData/RLA3/CA-LAB-DATA/filteredValue','DOUBLE');
insert into TagMap values ('cast-disposition-entry','[]DiagnosticToolkit/CSTR/VFS009ME/value','DOUBLE');
insert into TagMap values ('cs-recipe-downloaded','[]Vistalon/RLA3/CS-RECIPE-DOWNLOADED','BOOLEAN');
insert into TagMap values ('cs-prestick-checklist-complete','[]Vistalon/RLA3/CS-PRESTICK-CHECKLIST-COMPLETE','BOOLEAN');
insert into TagMap values ('cs-sqc-gains-reviewed','[]Vistalon/RLA3/CS-SQC-GAINS-REVIEWED','BOOLEAN');
insert into TagMap values ('cs-sqc-limits-reviewed','[]Vistalon/RLA3/CS-SQC-LIMITS-REVIEWED','BOOLEAN');
insert into TagMap values ('d20-service-status','[]DiagnosticToolkit/CSTR/VFS000ME/value','DOUBLE');
insert into TagMap values ('d20a-service-status','[]DiagnosticToolkit/CSTR/VFS100ME/value','DOUBLE');
insert into TagMap values ('d20b-service-status','[]DiagnosticToolkit/CSTR/VFS200ME/value','DOUBLE');
insert into TagMap values ('dc2_filtered_value','[]LabData/RLA3/DC2-LAB-DATA/filteredValue','DOUBLE');
insert into TagMap values ('denb_filtered_value','[]LabData/RLA3/DENB-LAB-DATA/filteredValue','DOUBLE');
insert into TagMap values ('dml_filtered_value','[]LabData/RLA3/DML-LAB-DATA/filteredValue','DOUBLE');
insert into TagMap values ('enb_filtered_value','[]LabData/RLA3/ENB-LAB-DATA/filteredValue','DOUBLE');
insert into TagMap values ('ethylene_filtered_value','[]LabData/RLA3/ETHYLENE-LAB-DATA/filteredValue','DOUBLE');
insert into TagMap values ('mlr_filtered_value','[]LabData/RLA3/MLR-LAB-DATA/filteredValue','DOUBLE');
insert into TagMap values ('mooney_filtered_value','[]LabData/RLA3/MOONEY-LAB-DATA/filteredValue','DOUBLE');
insert into TagMap values ('oc-alert-start-exe','[]DiagnosticToolkit/ocAlert/oc-Alert-Start','BOOLEAN');
insert into TagMap values ('oil_filtered_value','[]LabData/RLA3/OIL-LAB-DATA/filteredValue','DOUBLE');
insert into TagMap values ('polysplit_filtered_value','[]LabData/RLA3/POLYSPLIT-LAB-DATA/filteredValue','DOUBLE');
insert into TagMap values ('s88-rc-main-c2-rate-ramper','[]Site/S88-RC-MAIN/C2-RATE-RAMPER','DOUBLE');
insert into TagMap values ('s88-rc-r2-c2-rate-ramper','[]Site/S88-RC-MAIN/C2-R2-RAMPER','DOUBLE');
insert into TagMap values ('s88-rc-poly-rate','[]Site/RC/POLY-RATE','BOOLEAN');
insert into TagMap values ('s88-rx-rcp-alkyl-and-sweep','[]Site/S88-RX-RCP/ALKYL-SWEEP','DOUBLE');
insert into TagMap values ('s88-rx-rcp-alkyl-conc','[]Site/S88-RX-RCP/ALKYL-CONC','DOUBLE');
insert into TagMap values ('s88-rx-rcp-ca-efficiency','[]Site/S88-RX-RCP/CA-EFFICIENCY','DOUBLE');
insert into TagMap values ('s88-rx-rcp-cast-conc','[]Site/S88-RX-RCP/CAST-CONC','DOUBLE');
insert into TagMap values ('s88-rx-rcp-cat2d901','[]Site/S88-RX-RCP/CAT2D901','DOUBLE');
insert into TagMap values ('s88-rx-rcp-c3strm_c2','[]Site/S88-RX-RCP/C3STRM-C2','DOUBLE');
insert into TagMap values ('s88-rx-rcp-c3strm_c2o','[]Site/S88-RX-RCP/C3STRM-C2O','DOUBLE');
insert into TagMap values ('s88-rx-rcp-c3strm_c3','[]Site/S88-RX-RCP/C3STRM-C3','DOUBLE');
insert into TagMap values ('s88-rx-rcp-c3strm_c3o','[]Site/S88-RX-RCP/C3STRM-C3O','DOUBLE');
insert into TagMap values ('s88-rx-rcp-crx-mix-tee-flow','[]Site/S88-RX-RCP/CRX-MIX-TEE-FLOW','DOUBLE');
insert into TagMap values ('s88-rx-rcp-etoh-conc','[]Site/S88-RX-RCP/ETOH-CONC','DOUBLE');
insert into TagMap values ('s88-rx-rcp-etoh-sweep','[]Site/S88-RX-RCP/ETOH-SWEEP','DOUBLE');
insert into TagMap values ('s88-rx-rcp-mw-alykl','[]Site/S88-RX-RCP/MW-ALYKL','DOUBLE');
insert into TagMap values ('s88-rx-rcp-mw-van','[]Site/S88-RX-RCP/MW-VAN','DOUBLE');
insert into TagMap values ('s88-rx-rcp-nh3-sweep','[]Site/S88-RX-RCP/NH3-SWEEP','DOUBLE');
insert into TagMap values ('s88-rx-rcp-oil-flag','[]Site/S88-RX-RCP/OIL-FLAG','BOOLEAN');
insert into TagMap values ('s88-rx-rcp-r1-heat-of-rx','[]Site/S88-RX-RCP/R1-HEAT-OF-RX','DOUBLE');
insert into TagMap values ('s88-rx-rcp-ss1-nozzle','[]Site/S88-RX-RCP/SS1-NOZZLE','DOUBLE');
insert into TagMap values ('s88-rx-rcp-ss2-nozzle','[]Site/S88-RX-RCP/SS2-NOZZLE','DOUBLE');
insert into TagMap values ('s88-rx-rcp-ss3-nozzle','[]Site/S88-RX-RCP/SS3-NOZZLE','DOUBLE');
insert into TagMap values ('s88-rx-rcp-ss4-nozzle','[]Site/S88-RX-RCP/SS4-NOZZLE','DOUBLE');
insert into TagMap values ('s88-rx-rcp-ss5-nozzle','[]Site/S88-RX-RCP/SS5-NOZZLE','DOUBLE');
insert into TagMap values ('s88-rx-rcp-ss6-nozzle','[]Site/S88-RX-RCP/SS6-NOZZLE','DOUBLE');
insert into TagMap values ('s88-rx-rcp-van-sweep','[]Site/S88-RX-RCP/VAN-SWEEP','DOUBLE');
insert into TagMap values ('s88-rx-rcp-vocl3-sweep','[]Site/S88-RX-RCP/VOCL3-SWEEP','DOUBLE');

insert into TagMap values ('split_feed_grade','[]Site/CSTR/SPLIT-FEED_GRADE','BOOLEAN');
insert into TagMap values ('stab_filtered_value','[]LabData/RLA3/STAB-LAB-DATA/filteredValue','DOUBLE');



-- Miscellaneous Named Objects
insert into TagMap values ('the em-sqc-plot upon the subworkspace of toolkit-console','[]Site/Plots/SqcPlotName','STRING');
insert into TagMap values ('ML_LAB_DATA_SAMPLE_TIME','[]LabData/RLA3/MOONEY-LAB-DATA/sampleTime','STRING');
