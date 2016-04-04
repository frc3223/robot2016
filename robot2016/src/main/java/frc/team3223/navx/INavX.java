package frc.team3223.navx;

import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.interfaces.Gyro;

// don't know why AHRS doesn't implement Gyro itself..
public interface INavX extends Gyro, PIDSource {
  float getYaw();

  float getPitch();

  float getRoll();

  float getWorldLinearAccelX();

  float getWorldLinearAccelY();

  float getWorldLinearAccelZ();

  float getVelocityX();

  float getVelocityY();

  float getVelocityZ();

  float getDisplacementX();

  float getDisplacementY();

  float getDisplacementZ();

  float getFusedHeading();

  double getAngle();
}
