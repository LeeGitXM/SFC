create table SfcSessions (
  userName[varchar](64) not null,
  chartName [varchar](64)    not null,
  chartRunId [varchar](64)   not null,
  status [varchar](16)    not null,
  operation[varchar](64)    null,
  startTime 	datetime  not null,
  lastChangeTime datetime not null
)
GO
CREATE INDEX idx_sfc_sessions on SfcSessions (operator, status)
GO

create table ControlPanelMsgs (
  id INT IDENTITY PRIMARY KEY,
  chartRunId [varchar](64)   not null,
  message [varchar](256)    not null,
  createTime    [datetime]  not null,
  ackRequired[bit]    not null,
  ackTime    [datetime]    null,
)
GO
CREATE INDEX idx_control_msgs on ControlPanelMsgs (chartRunId)
GO

create table Units (
  name        [varchar](64)    not null,
  isBaseUnit    [bit]    not null,
  type        [varchar](64)    not null,
  description    [varchar](2000)    not null,
  m        [float](53),
  b        [float](53),
)
GO
 
create table UnitAliases (
  alias        [varchar](64)    not null,
  name        [varchar](64)    not null,
)
GO


