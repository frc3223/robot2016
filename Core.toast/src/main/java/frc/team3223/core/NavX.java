package frc.team3223.core;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.SPI;
import jaci.openrio.toast.lib.module.ToastModule;

public class NavX {
    private static AHRS navX;

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
