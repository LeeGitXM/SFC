package com.ils.sfc.common.step;

public class ProcedureStepDelegate extends AbstractIlsStepDelegate implements
ProcedureStepProperties {

	public ProcedureStepDelegate() {
		super(properties);
	}
	
	@Override
	public String getId() {
		return ProcedureStepProperties.FACTORY_ID;
	}

}
