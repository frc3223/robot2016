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
    public float getYaw() {
        return ahrs.getYaw();
    }

    @Override
    public float getPitch() {
        return ahrs.getPitch();
    }

    @Override
    public float getRoll() {
        return ahrs.getRoll();
    }

    @Override
    public float getWorldLinearAccelX() {
        return ahrs.getWorldLinearAccelX();
    }

    @Override
    public float getWorldLinearAccelY() {
        return ahrs.getWorldLinearAccelY();
    }

    @Override
    public float getWorldLinearAccelZ() {
       return ahrs.getWorldLinearAccelZ();
    }

    @Override
    public float getVelocityX() {
        return ahrs.getVelocityX();
    }

    @Override
    public float getVelocityY() {
        return ahrs.getVelocityY();
    }

    @Override
    public float getVelocityZ() {
        return ahrs.getVelocityZ();
    }

    @Override
    public float getDisplacementX() {
        return ahrs.getDisplacementX();
    }

    @Override
    public float getDisplacementY() {
        return ahrs.getDisplacementY();
    }

    @Override
    public float getDisplacementZ() {
        return ahrs.getDisplacementZ();
    }

    @Override
    public float getFusedHeading() {
        return ahrs.getFusedHeading();
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
