package frc.team3223.navx;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.SPI;
import jaci.openrio.toast.lib.module.ToastModule;

public class NavX extends ToastModule {

    private static AHRS navX;

    @Override
    public String getModuleName() {
        return "NavX";
    }

    @Override
    public String getModuleVersion() {
        return "0.0.1";
    }

    @Override
    public void prestart() { }

    @Override
    public void start() { }

    /**
     * Return the NavX instance, creating it if necessary
     */
    public static AHRS navX() {
        if (navX == null) {
            navX = new AHRS(SPI.Port.kMXP);
        }
        return navX;
    }
}
