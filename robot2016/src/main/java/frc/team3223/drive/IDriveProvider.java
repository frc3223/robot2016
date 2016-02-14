package frc.team3223.drive;

import edu.wpi.first.wpilibj.SpeedController;

import java.util.Iterator;

public interface IDriveProvider {
    Iterator<SpeedController> getLeftMotors();
    Iterator<SpeedController> getRightMotors();

    SpeedController getFrontLeftTalon();
    SpeedController getRearLeftTalon();
    SpeedController getFrontRightTalon();
    SpeedController getRearRightTalon();
}
