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
import jaci.openrio.toast.lib.registry.Registrar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class RobotModule extends IterativeModule implements ITableListener, ISpeedControllerProvider {

    public static Logger logger;

    NetworkTable networkTable;
    ArrayList<Talon> talons;
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

        initTalons();
        initJoysticks();
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
        shooter = new Shooter(leftJoystick, 3, rightJoystick, 3);
        networkTable.addTableListener(shooter);
    }

    private void initJoysticks() {
        leftJoystick = new Joystick(0);
        rightJoystick = new Joystick(1);
    }

    private void initSensors() {
        navX = NavXRegistrar.navX();
    }

    private void initSimpleDrive() {
        simpleDrive = new SimpleDrive(leftJoystick,rightJoystick, this, networkTable );
        toggleButtons.add(new ToggleButton(leftJoystick, 4)
                .onToggleOn(x -> {
                    simpleDrive.toggleNormalJoystickOrientation();
                }));
        driveModes.put(DriveMode.SimpleTank, simpleDrive);
    }

    private void initTalons() {
        int n = 6;
        talons = new ArrayList<Talon>(n);
        for(int j = 0; j < n; j ++) {
            Talon talon = Registrar.talon(j);
            talons.add(talon);
        }
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
        ptDrive = new PolarTankDrive(navX, this);
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

    @Override
    public Talon getFrontLeftTalon() {
        return talons.get(0);
    }

    @Override
    public Talon getRearLeftTalon() {
        return talons.get(1);
    }

    @Override
    public Talon getFrontRightTalon() {
        return talons.get(2);
    }

    @Override
    public Talon getRearRightTalon() {
        return talons.get(3);
    }

    public static boolean isReal() {
        return !ToastBootstrap.isSimulation;
    }

    @Override
    public void autonomousInit() {
        shooter.publishValues();
    }

    @Override
    public void autonomousPeriodic() {
        talons.forEach(talon -> {
            talon.Feed();
        });

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

    @Override
    public Iterator<SpeedController> getLeftMotors() {
        ArrayList<SpeedController> grr = new ArrayList<>(2);
        grr.add(getFrontLeftTalon());
        grr.add(getRearLeftTalon());
        return grr.iterator();
    }

    @Override
    public Iterator<SpeedController> getRightMotors() {
        ArrayList<SpeedController> grr = new ArrayList<>(2);
        grr.add(getFrontRightTalon());
        grr.add(getRearRightTalon());
        return grr.iterator();
    }
}
