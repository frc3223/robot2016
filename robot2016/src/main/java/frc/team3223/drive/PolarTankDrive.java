package frc.team3223.drive;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.MotorSafety;
import edu.wpi.first.wpilibj.MotorSafetyHelper;
import edu.wpi.first.wpilibj.interfaces.Gyro;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import frc.team3223.util.Pair;
import jaci.openrio.toast.lib.log.Logger;

import java.util.function.Supplier;

/**
 * Polar Tank Drive does several things:
 * 1. translate polar radius and angle to tank drive thrusts
 *    (algorithm stolen from http://robotics.stackexchange.com/questions/2011/how-to-calculate-the-right-and-left-speed-for-a-tank-like-rover/2016#2016)
 * 2. translate joystick inputs to polar radius and angle
 * 3. Field Centric control - e.g. push down on joystick means
 *    robot drives toward the operator end of the arena
 * 4. Turn threshold - when the robot's heading is within this
 *    distance of the desired heading, turn robot while moving forward,
 *    otherwise rotate the robot in place.
 */
public class PolarTankDrive implements IDrive, MotorSafety {

    public static Logger logger;
    protected MotorSafetyHelper m_safetyHelper;
    private NetworkTable networkTable;
    private Gyro gyro;
    private ISpeedControllerProvider driveProvider;

    private Joystick directionJoystick;
    private Supplier<Double> thrustAxis;

    /**
     * err, what's this?
     */
    private double thetaInitial;

    /**
     * If we are within this threshold (in degrees) from the desired heading,
     * drive forward and turn rather than rotate in place.
     */
    private double rotateThreshold;
    public final static double kDefaultExpirationTime = 0.1;

    public PolarTankDrive(Gyro gyro, ISpeedControllerProvider driveProvider, NetworkTable networkTable) {
        this.gyro = gyro;
        this.driveProvider = driveProvider;
        this.networkTable = networkTable;
        this.rotateThreshold = 10;
        setupMotorSafety();
        disable();
    }

    private void setupMotorSafety() {
        m_safetyHelper = new MotorSafetyHelper(this);
        m_safetyHelper.setExpiration(kDefaultExpirationTime);
        m_safetyHelper.setSafetyEnabled(true);
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

        if(Math.abs(directionX) < 0.15 && Math.abs(directionY) < 0.15) {
            drive(0, 0);
        }else {
            this.networkTable.putNumber("theta", direction);
            this.networkTable.putNumber("power", power);
            drive(power, direction);
        }
    }

    public void driveSingleFieldCentric() {
        driveSingleFieldCentric(this.getDirectionJoystick());
    }

    public void driveSingleFieldCentric(Joystick joystick) {
        final double directionX = joystick.getX();
        final double directionY = joystick.getY();
        final double desiredAbsHeading = Math.toDegrees(Math.atan2(directionX, directionY));
        final double powerRaw = Math.hypot(directionX, directionY);
        final double power = Math.min(powerRaw, 1.0);

        if(Math.abs(directionX) < 0.15 && Math.abs(directionY) < 0.15) {
            drive(0, 0);
        }else {
            double desiredRelativeHeading = normalizeDegrees(desiredAbsHeading - gyro.getAngle());
            if(Math.abs(desiredRelativeHeading) > 10) {
                desiredRelativeHeading = Math.copySign(90, desiredRelativeHeading);
            }
            this.networkTable.putNumber("theta", desiredRelativeHeading);
            this.networkTable.putNumber("power", power);
            drive(power, desiredRelativeHeading);
        }

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

        driveProvider.getLeftMotors().forEachRemaining(sc -> {
            sc.setInverted(true);
            sc.set(speed.fst);
        });
        driveProvider.getRightMotors().forEachRemaining(sc -> {
            sc.setInverted(false);
            sc.set(speed.snd);
        });

        m_safetyHelper.feed();
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
        final double thetaRelative = normalizeDegrees(desiredHeading - currentHeading);
        final double heading = Math.copySign(
            (Math.abs(thetaRelative) <= rotateThreshold) ? rotateThreshold : 180,
            thetaRelative);
        return polarToThrust(heading, power);
    }

    public static double normalizeDegrees(double angle) {
        double result = (angle ) % 360 ;
        if(result < -180) {
            result += 360;
        }
        if(result >= 180) {
            result -= 360;
        }
        return result;
    }

    public static Pair<Double, Double> polarToThrust(final double theta, final double power) {
        double left = power * drivingTriangle(theta + 135);
        double right = power * drivingTriangle(theta - 135);
        return new Pair<>(
            Math.max(-1, Math.min(1, left)),
            Math.max(-1, Math.min(1, right))
        );
    }

    public static double drivingTriangle(final double theta) {
        final double c = theta / 360;
        final double sawtooth = c - Math.floor(c + 0.5);
        final double result = 8 * Math.abs(sawtooth) - 2;
        return result;
    }

    @Override
    public void enable() {
        m_safetyHelper.setSafetyEnabled(true);
    }

    @Override
    public void disable() {
        //m_safetyHelper.setSafetyEnabled(false);

    }

    @Override
    public void setExpiration(double timeout) {
        m_safetyHelper.setExpiration(timeout);

    }

    @Override
    public double getExpiration() {
        return m_safetyHelper.getExpiration();
    }

    @Override
    public boolean isAlive() {
        return m_safetyHelper.isAlive();
    }

    @Override
    public void stopMotor() {
        drive(0, 0);
    }

    @Override
    public void setSafetyEnabled(boolean enabled) {
        m_safetyHelper.setSafetyEnabled(enabled);
    }

    @Override
    public boolean isSafetyEnabled() {
        return m_safetyHelper.isSafetyEnabled();
    }

    @Override
    public String getDescription() {
        return "Polar Field Centric Tank Drive";
    }
}
