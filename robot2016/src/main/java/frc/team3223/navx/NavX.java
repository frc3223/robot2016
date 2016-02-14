package frc.team3223.navx;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.SPI;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class NavX implements INavX {

    private AHRS ahrs;

    public NavX() {
        ahrs = new AHRS(SPI.Port.kMXP);
    }

    @Override
    public double getYaw() {
        return ahrs.getYaw();
    }

    @Override
    public double getPitch() {
        return ahrs.getPitch();
    }

    @Override
    public double getRoll() {
        return ahrs.getRoll();
    }

    @Override
    public void calibrate() {
        throw new NotImplementedException();
    }

    @Override
    public void reset() {
        ahrs.reset();
    }

    @Override
    public double getAngle() {
        return ahrs.getAngle();
    }

    @Override
    public double getRate() {
        return ahrs.getRate();
    }

    @Override
    public void free() {
        ahrs.free();

    }

    @Override
    public void setPIDSourceType(PIDSourceType pidSource) {
        ahrs.setPIDSourceType(pidSource);
    }

    @Override
    public PIDSourceType getPIDSourceType() {
        return ahrs.getPIDSourceType();
    }

    @Override
    public double pidGet() {
        return ahrs.pidGet();
    }
}
