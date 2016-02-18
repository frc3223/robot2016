package frc.team3223.driving;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.SpeedController;
import frc.team3223.core.NavX;
import frc.team3223.core.MultiSpeedController;
import frc.team3223.core.Pair;
import jaci.openrio.toast.lib.module.IterativeModule;
import jaci.openrio.toast.lib.log.Logger;
import jaci.openrio.toast.lib.registry.Registrar;
import java.lang.Math;

public class Driving extends IterativeModule {

    public static Logger logger;
    private NetworkTable networkTable;
    private AHRS navX;

    private Joystick joyPower;
    private Joystick joyDirection;

    private SpeedController motorRight;
    private SpeedController motorLeft;

    private double θInit;

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

        navX = NavX.navX();
        motorRight = new MultiSpeedController().add(Registrar.talon(0)).add(Registrar.talon(1));
        motorLeft = new MultiSpeedController().add(Registrar.talon(2)).add(Registrar.talon(3));

        joyPower = new Joystick(0);
        joyDirection = new Joystick(1);
    }

    @Override
    public void teleopInit() {
        θInit = navX.getAngle();
        logger.info("Initial teleop heading: " + θInit);
    }

    @Override
    public void teleopPeriodic() {
        final double directionX = joyDirection.getX();
        final double directionY = joyDirection.getY();

        final double direction = Math.toDegrees(Math.atan2(directionX, directionY));
        final double powerRaw = Math.hypot(directionX, directionY);
        final double power = Math.min(powerRaw, 1.0);

        final Pair<Double, Double> speed = tankSpeeds(power, direction, navX.getAngle(), 10);

        motorLeft.set(speed.fst);
        motorRight.set(speed.snd);
    }

    public static Pair<Double, Double> tankSpeeds(final double power,
            final double θDesired, final double θCurrent,
            final double θSmoothThreshold) {
        final double θRelative = (θDesired - θCurrent + 180) % 360 - 180;
        final double θ = Math.copySign(
            (Math.abs(θRelative) <= θSmoothThreshold) ? θSmoothThreshold : 180,
            θRelative);
        return θToThrust(θ, power);
    }

    public static Pair<Double, Double> θToThrust(final double θ, final double power) {
        return new Pair<>(
            Math.max(-1, Math.min(1, power * drivingTriangle(θ + 135))),
            Math.max(-1, Math.min(1, power * drivingTriangle(θ - 135)))
        );
    }

    public static double drivingTriangle(final double θ) {
        final double c = θ / 360;
        return 8 * Math.abs(c - Math.floor(c + 0.5)) - 2;
    }
}
