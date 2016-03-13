package frc.team3223.robot2016;

import edu.wpi.first.wpilibj.*;
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
    private int simpleDriveReverseButton = 8;
    private int simpleDriveResetButton = 8;
    private int rotateToAngleButton = 9;
    private int polarDriveButton = 10;
    private int aimAssistButton = 11;
    private int recordButton = 6;
    private int resetEncoderButton = 10;

    private int leftShooterChannel = 4;
    private int rightShooterChannel = 5;
    private int leftRaiseShooterChannel = 8;
    private int rightRaiseShooterChannel = 7;
    private int tailChannel = 9;

    private Talon leftShooterTalon;
    private Talon rightShooterTalon;
    private Talon leftRaiseShooterTalon;
    private Talon rightRaiseShooterTalon;

    private SpeedController tailMotor;
    private SensorManager sensorManager;

    public RobotConfiguration(NetworkTable networkTable){
        this.networkTable = networkTable;
        initTalons();
        initJoysticks();
        initShooter();
        initButtonPublishers();
        tailMotor = Registrar.victor(tailChannel);
        sensorManager = new SensorManager();
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
        networkTable.putString("right_" + shootButton, "fire");
        networkTable.putString("left_" + slurpButton, "get ball");
        networkTable.putString("right_" + simpleDriveReverseButton, "reverse tank drive");
        networkTable.putString("right_" + recordButton, "start/stop recording");
        networkTable.putString("right_" + resetEncoderButton, "reset shooter encoder");
        networkTable.putString("left_" + shooterUpButton, "aim up");
        networkTable.putString("left_" + shooterDownButton, "aim down");
        networkTable.putString("left_" + aimAssistButton, "aim assist drive mode (untested)");
        networkTable.putString("left_" + rotateToAngleButton, "rotate to angle drive mode (untested)");
        networkTable.putString("left_" + polarDriveButton, "polar fc tank drive mode (untested)");
        networkTable.putString("left_" + simpleDriveResetButton, "reset to tank drive mode");
    }


    public boolean shouldShoot() {
        return rightJoystick.getRawButton(shootButton);
    }

    public boolean shouldSlurp() {
        return leftJoystick.getRawButton(slurpButton);
    }

    public boolean shouldAimUp(){
        return leftJoystick.getRawButton(shooterUpButton);
    }

    public boolean testShouldAimUpLeft(){
        return leftJoystick.getRawButton(shooterUpButton);
    }

    public boolean testShouldAimUpRight(){
        return rightJoystick.getRawButton(shooterUpButton);
    }

    public boolean testShouldAimDownLeft(){
        return leftJoystick.getRawButton(shooterDownButton);
    }

    public boolean testShouldAimDownRight(){
        return rightJoystick.getRawButton(shooterDownButton);
    }

    public boolean testShouldOffBearingLeft(){
        return leftJoystick.getRawButton(3);
    }

    public boolean testShouldOffBearingRight(){
        return rightJoystick.getRawButton(3);
    }

    public boolean testShouldStayLeft(){
        return leftJoystick.getRawButton(10);
    }

    public boolean testShouldStayRight(){
        return rightJoystick.getRawButton(10);
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
        this.leftShooterTalon = Registrar.talon(leftShooterChannel);
        this.rightShooterTalon = Registrar.talon(rightShooterChannel);
        this.leftRaiseShooterTalon = Registrar.talon(leftRaiseShooterChannel);
        this.rightRaiseShooterTalon = Registrar.talon(rightRaiseShooterChannel);
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
        return talons.get(2);
    }

    @Override
    public Talon getRearLeftTalon() {
        return talons.get(3);
    }

    @Override
    public Talon getFrontRightTalon() {
        return talons.get(0);
    }

    @Override
    public Talon getRearRightTalon() {
        return talons.get(1);
    }

    public Talon getLeftShooterTalon() {
        return leftShooterTalon;
    }

    public Talon getRightShooterTalon() {
        return rightShooterTalon;
    }

    public Talon getLeftRaiseShooterTalon() {
        return leftRaiseShooterTalon;
    }

    public Talon getRightRaiseShooterTalon() {
        return rightRaiseShooterTalon;
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


    public ToggleButton makePolarDriveToggle() {
        return new ToggleButton(getLeftJoystick(), polarDriveButton);
    }

    public ToggleButton makeSimpleDriveResetToggle() {
        return new ToggleButton(getLeftJoystick(), simpleDriveResetButton);
    }

    public ToggleButton makeSimpleDriveReverseToggle() {
        return new ToggleButton(getRightJoystick(), simpleDriveReverseButton);
    }

    public double getShooterPitch() {
        int encoderValue = sensorManager.getShooterRaiserEncoder().get();
        return encoderValue;
    }

    public SensorManager getSensorManager() {
        return sensorManager;
    }

    public SpeedController getTailMotor() {return tailMotor;}

    public ToggleButton makeRecordToggle() {


        return new ToggleButton(getRightJoystick(), recordButton);
    }

    public boolean shouldResetEncoder() {
        return (getRightJoystick().getRawButton(resetEncoderButton));
    }

    public ToggleButton make180Toggle() {
        return new ToggleButton(getRightJoystick(), 11);
    }
}
