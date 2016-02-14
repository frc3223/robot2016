package frc.team3223.robot2016;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.tables.ITable;
import edu.wpi.first.wpilibj.tables.ITableListener;
import frc.team3223.navx.INavX;
import frc.team3223.navx.NavXRegistrar;
import frc.team3223.polardrive.PolarTankDrive;
import frc.team3223.util.ToggleButton;
import jaci.openrio.toast.core.ToastBootstrap;
import jaci.openrio.toast.lib.log.Logger;
import jaci.openrio.toast.lib.module.IterativeModule;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import jaci.openrio.toast.lib.registry.Registrar;

import java.util.ArrayList;

public class RobotModule extends IterativeModule implements ITableListener {

    public static Logger logger;

    NetworkTable networkTable;
    Talon[] talons;
    Joystick leftJoystick;
    Joystick rightJoystick;
    RobotDrive drive;
    PolarTankDrive ptDrive;
    INavX navX;

    Shooter shooter;
    ArrayList<ToggleButton> toggleButtons;
    RotateToAngle rotateToAngle;
    boolean shouldRotate = false;

    double arm_pitch_up_speed = 1.00;
    double arm_pitch_up_dir = -1;
    double arm_pitch_down_speed = 0.25;
    double arm_pitch_down_dir = 1;

    double arm_roller_out_speed = 0.25;
    double arm_roller_out_dir = -1;
    double arm_roller_in_speed = 0.25;
    double arm_roller_in_dir = 1;

    boolean normalJoystickOrientation;

    double desiredHeading = 0.00;
    DriveMode driveMode = DriveMode.SimpleTank;
    DriveMode lastDriveMode = DriveMode.SimpleTank;

    @Override
    public String getModuleName() {
        return "robot2016";
    }

    @Override
    public String getModuleVersion() {
        return "0.0.1";
    }

    @Override
    public void robotInit() {
        logger = new Logger("robot2016", Logger.ATTR_DEFAULT);
        networkTable = NetworkTable.getTable("SmartDashboard");
        networkTable.addTableListener(this);
        toggleButtons = new ArrayList<>();

        initTalons();
        initJoysticks();
        initSensors();
        initShooter();
        initSimpleDrive();
        initRotateToAngle();
        initPolarDrive();

    }

    private void initShooter() {
        shooter = new Shooter(leftJoystick, 3, rightJoystick, 3);
    }

    private void initJoysticks() {
        leftJoystick = new Joystick(0);
        rightJoystick = new Joystick(1);
    }

    private void initSensors() {
        navX = NavXRegistrar.navX();
    }

    private void initSimpleDrive() {
        drive = new RobotDrive(
                getFrontLeftTalon(), getRearLeftTalon(),
                getFrontRightTalon(), getRearRightTalon());
        normalJoystickOrientation = true;
        toggleButtons.add(new ToggleButton(leftJoystick, 4)
                .onToggleOn(x -> {
                    normalJoystickOrientation = !normalJoystickOrientation;
                }));
    }

    private void initTalons() {
        talons = new Talon[6];
        for(int j = 0; j < talons.length; j ++) {
            talons[j] = Registrar.talon(j);
        }
    }

    private void initRotateToAngle() {
        rotateToAngle = new RotateToAngle(navX);
        toggleButtons.add(new ToggleButton(leftJoystick, 9)
                .onToggleOn(x -> {
                    rotateToAngle.enable();
                    lastDriveMode = driveMode;
                    driveMode = DriveMode.RotateToAngle;
                })
                .onToggleOff(x -> {
                    rotateToAngle.disable();
                    driveMode = lastDriveMode;
                }));
    }

    private void initPolarDrive() {
        ptDrive = new PolarTankDrive(navX);
        ptDrive.addLeftMotor(getFrontLeftTalon());
        ptDrive.addLeftMotor(getRearLeftTalon());
        ptDrive.addRightMotor(getFrontRightTalon());
        ptDrive.addRightMotor(getRearRightTalon());
        toggleButtons.add(new ToggleButton(leftJoystick, 5)
                .onToggleOn(x -> {
                    if(driveMode == DriveMode.PolarFCTank) {
                        driveMode = lastDriveMode;
                    }else{
                        lastDriveMode = driveMode;
                        driveMode = DriveMode.PolarFCTank;
                    }
                }));

    }

    public Talon getFrontLeftTalon() {
        return talons[0];
    }

