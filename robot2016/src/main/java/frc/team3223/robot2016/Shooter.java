package frc.team3223.robot2016;

import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.tables.ITable;
import edu.wpi.first.wpilibj.tables.ITableListener;

public class Shooter implements ITableListener {
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
    private RobotConfiguration conf;
    private NetworkTable networkTable;


    public Shooter(RobotConfiguration conf, NetworkTable networkTable) {

        this.conf = conf;
        this.networkTable = networkTable;
    }

    public void teleopPeriodic() {
        if (conf.shouldShoot()) {
            shoot();
        } else if (conf.shouldSlurp()) {
            slurp();
		} else if (conf.shouldAimUp()){
			raiseShooter();
		} else if (conf.shouldAimDown()){
			lowerShooter();
		} else {
            stop();
        }
    }

	public boolean shouldMoveShooter(){
        return false;
	}

    public void shoot() {
        conf.getLeftShooterTalon().set(getShootSpeed());
        conf.getRightShooterTalon().set(-getShootSpeed());

    }

    public void slurp() {
        conf.getLeftShooterTalon().set(getSlurpSpeed());
        conf.getRightShooterTalon().set(-getSlurpSpeed());
        conf.getRollerTalon().set(getRollerSlurpSpeed());
    }

    public void stop() {
        conf.getRightShooterTalon().set(0.);
        conf.getLeftShooterTalon().set(0.);
        conf.getRollerTalon().set(0.);
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
        networkTable.putNumber("arm_pitch_up_speed", getArmPitchUpSpeed());
        networkTable.putNumber("arm_pitch_down_speed", getArmPitchDownSpeed());
        networkTable.putNumber("arm_roller_out_speed", getArmRollerOutSpeed());
        networkTable.putNumber("arm_roller_in_speed", getArmRollerInSpeed());
    }

    public void raiseShooter() {
        System.out.println("raise shooter");
        conf.getLeftWindowMotorTalon().set(getArmPitchUpSpeed());
        conf.getRightWindowMotorTalon().set(-getArmPitchUpSpeed());
    }

    public void lowerShooter() {
        System.out.println("lower shooter");
        conf.getLeftWindowMotorTalon().set(getArmPitchDownSpeed());
        conf.getRightWindowMotorTalon().set(-getArmPitchDownSpeed());
    }
}
