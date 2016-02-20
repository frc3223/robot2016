package frc.team3223.robot2016;

import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Talon;
import frc.team3223.drive.ISpeedControllerProvider;
import jaci.openrio.toast.lib.registry.Registrar;

import java.util.ArrayList;
import java.util.Iterator;

public class RobotConfiguration implements ISpeedControllerProvider {

    ArrayList<Talon> talons;

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
