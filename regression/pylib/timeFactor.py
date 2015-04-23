# Copyright 2015. ILS Automation. All rights reserved.
# Argument is the diagram path
import system.ils.sfc as ilssfc
def setFactor(common,factor):
	ilssfc.setTimeFactor(float(factor))
