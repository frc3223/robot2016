package frc.team3223.polardrive;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.interfaces.Gyro;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import frc.team3223.util.Pair;
import jaci.openrio.toast.lib.log.Logger;

import java.util.ArrayList;
import java.util.function.Supplier;

public class PolarTankDrive {

    public static Logger logger;
    private NetworkTable networkTable;
    private Gyro gyro;

    private Joystick directionJoystick;
    private Supplier<Double> thrustAxis;

    private ArrayList<SpeedController> rightMotors;
    private ArrayList<SpeedController> leftMotors;

    /**
     * err, what's this?
     */
    private double thetaInitial;

    /**
     * If we are within this threshold (in degrees) from the desired heading,
     * drive forward and turn rather than rotate in place.
     */
    private double rotateThreshold;

    public PolarTankDrive(Gyro gyro) {
        this.gyro = gyro;
        this.rotateThreshold = 10;
        this.rightMotors = new ArrayList<SpeedController>(2);
        this.leftMotors = new ArrayList<SpeedController>(2);
    }

    public void addRightMotor(SpeedController speedController) {
        this.rightMotors.add(speedController);
    }

    public void addLeftMotor(SpeedController speedController) {
        this.leftMotors.add(speedController);
    }

    public void takeInitialHeading() {
        thetaInitial = gyro.getAngle();
    }

    /**
     * Heading and thrust determined from
     * single joystick. call setDirectionJoystick() before calling this.
     */
    public void driveSingle() {
        driveSingle(this.getDirectionJoystick());
    }

    /**
     * Heading and thrust determined from provided joystick
     * @param joystick
     */
    public void driveSingle(Joystick joystick) {
        final double directionX = joystick.getX();
        final double directionY = joystick.getY();
        final double direction = Math.toDegrees(Math.atan2(directionX, directionY));
        final double powerRaw = Math.hypot(directionX, directionY);
        final double power = Math.min(powerRaw, 1.0);

        drive(power, direction);
    }

    /**
     * Heading and thrust determined from different joysticks.
     * call setDirectionJoystick() and setThrustAxis() before calling this.
     */
    public void driveDual() {
        driveDual(getDirectionJoystick(), getThrustAxis());
    }

    public void driveDual(Joystick joystick, Supplier<Double> thrustAxis) {
        final double directionX = joystick.getX();
        final double directionY = joystick.getY();
        final double direction = Math.toDegrees(Math.atan2(directionX, directionY));
        final double power = thrustAxis.get();

        drive(power, direction);
    }

    /**
     * Heading and thrust provided directly
     * @param heading
     * @param thrust
     */
    public void drive(double heading, double thrust) {
        final Pair<Double, Double> speed = tankSpeeds(
                thrust, heading, gyro.getAngle(), getRotateThreshold());

        leftMotors.forEach(sc -> {
            sc.setInverted(true);
            sc.set(speed.fst);
        });
        rightMotors.forEach(sc -> {
            sc.setInverted(false);
            sc.set(speed.snd);
        });
    }

    public Joystick getDirectionJoystick() {
        return directionJoystick;
    }

    public void setDirectionJoystick(Joystick joystick) {
        directionJoystick = joystick;
    }

    public Supplier<Double> getThrustAxis() {
        return thrustAxis;
    }

    public void setThrustAxis(Supplier<Double> thrustAxis){
        this.thrustAxis = thrustAxis;
    }

    public double getRotateThreshold() {
        return rotateThreshold;
    }

    public void setRotateThreshold(double rotateThreshold) {
        this.rotateThreshold = rotateThreshold;
    }

    public static Pair<Double, Double> tankSpeeds(final double power,
            final double desiredHeading, final double currentHeading,
            final double rotateThreshold) {
        final double θRelative = normalizeDegrees(desiredHeading - currentHeading);
        final double heading = Math.copySign(
            (Math.abs(θRelative) <= rotateThreshold) ? rotateThreshold : 180,
            θRelative);
        return polarToThrust(heading, power);
    }

    public static double normalizeDegrees(double angle) {
        return (angle + 180) % 360 - 180;
    }

    public static Pair<Double, Double> polarToThrust(final double θ, final double power) {
        double left = power * drivingTriangle(θ + 135);
        double right = power * drivingTriangle(θ - 135);
        return new Pair<>(
            Math.max(-1, Math.min(1, left)),
            Math.max(-1, Math.min(1, right))
        );
    }

    public static double drivingTriangle(final double theta) {
        final double c = theta / 360;
        return 8 * Math.abs(c - Math.floor(c + 0.5)) - 2;
    }
}
