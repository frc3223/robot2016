package frc.team3223.core;

import jaci.openrio.toast.lib.module.ToastModule;

public class Core extends ToastModule {

    @Override
    public String getModuleName() {
        return "Core";
    }

    @Override
    public String getModuleVersion() {
        return "0.0.1";
    }
    
    @Override
    public void prestart() { }

    @Override
    public void start() { }
}
