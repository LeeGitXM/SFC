-- These are class-dependent UI attributes for classes implemented in Python
-- Columns are:  className, Key, Value  for the block descriptors.
--    keys: blockStyle,editorClass,iconPath,label,nameDisplayed,nameOffsetX,nameOffsetY,tooltip,tabName,
--          viewBackgroundColor,viewBlockIcon,viewFontSize,viewHeight,viewIcon,viewLabel,viewWidth
-- Columns are:  className, shape,embedded icon,embedded text, icon path, text size, width, height 
insert into BltPythonPrototypes values ('xom.block.action.Action','blockstyle','SQUARE');
insert into BltPythonPrototypes values ('xom.block.action.Action','viewIcon','Block/icons/embedded/gear.png');
insert into BltPythonPrototypes values ('xom.block.action.Action','viewFontSize','24');
insert into BltPythonPrototypes values ('xom.block.action.Action','viewHeight','70');
insert into BltPythonPrototypes values ('xom.block.action.Action','viewWidth','70');

insert into BltPythonPrototypes values ('xom.block.arithmetic.Arithmetic','blockstyle','SQUARE');
insert into BltPythonPrototypes values ('xom.block.arithmetic.Arithmetic','viewIcon','Block/icons/embedded/fx.png');
insert into BltPythonPrototypes values ('xom.block.arithmetic.Arithmetic','viewFontSize','24');
insert into BltPythonPrototypes values ('xom.block.arithmetic.Arithmetic','viewHeight','70');
insert into BltPythonPrototypes values ('xom.block.arithmetic.Arithmetic','viewWidth','70');

insert into BltPythonPrototypes values ('xom.block.finaldiagnosis.FinalDiagnosis','blockstyle','SQUARE');
insert into BltPythonPrototypes values ('xom.block.finaldiagnosis.FinalDiagnosis','editorClass','com.ils.blt.designer.config.FinalDiagnosisConfiguration');
insert into BltPythonPrototypes values ('xom.block.finaldiagnosis.FinalDiagnosis','transmitEnabled','true');
insert into BltPythonPrototypes values ('xom.block.finaldiagnosis.FinalDiagnosis','viewBackgroundColor','16580350');
insert into BltPythonPrototypes values ('xom.block.finaldiagnosis.FinalDiagnosis','viewLabel','Final Diagnosis');
insert into BltPythonPrototypes values ('xom.block.finaldiagnosis.FinalDiagnosis','viewFontSize','24');
insert into BltPythonPrototypes values ('xom.block.finaldiagnosis.FinalDiagnosis','viewHeight','80');
insert into BltPythonPrototypes values ('xom.block.finaldiagnosis.FinalDiagnosis','viewWidth','100');

insert into BltPythonPrototypes values ('xom.block.sqcdiagnosis.SQCDiagnosis','blockstyle','SQUARE');
insert into BltPythonPrototypes values ('xom.block.sqcdiagnosis.SQCDiagnosis','viewLabel','SQC Diagnosis');
insert into BltPythonPrototypes values ('xom.block.sqcdiagnosis.SQCDiagnosis','viewFontSize','24');
insert into BltPythonPrototypes values ('xom.block.sqcdiagnosis.SQCDiagnosis','viewHeight','80');
insert into BltPythonPrototypes values ('xom.block.sqcdiagnosis.SQCDiagnosis','viewWidth','100');
insert into BltPythonPrototypes values ('xom.block.subdiagnosis.SubDiagnosis','blockstyle','SQUARE');
insert into BltPythonPrototypes values ('xom.block.subdiagnosis.SubDiagnosis','viewLabel','Sub Diagnosis');
insert into BltPythonPrototypes values ('xom.block.subdiagnosis.SubDiagnosis','viewFontSize','24');
insert into BltPythonPrototypes values ('xom.block.subdiagnosis.SubDiagnosis','viewHeight','80');
insert into BltPythonPrototypes values ('xom.block.subdiagnosis.SubDiagnosis','viewWidth','100');

-- Define anchors for possibly dangling connections (Python only?)
-- Columns are: IgnitionClass,Port,ConnectionType,Direction,Annotation
insert into BltPythonAnchorMap values('xom.block.action.Action','in','TRUTHVALUE','INCOMING','');
insert into BltPythonAnchorMap values('xom.block.action.Action','out','TRUTHVALUE','OUTGOING','');
insert into BltPythonAnchorMap values('xom.block.arithmetic.Arithmetic','in','DATA','INCOMING','');
insert into BltPythonAnchorMap values('xom.block.arithmetic.Arithmetic','out','DATA','OUTGOING','');
insert into BltPythonAnchorMap values('xom.block.finaldiagnosis.FinalDiagnosis','in','TRUTHVALUE','INCOMING','');
insert into BltPythonAnchorMap values('xom.block.finaldiagnosis.FinalDiagnosis','out','TRUTHVALUE','OUTGOING','');
insert into BltPythonAnchorMap values('xom.block.finaldiagnosis.FinalDiagnosis','diagnosis','TEXT','OUTGOING','');
insert into BltPythonAnchorMap values('xom.block.sqcdagnosis.SQCDiagnosis','in','TRUTHVALUE','INCOMING','');
insert into BltPythonAnchorMap values('xom.block.sqcdiagnosis.SQCDiagnosis','out','TRUTHVALUE','OUTGOING','');
insert into BltPythonAnchorMap values('xom.block.sqcdiagnosis.SQCDiagnosis','diagnosis','TEXT','OUTGOING','');
insert into BltPythonAnchorMap values('xom.block.subdagnosis.SubDiagnosis','in','TRUTHVALUE','INCOMING','');
insert into BltPythonAnchorMap values('xom.block.subdiagnosis.SubDiagnosis','out','SIGNAL','OUTGOING','');

-- Columns are:  className, propertyName, propertyType, editable
insert into BltPythonBlockProperties values ('xom.block.action.Action','Script','SCRIPTREF',1);
insert into BltPythonBlockProperties values ('xom.block.action.Action','Trigger','BOOLEAN',1);
insert into BltPythonBlockProperties values ('xom.block.arithmetic.Arithmetic','Function','STRING',1);
--- NOTE: For FinalDiagnosis, only the Label is a block property. Many others are aux (database) resident.
insert into BltPythonBlockProperties values ('xom.block.finaldiagnosis.FinalDiagnosis','Label','STRING',1);

insert into BltPythonBlockProperties values ('xom.block.sqcdiagnosis.SQCDiagnosis','Label','STRING',1);
insert into BltPythonBlockProperties values ('xom.block.subdiagnosis.SubDiagnosis','Label','STRING',1);
