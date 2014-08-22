package com.inductiveautomation.sdk.examples.sfc;

import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.gateway.model.AbstractGatewayModuleHook;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.ignition.gateway.services.ModuleServiceConsumer;
import com.inductiveautomation.sfc.api.ChartManagerService;

public class SfcExampleGatewayHook extends AbstractGatewayModuleHook implements ModuleServiceConsumer {

    private final ExampleStepFactory exampleStepFactory = new ExampleStepFactory();

    private volatile ChartManagerService chartManager;
    private volatile GatewayContext context;

    @Override
    public void setup(GatewayContext context) {
        this.context = context;

        context.getModuleServicesManager().subscribe(ChartManagerService.class, this);
    }

    @Override
    public void startup(LicenseState licenseState) {

    }

    @Override
    public void shutdown() {

    }

    @Override
    public void serviceReady(Class<?> serviceClass) {
        if (serviceClass == ChartManagerService.class) {
            chartManager = context.getModuleServicesManager().getService(ChartManagerService.class);

            chartManager.register(exampleStepFactory);
        }
    }

    @Override
    public void serviceShutdown(Class<?> serviceClass) {
        if (serviceClass == ChartManagerService.class) {
            chartManager.unregister(exampleStepFactory);

            chartManager = null;
        }
    }
}
