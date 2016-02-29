package frc.team3223.core;

import javax.naming.OperationNotSupportedException;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.SPI;

public class PhysNavX implements NavX {
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
    
    public PhysNavX() {
      PhysNavX.navX();
    }

    @Override
    public void calibrate() {
      // handled in reset()
      return;
    }

    @Override
    public void reset() {
      navX.reset();
    }

    @Override
    public double getAngle() {
      return navX.getAngle();
    }

    @Override
    public double getRate() {
      return navX.getRate();
    }

    @Override
    public void free() {
      navX.free();
    }

    @Override
    public void setRange(Range range) {
      throw Util.sneakyThrow(new OperationNotSupportedException());
    }

    @Override
    public double getX() {
      return navX.getRawAccelX();
    }

    @Override
    public double getY() {
      return navX.getRawAccelY();
    }

    @Override
    public double getZ() {
      return navX.getRawAccelZ();
    }
}
