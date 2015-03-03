package com.ils.sfc.common.step;

public class OperationStepDelegate extends AbstractIlsStepDelegate implements
OperationStepProperties {

	public OperationStepDelegate() {
		super(properties);
	}
	
	@Override
	public String getId() {
		return OperationStepProperties.FACTORY_ID;
	}

}
