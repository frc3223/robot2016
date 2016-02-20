package frc.team3223.robot2016;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Talon;
//import edu.wpi.first.wpilibj.
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import frc.team3223.drive.ISpeedControllerProvider;
import jaci.openrio.toast.lib.registry.Registrar;

import java.util.ArrayList;
import java.util.Iterator;

public class RobotConfiguration implements ISpeedControllerProvider {

    ArrayList<Talon> talons;
    NetworkTable networkTable;
    Joystick leftJoystick;
    Joystick rightJoystick;

    private int shootButton = 3;
    private int slurpButton = 3;
    private int shooterUpButton = 4;
    private int shooterDownButton = 5;

    public RobotConfiguration(NetworkTable networkTable){
        this.networkTable = networkTable;
        initTalons();
        initJoysticks();
    }

    public void publishJoystickConfiguration(){
        /* assigning joystick buttons to names to be dispalyed in the dashboard
         * left = leftJoystick : right = rightJoystick
         */
        networkTable.putString("left_" + shootButton, "fire");
        networkTable.putString("right_" + slurpButton, "get ball");
        networkTable.putString("left_" + shooterUpButton, "aim up");
        networkTable.putString("left_" + shooterDownButton, "aim down");
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

}
