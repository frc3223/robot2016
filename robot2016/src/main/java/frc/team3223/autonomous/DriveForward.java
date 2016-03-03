package frc.team3223.autonomous;

import frc.team3223.drive.PolarTankDrive;

/**
 * Created by Samantha on 2/17/2016.
 */
public class DriveForward implements IAutonomous {
    PolarTankDrive drive;
    long time;

    public DriveForward(PolarTankDrive drive) {
        this.drive = drive;
    }

    @Override
    public void autonomousInit() {
        this.time = System.currentTimeMillis();
    }

    @Override
    public void autonomousPeriodic() {
        long now = System.currentTimeMillis();
    }

    @Override
    public void enable() {
        drive.enable();
    }

    @Override
    public void disable() {
        drive.disable();
    }
}
