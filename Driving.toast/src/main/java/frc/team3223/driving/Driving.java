package frc.team3223.driving;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.SafePWM;
import frc.team3223.navx.NavX;
import frc.team3223.driving.Pair;
import jaci.openrio.toast.lib.module.IterativeModule;
import jaci.openrio.toast.lib.log.Logger;
import jaci.openrio.toast.lib.registry.Registrar;
import java.lang.Math;

public class Driving extends IterativeModule {

    public static Logger logger;
    NetworkTable networkTable;
    AHRS navX;

    Joystick joyPower;
    Joystick joyDirection;

    SafePWM motorLeft;
    SafePWM motorRight;

    double θInit;

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
        motorLeft = Registrar.talon(0);
        motorRight = Registrar.talon(1);

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
        final float directionX = joyDirection.getX();
        final float directionY = joyDirection.getY();

        final double direction = Math.toDegrees(Math.atan2(directionX, directionY));
        final float powerRaw = Math.hypot(directionX, directionY);
        final float power = Math.min(powerRaw, 1.0);

        final Pair<Float, Float> speed = tankSpeeds(power, direction, navX.getAngle());

        final float speedLeft = speed.fst;
        final float speedRight = speed.snd;

        motorLeft.setSpeed(speedLeft);
        motorLeft.feed();
        motorRight.setSpeed(speedRight);
        motorRight.feed();
    }

    public static Pair<Float, Float> tankSpeeds(final float power,
            final double θDesired, final double θCurrent,
            final double θSmoothThreshold) {
        final double θRelative = (θDesired - θCurrent + 180) % 360 - 180;
        final double θ = Math.copySign(
            (Math.abs(θRelative) <= θSmoothThreshold) ? θSmoothThreshold : 180,
            θRelative);
        return θToThrust(θ, power);
    }

    public static Pair<Float, Float> θToThrust(final double θ, final float power) {
        return new Pair<>(
            Math.max(-1, Math.min(1, r*drivingTriangle(θ+135))),
            Math.max(-1, Math.min(1, r*drivingTriangle(θ-135))),
        );
    }

    public static double drivingTriangle(final double θ) {
        final double c = theta/360;
        return 8 * abs(c - floor(c + 0.5)) - 2
    }
}
