# Copyright 2016 ILS Automation. All rights reserved.
# emS88InitializeContainerData.py
from com.inductiveautomation.ignition.common.util import LogUtil
from ils.sfc.gateway.util import getFullChartPath
from ils.vistalon import util
from ils.diagToolkit import finalDiagnosis
from com.ils.queue import post
from com.inductiveautomation.ignition.common.util import LoggerEx
from com.ils.sfc.python import UnitProcedure
from com.ils.sfc.python import S88State
from time import sleep
from ils.sfc.gateway import api
from ils.constants.enumerations import S88Scope
def onStart(chart,block):
   log = LogUtil.getLogger(getFullChartPath(chart))
   log.tracef("In %s with %s, a %s. . .",chart.get("name",""),block.get("name",""),"step")

   block["state"]=str(S88State.RUNNING)


   block["state"]=str(S88State.COMPLETE)
   log.tracef("%s has completed!",block.get("name",""))
