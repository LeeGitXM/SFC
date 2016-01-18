create table Units (
  name        [varchar](64)    not null,
  isBaseUnit    [bit]    not null,
  type        [varchar](64)    not null,
  description    [varchar](2000)    not null,
  m        [float](53) default 0,
  b        [float](53) default 0,
)
GO
 
create table UnitAliases (
  alias        [varchar](64)    not null,
  name        [varchar](64)    not null,
)

GO
create table SfcControlPanelMsg (
  id [varchar](36) PRIMARY KEY,
  chartRunId [varchar](36)   not null,
  message [varchar](256)    not null,
  createTime    [datetime]  not null,
  ackRequired[bit]    not null,
  ackTimedOut[bit]  default 0  not null,
  ackTime    [datetime]    null,
)
GO
CREATE INDEX idx_control_msgs on SfcControlPanelMsg (chartRunId)
GO

CREATE TABLE SfcControlPanel(
controlPanelId int IDENTITY(1,1) NOT NULL,
controlPanelName varchar(900) NOT NULL,
chartPath varchar(900) NOT NULL,
chartRunId varchar(900) NULL,
operation varchar(900) NULL,
msgQueue varchar(900) NULL,
originator varchar(900) NULL,
project varchar(900) NULL,
isolationMode bit NULL,
enablePause bit NULL,
enableResume bit NULL,
enableCancel bit NULL,
CONSTRAINT PK_SfcControlPanel PRIMARY KEY CLUSTERED (controlPanelId)
);

CREATE UNIQUE NONCLUSTERED INDEX idx_controlPanelName ON SfcControlPanel (controlPanelName);

CREATE TABLE SfcWindow(
windowId varchar(900) NOT NULL,
controlPanelId int NOT NULL,
type varchar(900) NOT NULL,
buttonLabel varchar(900) NOT NULL,
position varchar(900) NOT NULL,
scale float NOT NULL,
title varchar(900) NOT NULL,
CONSTRAINT PK_SfcWindow PRIMARY KEY CLUSTERED (controlPanelId),
foreign key (controlPanelId) references SfcControlPanel(windowId)
);

CREATE TABLE SfcDialogMsg(
windowId varchar(900) NOT NULL,
message varchar(900) NOT NULL,
ackRequired bit NOT NULL,
CONSTRAINT PK_SfcDialogMsg PRIMARY KEY CLUSTERED (windowId),
foreign key (windowId) references SfcWindow(windowId)
);

CREATE TABLE SfcInput(
windowId varchar(900) NOT NULL,
prompt varchar(900) NOT NULL,
lowLimit float NULL,
highLimit float NULL,
CONSTRAINT PK_SfcInput PRIMARY KEY CLUSTERED (windowId),
foreign key (windowId) references SfcWindow(windowId)
);

CREATE TABLE SfcInputChoices(
windowId varchar(900) NOT NULL,
choice varchar(900) NOT NULL,
foreign key (windowId) references SfcInput(windowId)
);

CREATE INDEX idx_SfcInputChoices ON SfcInputChoices(windowId);

CREATE TABLE SfcManualDataEntry(
windowId varchar(900) NOT NULL,
requireAllInputs bit NOT NULL,
CONSTRAINT PK_SfcManualDataEntry PRIMARY KEY CLUSTERED (windowId),
foreign key (windowId) references SfcWindow(windowId)
);


CREATE TABLE SfcManualDataEntryTable(
windowId varchar(900) NOT NULL,
description varchar(900) NOT NULL,
value varchar(900) NOT NULL,
units varchar(900) NOT NULL,
dataKey varchar(900) NOT NULL,
destination varchar(900) NOT NULL,
type varchar(900) NOT NULL,
recipeUnits varchar(900) NOT NULL,
rowNum int NOT NULL,
lowLimit float NULL,
highLimit float NULL,
foreign key (windowId) references SfcManualDataEntry(windowId)
);

CREATE INDEX idx_SfcManualDataEntryTable ON SfcManualDataEntryTable(windowId);

CREATE TABLE SfcReviewData(
windowId varchar(900) NOT NULL,
showAdvice bit NOT NULL,
CONSTRAINT PK_SfcManualDataEntryTable PRIMARY KEY CLUSTERED (windowId),
foreign key (windowId) references SfcWindow(windowId)
);


CREATE TABLE dbo.SfcReviewDataTable(
windowId varchar(900) NOT NULL,
rowNum int NOT NULL,
data varchar(900) NULL,
value float NULL,
units varchar(900) NULL,
isPrimary bit NOT NULL,
advice varchar(900) NULL,
foreign key (windowId) references SfcReviewData(windowId)
);

CREATE INDEX idx_SfcReviewDataTable ON SfcReviewDataTable(windowId);

CREATE TABLE SfcReviewFlows(
windowId varchar(900) NOT NULL,
heading1 varchar(900) NOT NULL,
heading2 varchar(900) NOT NULL,
heading3 varchar(900) NOT NULL,
CONSTRAINT PK_SfcReviewFlows PRIMARY KEY CLUSTERED (windowId),
foreign key (windowId) references SfcWindow(windowId)
);

CREATE TABLE SfcReviewFlowsTable(
windowId varchar(900) NOT NULL,
rowNum int NOT NULL,
advice varchar(900) NOT NULL,
units varchar(900) NOT NULL,
prompt varchar(900) NOT NULL,
sumFlows bit NOT NULL,
data1 float NOT NULL,
data2 float NOT NULL,
data3 float NOT NULL,
foreign key (windowId) references SfcReviewFlows(windowId)
)

CREATE INDEX idx_SfcReviewFlowsTable ON SfcReviewFlowsTable(windowId);

CREATE TABLE SfcSaveData(
windowId varchar(900) NOT NULL,
text varchar(900) NOT NULL,
printText bit NOT NULL,
viewText bit NOT NULL,
filePath varchar(900) NULL,
CONSTRAINT PK_SfcSaveData PRIMARY KEY CLUSTERED (windowId),
foreign key (windowId) references SfcWindow(windowId)
);

CREATE TABLE dbo.SfcTimeDelayNotification(
windowId varchar(900) NOT NULL,
message varchar(900) NULL,
endTime float NOT NULL,
CONSTRAINT PK_SfcTimeDelayNotification PRIMARY KEY CLUSTERED (windowId),
foreign key (windowId) references SfcWindow(windowId)
);