    public Talon getRearLeftTalon() {
        return talons[1];
    }

    public Talon getFrontRightTalon() {
        return talons[2];
    }

    public Talon getRearRightTalon() {
        return talons[3];
    }

    public static boolean isReal() {
        return !ToastBootstrap.isSimulation;
    }

    @Override
    public void autonomousInit() {
        publishValues();
    }

    @Override public void teleopInit() {
    }

    @Override
    public void teleopPeriodic() {
        pushNavx();
        toggleButtons.forEach(tb -> tb.teleopPeriodic());
        shooter.teleopPeriodic();
        switch(driveMode) {
            case SimpleTank:
                drive();
                break;
            case RotateToAngle:
                rotate();
                break;
            case PolarFCTank:
                break;
        }
    }

    public void rotate() {
        double left = rotateToAngle.getRotateToAngleRate();
        double right = -left;
        networkTable.putNumber("left", left);
        networkTable.putNumber("right", right);
        drive.tankDrive(left, right);
    }

    public void pushNavx() {
        if(isReal()) {
            networkTable.putNumber("angle", navX.getAngle());
            networkTable.putNumber("dangle", navX.getRate());
            networkTable.putNumber("yaw", navX.getYaw());
            networkTable.putNumber("pitch", navX.getPitch());
            networkTable.putNumber("roll", navX.getRoll());
        }
    }

    public void armPitch() {
        if(leftJoystick.getRawButton(3)) {
            talons[4].set(getArmPitchUpSpeed());
        }else if(rightJoystick.getRawButton(3)) {
            talons[4].set(getArmPitchDownSpeed());
        }else {
            talons[4].set(0.);
        }
    }

    public void armRoller() {
        if(leftJoystick.getRawButton(2)) {
            talons[5].set(getArmRollerOutSpeed());
        }else if(rightJoystick.getRawButton(2)) {
            talons[5].set(getArmRollerInSpeed());
        }else {
            talons[5].set(0.);
        }
    }

    public void drive() {
        double n = 0.85;
        double leftvalue = leftJoystick.getAxis(Joystick.AxisType.kY) * n;
        double rightvalue = rightJoystick.getAxis(Joystick.AxisType.kY) * n;
        if( Math.abs(leftvalue - rightvalue) <= 0.5)
        {
            leftvalue = rightvalue;
        }
        if(!normalJoystickOrientation){
            leftvalue=-leftvalue;
            rightvalue=-rightvalue;
        }
        drive.tankDrive(leftvalue, rightvalue, true);
    }

    @Override
    public void valueChanged(ITable table,
            String name, Object value, boolean isNew) {
        System.out.println("received " + name + ": " + value);
        switch(name) {
            case "arm_pitch_up_speed": {
                double dvalue = (double) value;
                arm_pitch_up_speed = Math.min(Math.abs(dvalue), 1.0);
                break;
            }
            case "arm_pitch_down_speed": {
                double dvalue = (double) value;
                arm_pitch_down_speed = Math.min(Math.abs(dvalue), 1.0);
                break;
            }
            case "arm_roller_out_speed": {
                double dvalue = (double) value;
                arm_roller_out_speed = Math.min(Math.abs(dvalue), 1.0);
                break;
            }
            case "arm_roller_in_speed": {
                double dvalue = (double) value;
                arm_roller_out_speed = Math.min(Math.abs(dvalue), 1.0);
                break;
            }
        }
    }

    public double getArmPitchUpSpeed() {
        return Math.copySign(arm_pitch_up_speed, arm_pitch_up_dir);
    }

    public double getArmPitchDownSpeed() {
        return Math.copySign(arm_pitch_down_speed, arm_pitch_down_dir);
    }

    public double getArmRollerOutSpeed() {
        return Math.copySign(arm_roller_out_speed, arm_roller_out_dir);
    }
    public double getArmRollerInSpeed() {
        return Math.copySign(arm_roller_in_speed, arm_roller_in_dir);
    }

    public void publishValues() {
        System.out.println("yup publishing values");
        networkTable.putNumber("arm_pitch_up_speed", getArmPitchUpSpeed());
        networkTable.putNumber("arm_pitch_down_speed", getArmPitchDownSpeed());
        networkTable.putNumber("arm_roller_out_speed", getArmRollerOutSpeed());
        networkTable.putNumber("arm_roller_in_speed", getArmRollerInSpeed());
    }

}
