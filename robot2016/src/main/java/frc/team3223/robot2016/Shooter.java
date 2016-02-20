package frc.team3223.robot2016;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.tables.ITable;
import edu.wpi.first.wpilibj.tables.ITableListener;
import jaci.openrio.toast.lib.registry.Registrar;

public class Shooter implements ITableListener {


    private Talon leftShooterTalon;
    private Talon rightShooterTalon;
    private Talon rollerTalon;
    private Talon leftWindowMotorTalon;
    private Talon rightWindowMotorTalon;

    public double slurpSpeed = .78;
    public double slurpDirection = 1;
    public double shootSpeed = 1;
    public double shootDirection = -1;

    double arm_pitch_up_speed = 1.00;
    double arm_pitch_up_dir = -1;
    double arm_pitch_down_speed = 0.25;
    double arm_pitch_down_dir = 1;

    double arm_roller_out_speed = 1;
    double arm_roller_out_dir = -1;
    double arm_roller_in_speed = 1;
    double arm_roller_in_dir = 1;
    private RobotConfiguration configuration;


    public Shooter(RobotConfiguration configuration) {

       /*this.shootJoystick = shootJoystick;
        this.shootButton = shootButton;
        this.slurpJoystick = slurpJoystick;
        this.slurpButton = slurpButton;
        this.shooterControlStick = shooterControlStick;
        this.shooterUpButton = shooterUpButton;
        this.shooterDownButton = shooterDownButton;
        this.leftShooterTalon = Registrar.talon(4);
        this.rightShooterTalon = Registrar.talon(5);
        this.rollerTalon = Registrar.talon(6);
        this.leftWindowMotorTalon = Registrar.talon(7);
        this.rightWindowMotorTalon = Registrar.talon(8);
        */
        this.configuration = configuration;
    }

    public void teleopPeriodic() {
        if (configuration.shouldShoot()) {
            shoot();
        } else if (configuration.shouldSlurp()) {
            slurp();
		} else if (configuration.shouldAimUp()){
			raiseShooter();
		} else if (configuration.shouldAimDown()){
			lowerShooter();
		} else {
            stop();
        }
    }

	public boolean shouldMoveShooter(){
        return false;
	}

    public void shoot() {
        leftShooterTalon.set(getShootSpeed());
        rightShooterTalon.set(-getShootSpeed());

    }

    public void slurp() {
        leftShooterTalon.set(getSlurpSpeed());
        rightShooterTalon.set(-getSlurpSpeed());
        rollerTalon.set(getRollerSlurpSpeed());
    }

    public void stop() {
        rightShooterTalon.set(0.);
        leftShooterTalon.set(0.);
        rollerTalon.set(0.);
    }

    public double getShootSpeed() {
        return Math.copySign(shootSpeed, shootDirection);
    }

    public double getSlurpSpeed() {
        return Math.copySign(slurpSpeed, slurpDirection);

    }

    public double getRollerSlurpSpeed() {
        return Math.copySign(arm_roller_in_speed, arm_roller_in_dir);
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

    public void publishValues() {
        System.out.println("yup publishing values");
        /*
        networkTable.putNumber("arm_pitch_up_speed", getArmPitchUpSpeed());
        networkTable.putNumber("arm_pitch_down_speed", getArmPitchDownSpeed());
        networkTable.putNumber("arm_roller_out_speed", getArmRollerOutSpeed());
        networkTable.putNumber("arm_roller_in_speed", getArmRollerInSpeed());
        */
        System.out.println("yup didn't actually do that");
    }

    public void raiseShooter() {
        leftWindowMotorTalon.set(.25);
        rightWindowMotorTalon.set(-.25);
    }

    public void lowerShooter() {
        leftWindowMotorTalon.set(-.25);
        rightWindowMotorTalon.set(.25);
    }
}
