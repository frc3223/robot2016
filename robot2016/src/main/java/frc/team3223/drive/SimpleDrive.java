package frc.team3223.drive;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import frc.team3223.robot2016.RobotConfiguration;

/**
 * silly wrapper around silly RobotDrive class.
 * Mostly to make its safety helper shut up when it isn't being used.
 * But also incorporates the idea of an 'equalizer' (todo: naming) that
 * snaps the joystick inputs to the same value if they are pretty close to each other.
 * And also incorporates the idea of a zero threshold to keep the motors
 * from running a tiny amount when the user isn't doing anything with the joysticks
 */
public class SimpleDrive implements IDrive{
    private RobotDrive drive;
    private NetworkTable networkTable;

    private double maxSpeedModifier;

    /**
     * if joystick inputs are within this distance from each other,
     * the user intends them to be the same, so make that happen
     */
    private double equalizeThreshold;

    /**
     * if joystick input is within this distance to zero,
     * the user intends it to be zero, so make that happen.
     */
    private double zeroThreshold;

    /**
     * true => forward means forward and backwards means backwards
     * false => forward means backwards etc
     */
    private boolean normalJoystickOrientation;

    private Joystick leftJoystick;
    private Joystick rightJoystick;

    public SimpleDrive(RobotConfiguration conf, NetworkTable networkTable) {
        this.leftJoystick = conf.getLeftJoystick();
        this.rightJoystick = conf.getRightJoystick();
        this.networkTable = networkTable;
        drive = new RobotDrive(
                conf.getFrontLeftTalon(),
                conf.getRearLeftTalon(),
                conf.getFrontRightTalon(),
                conf.getRearRightTalon());

        setMaxSpeedModifier(0.85);
        setEqualizeThreshold(0.5);
        setZeroThreshold(0.2);
        disable();
    }

    @Override
    public void enable() {
        drive.setSafetyEnabled(true);
    }

    @Override
    public void disable() {
        drive.setSafetyEnabled(false);
    }

    public void drive() {
        double leftValue = leftJoystick.getAxis(Joystick.AxisType.kY) * getMaxSpeedModifier();
        double rightValue = rightJoystick.getAxis(Joystick.AxisType.kY) * getMaxSpeedModifier();
        if(Math.abs(leftValue) < getZeroThreshold()) {
            leftValue = 0;
        }
        if(Math.abs(rightValue) < getZeroThreshold()) {
            rightValue = 0;
        }
        if( Math.abs(leftValue - rightValue) <= 0.5)
        {
            leftValue = rightValue;
        }
        if(!normalJoystickOrientation){
            leftValue=-leftValue;
            rightValue=-rightValue;
        }

        drive(leftValue, rightValue);
    }

    public void drive(double leftValue, double rightValue) {
        if(networkTable != null) {
            networkTable.putNumber("left", leftValue);
            networkTable.putNumber("right", rightValue);
        }
        drive.tankDrive(leftValue, rightValue, true);
    }

    public double getMaxSpeedModifier() {
        return maxSpeedModifier;
    }

    public void setMaxSpeedModifier(double maxSpeedModifier) {
        this.maxSpeedModifier = maxSpeedModifier;
    }

    public void toggleNormalJoystickOrientation() {
        normalJoystickOrientation = !normalJoystickOrientation;
    }

    public double getEqualizeThreshold() {
        return equalizeThreshold;
    }

    public void setEqualizeThreshold(double equalizeThreshold) {
        this.equalizeThreshold = equalizeThreshold;
    }

    public double getZeroThreshold() {
        return zeroThreshold;
    }

    public void setZeroThreshold(double zeroThreshold) {
        this.zeroThreshold = zeroThreshold;
    }
}
