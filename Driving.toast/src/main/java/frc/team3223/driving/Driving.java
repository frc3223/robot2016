package frc.team3223.driving;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.SpeedController;
import frc.team3223.core.NavX;
import frc.team3223.core.PhysNavX;
import frc.team3223.core.MultiSpeedController;
import frc.team3223.core.Pair;
import frc.team3223.driving.PolarTank;
import jaci.openrio.toast.lib.module.IterativeModule;
import jaci.openrio.toast.lib.log.Logger;
import jaci.openrio.toast.lib.registry.Registrar;
import java.lang.Math;

public class Driving extends IterativeModule {

  public static Logger logger;
  private NetworkTable networkTable;
  private NavX navX;

  private Joystick joy;

  private SpeedController motorRight;
  private SpeedController motorLeft;

  private double thetaInit;

  @Override
  public String getModuleName() {
    return "Driving";
  }

  @Override
  public String getModuleVersion() {
    return "0.0.1";
  }

  @Override
  public void robotInit() {
    logger = new Logger("Driving", Logger.ATTR_DEFAULT);
    networkTable = NetworkTable.getTable("SmartDashboard");

    navX = new PhysNavX();
    motorRight = new MultiSpeedController().add(Registrar.talon(0)).add(Registrar.talon(1));
    motorLeft = new MultiSpeedController().add(Registrar.talon(2)).add(Registrar.talon(3));

    joy = new Joystick(0);
  }

  @Override
  public void teleopInit() {
    thetaInit = navX.getAngle();
    logger.info("Initial teleop heading: " + thetaInit);
  }

  @Override
  public void teleopPeriodic() {
    final double directionX = joy.getX();
    final double directionY = joy.getY();

    final double direction = Math.toDegrees(Math.atan2(directionX, directionY));
    final double powerRaw = Math.hypot(directionX, directionY);
    final double power = Math.min(powerRaw, 1.0);

    final Pair<Double, Double> speed = PolarTank.speeds(power, direction, navX.getAngle(), 10);

    motorLeft.set(speed.fst);
    motorRight.set(speed.snd);
  }

}
