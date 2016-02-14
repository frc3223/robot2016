package frc.team3223.robot2016;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.tables.ITable;
import edu.wpi.first.wpilibj.tables.ITableListener;
import jaci.openrio.toast.lib.registry.Registrar;

public class Shooter implements ITableListener {

    private Joystick shootJoystick;
    private int shootButton;
    private Joystick slurpJoystick;
    private int slurpButton;
    private Talon leftShooterTalon;
    private Talon rightShooterTalon;

    public double slurpSpeed = 0.25;
    public double slurpDirection = 1;
    public double shootSpeed = 1;
    public double shootDirection = -1;


    public Shooter(Joystick shootJoystick, int shootButton,
                   Joystick slurpJoystick, int slurpButton ) {

        this.shootJoystick = shootJoystick;
        this.shootButton = shootButton;
        this.slurpJoystick = slurpJoystick;
        this.slurpButton = slurpButton;
        this.leftShooterTalon = Registrar.talon(4);
        this.rightShooterTalon = Registrar.talon(5);
    }

    public void teleopPeriodic() {
        if (shouldShoot()) {
            shoot();
        } else if (shouldSlurp()) {
            slurp();
        } else {
            stop();
        }
    }

    public boolean shouldShoot() {
        return shootJoystick.getRawButton(shootButton);
    }

    public boolean shouldSlurp() {
        return slurpJoystick.getRawButton(slurpButton);
    }

    public void shoot() {
        leftShooterTalon.set(getShootSpeed());
        rightShooterTalon.set(-getShootSpeed());

    }

    public void slurp() {
        leftShooterTalon.set(getSlurpSpeed());
        rightShooterTalon.set(-getSlurpSpeed());
    }

    public void stop() {
        rightShooterTalon.set(0.);
        leftShooterTalon.set(0.);
    }

    public double getShootSpeed() {
        return Math.copySign(shootSpeed, shootDirection);
    }

    public double getSlurpSpeed() {
        return Math.copySign(slurpSpeed, slurpDirection);

    }

    @Override
    public void valueChanged(ITable table,
                             String name, Object value, boolean isNew) {
        switch(name) {
            case "slurpSpeed": {
                double dvalue = (double) value;
                slurpSpeed = Math.min(Math.abs(dvalue), 1.0);
                break;
            }
            case "shootSpeed": {
                double dvalue = (double) value;
                slurpSpeed = Math.min(Math.abs(dvalue), 1.0);
                break;
            }
        }
    }
}
