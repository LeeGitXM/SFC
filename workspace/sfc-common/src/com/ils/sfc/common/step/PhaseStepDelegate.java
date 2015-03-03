package com.ils.sfc.common.step;

public class PhaseStepDelegate extends AbstractIlsStepDelegate implements PhaseStepProperties {

	public PhaseStepDelegate() {
		super(properties);
	}
	
	@Override
	public String getId() {
		return PhaseStepProperties.FACTORY_ID;
	}

}
