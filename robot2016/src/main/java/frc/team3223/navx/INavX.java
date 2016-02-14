package frc.team3223.navx;

import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.interfaces.Gyro;

// don't know why AHRS doesn't implement Gyro itself..
public interface INavX extends Gyro, PIDSource {
    double getYaw();
    double getPitch();
    double getRoll();
    double getAngle();
}
