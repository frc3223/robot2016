package frc.team3223.robot2016;

import edu.wpi.first.wpilibj.*;
//import edu.wpi.first.wpilibj.
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import frc.team3223.drive.ISpeedControllerProvider;
import frc.team3223.util.ToggleButton;
import jaci.openrio.toast.lib.registry.Registrar;

import java.util.ArrayList;
import java.util.Iterator;

public class RobotConfiguration implements ISpeedControllerProvider {

    ArrayList<Talon> talons;
    ArrayList<ToggleButton> buttonPublishers;
    NetworkTable networkTable;
    private Joystick leftJoystick;
    private Joystick rightJoystick;

    private int shootButton = 3;
    private int slurpButton = 3;
    private int shooterUpButton = 4;
    private int shooterDownButton = 5;
    private int simpleDriveButton = 8;
    private int rotateToAngleButton = 9;
    private int polarDriveButton = 10;
    private int aimAssistButton = 11;

    private Talon leftShooterTalon;
    private Talon rightShooterTalon;
    private Talon rollerTalon;
    private Talon leftWindowMotorTalon;
    private Talon rightWindowMotorTalon;

    private DigitalInput TailLimitSwitch1;
    private Spark TailSpark;



    public RobotConfiguration(NetworkTable networkTable){
        this.networkTable = networkTable;
        initTalons();
        initJoysticks();
        initShooter();
        initButtonPublishers();
        TailLimitSwitch1=Registrar.digitalInput(1);
        TailSpark=Registrar.spark(1);
    }

    private void initButtonPublishers() {
        buttonPublishers = new ArrayList<>();
        for(int i = 1; i <= 11; i++) {
            ToggleButton leftToggle = new ToggleButton(leftJoystick, i);
            leftToggle.onToggleOn((j, b) -> {
                networkTable.putBoolean("left_" + b + "_pressed", true);
            });
            leftToggle.onToggleOff((j, b) -> {
                networkTable.putBoolean("left_" + b + "_pressed", false);
            });
            buttonPublishers.add(leftToggle);
            ToggleButton rightToggle = new ToggleButton(rightJoystick, i);
            rightToggle.onToggleOn((j, b) -> {
                networkTable.putBoolean("right_" + b + "_pressed", true);
            });
            rightToggle.onToggleOff((j, b) -> {
                networkTable.putBoolean("right_" + b + "_pressed", false);
            });
            buttonPublishers.add(rightToggle);
        }
    }

    public void toggleButtonsPeriodic() {
        buttonPublishers.forEach(toggleButton -> {
            toggleButton.periodic();
        });
    }

    public void publishJoystickConfiguration(){
        /* assigning joystick buttons to names to be displayed in the dashboard
         * left = leftJoystick : right = rightJoystick
         */
        networkTable.putString("left_" + shootButton, "fire");
        networkTable.putString("right_" + slurpButton, "get ball");
        networkTable.putString("left_" + shooterUpButton, "aim up");
        networkTable.putString("left_" + shooterDownButton, "aim down");
        networkTable.putString("left_" + aimAssistButton, "aim assist drive mode (untested)");
        networkTable.putString("left_" + rotateToAngleButton, "rotate to angle drive mode (untested)");
        networkTable.putString("left_" + polarDriveButton, "polar fc tank drive mode (untested)");
        networkTable.putString("left_" + simpleDriveButton, "reset to tank drive mode");
    }


    public boolean shouldShoot() {
        return leftJoystick.getRawButton(shootButton);
    }

    public boolean shouldSlurp() {
        return rightJoystick.getRawButton(slurpButton);
    }

    public boolean shouldAimUp(){
        return leftJoystick.getRawButton(shooterUpButton);
    }

    public boolean shouldAimDown(){
        return leftJoystick.getRawButton(shooterDownButton);
    }

    public ToggleButton makeRotateToAngleToggle() {
        return new ToggleButton(getLeftJoystick(), rotateToAngleButton);
    }

    private void initJoysticks() {
        leftJoystick = new Joystick(0);
        rightJoystick = new Joystick(1);
    }


    private void initTalons() {
        int n = 6;
        talons = new ArrayList<Talon>(n);
        for(int j = 0; j < n; j ++) {
            Talon talon = Registrar.talon(j);
            talons.add(talon);
        }
    }

    public void feedumTalons() {
        talons.forEach(talon -> {
                talon.Feed();
        });
    }

    public void initShooter(){
        this.leftShooterTalon = Registrar.talon(4);
        this.rightShooterTalon = Registrar.talon(5);
        this.rollerTalon = Registrar.talon(6);
        this.leftWindowMotorTalon = Registrar.talon(7);
        this.rightWindowMotorTalon = Registrar.talon(8);
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

    public Talon getLeftShooterTalon() {
        return leftShooterTalon;
    }

    public Talon getRightShooterTalon() {
        return rightShooterTalon;
    }

    public Talon getRollerTalon() {
        return rollerTalon;
    }

    public Talon getLeftWindowMotorTalon() {
        return leftWindowMotorTalon;
    }

    public Talon getRightWindowMotorTalon() {
        return rightWindowMotorTalon;
    }

    public Joystick getLeftJoystick() {
        return leftJoystick;
    }

    public Joystick getRightJoystick() {
        return rightJoystick;
    }

    public ToggleButton makeAimAssistToggle() {
        return new ToggleButton(leftJoystick, aimAssistButton);
    }

    public boolean shouldAimAssist() {
        return leftJoystick.getRawButton(aimAssistButton);
    }

    public ToggleButton makePolarDriveToggle() {
        return new ToggleButton(getLeftJoystick(), polarDriveButton);
    }

    public ToggleButton makeSimpleDriveToggle() {
        return new ToggleButton(getLeftJoystick(), simpleDriveButton);
    }

    public DigitalInput getTailLimitSwitch1() {return TailLimitSwitch1;}

    public Spark getTailSpark() {return TailSpark;}
}
