package frc.team3223.autonomous;

import frc.team3223.drive.PolarTankDrive;

/**
 * Created by Samantha on 2/17/2016.
 */
public class DriveToHighGoal implements IAutonomous {
  int defenseNumber;
  PolarTankDrive drive;
  int highGoal;
  long time;

  public DriveToHighGoal(int defenseNumber, PolarTankDrive drive, int highGoal) {
    this.defenseNumber = defenseNumber;
    this.drive = drive;
    this.highGoal = highGoal;
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
