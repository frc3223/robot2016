package frc.team3223.robot2016;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.tables.ITable;
import edu.wpi.first.wpilibj.tables.ITableListener;
import frc.team3223.autonomous.DriveToHighGoal;
import frc.team3223.autonomous.IAutonomous;
import frc.team3223.drive.*;
import frc.team3223.navx.INavX;
import frc.team3223.navx.NavXRegistrar;
import frc.team3223.util.ToggleButton;
import jaci.openrio.toast.core.ToastBootstrap;
import jaci.openrio.toast.lib.log.Logger;
import jaci.openrio.toast.lib.module.IterativeModule;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RobotModule extends IterativeModule implements ITableListener {

    public static Logger logger;

    NetworkTable networkTable;
    Joystick leftJoystick;
    Joystick rightJoystick;
    DriveMode driveMode = DriveMode.SimpleTank;
    DriveMode lastDriveMode = DriveMode.SimpleTank;
    SimpleDrive simpleDrive;
    PolarTankDrive ptDrive;
    RotateToAngle rotateToAngle;
    Map<DriveMode, IDrive> driveModes;
    INavX navX;
    Map<AutonomousMode, IAutonomous> autonomousModes;
    DriveToHighGoal driveToHighGoal;
    AutonomousMode currentAutonomousMode;
    RobotConfiguration conf;

    Shooter shooter;
    ArrayList<ToggleButton> toggleButtons;
    boolean shouldRotate = false;

    double desiredHeading = 0.00;

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
        driveModes = new HashMap<>();
        autonomousModes = new HashMap<>();
        currentAutonomousMode = AutonomousMode.DriveToHighGoal;

        conf = new RobotConfiguration(networkTable);




        initSensors();
        initShooter();
        initSimpleDrive();
        initRotateToAngle();
        initPolarDrive();
        initDriveToHighGoal();

    }

    private void initDriveToHighGoal() {
        this.driveToHighGoal = new DriveToHighGoal(1, ptDrive, 1);
        driveToHighGoal.disable();
        autonomousModes.put(AutonomousMode.DriveToHighGoal, driveToHighGoal);
    }

    private void initShooter() {
        shooter = new Shooter(conf);
        networkTable.addTableListener(shooter);
    }

    private void initSensors() {
        navX = NavXRegistrar.navX();
    }

    private void initSimpleDrive() {
        simpleDrive = new SimpleDrive(leftJoystick,rightJoystick, this.conf, networkTable );
        toggleButtons.add(new ToggleButton(leftJoystick, 8)
                .onToggleOn(x -> {
                    simpleDrive.toggleNormalJoystickOrientation();
                }));
        driveModes.put(DriveMode.SimpleTank, simpleDrive);
    }

    private void initRotateToAngle() {
        rotateToAngle = new RotateToAngle(navX, simpleDrive);
        networkTable.addTableListener(rotateToAngle);
        toggleButtons.add(new ToggleButton(leftJoystick, 9)
                .onToggleOn(x -> {
                    pushDriveMode(DriveMode.RotateToAngle);
                })
                .onToggleOff(x -> {
                    revertDriveMode();
                }));
        driveModes.put(DriveMode.RotateToAngle, rotateToAngle);
    }

    private void initPolarDrive() {
        ptDrive = new PolarTankDrive(navX, this.conf, this.networkTable);
        ptDrive.setDirectionJoystick(leftJoystick);
        toggleButtons.add(new ToggleButton(leftJoystick, 5)
                .onToggleOn(x -> {
                    if(driveMode == DriveMode.PolarFCTank) {
                        revertDriveMode();
                    }else{
                        pushDriveMode(DriveMode.PolarFCTank);
                    }
                }));
        driveModes.put(DriveMode.PolarFCTank, ptDrive);
    }

    private void revertDriveMode() {
        disableDriveModes();
        driveMode = lastDriveMode;
        driveModes.get(driveMode).enable();
    }

    private void pushDriveMode(DriveMode driveMode) {
        disableDriveModes();
        lastDriveMode = this.driveMode;
        this.driveMode = driveMode;
        driveModes.get(driveMode).enable();
    }

    private void disableDriveModes() {
        driveModes.values().forEach(dr -> {
            dr.disable();
        });
    }

    private void disableAutonomousModes() {
        autonomousModes.values().forEach(au -> {
            au.disable();
        });
    }
    public static boolean isReal() {
        return !ToastBootstrap.isSimulation;
    }

    @Override
    public void autonomousInit() {
        shooter.publishValues();
        conf.publishJoystickConfiguration();
    }

    @Override
    public void autonomousPeriodic() {

        if (currentAutonomousMode == AutonomousMode.DriveToHighGoal) {
            this.driveToHighGoal.autonomousPeriodic();
        }
    }

    @Override public void teleopInit() {
        pushDriveMode(DriveMode.PolarFCTank);
    }

    @Override
    public void teleopPeriodic() {
        pushNavx();
        toggleButtons.forEach(tb -> tb.teleopPeriodic());

        shooter.teleopPeriodic();

        switch(driveMode) {
            case SimpleTank:
                simpleDrive.drive();
                break;
            case RotateToAngle:
                rotateToAngle.rotate();
                break;
            case PolarFCTank:
                ptDrive.driveSingle();
                break;
        }

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

    @Override
    public void valueChanged(ITable table,
            String name, Object value, boolean isNew) {
        System.out.println("received " + name + ": " + value);
    }
}
