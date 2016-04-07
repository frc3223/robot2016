package frc.team3223.autonomous;

public interface IAutonomous {
  void autonomousInit();

  void autonomousPeriodic();

  void enable();

  void disable();
}