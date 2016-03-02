package frc.team3223.robot2016;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.tables.ITable;
import edu.wpi.first.wpilibj.tables.ITableListener;

public class Shooter implements ITableListener {
    public double slurpSpeed = .78;
    public double slurpDirection = 1;
    public double shootSpeed = 1;
    public double shootDirection = -1;

    double arm_pitch_up_speed = 1.00;
    double arm_pitch_up_dir = 1;
    double arm_pitch_down_speed = 0.25;
    double arm_pitch_down_dir = -1;

    double arm_roller_out_speed = 1;
    double arm_roller_out_dir = -1;
    double arm_roller_in_speed = 1;
    double arm_roller_in_dir = 1;

    double tail_speed = 1;
    double tail_out_dir = 1;
    private RobotConfiguration conf;
    private NetworkTable networkTable;

    boolean hasDesiredPitch = false;
    String desiredPitchName;
    int pitchState = 0;
    double desiredPitch = 0;
    int pitchPublishIncrement = 0;

    double bottomPosition = 0;
    double position1 = 10;
    double position2 = 30;

    int tailState=0;

    public Shooter(RobotConfiguration conf, NetworkTable networkTable) {

        this.conf = conf;
        this.networkTable = networkTable;
    }

    public void teleopPeriodic() {
        if (conf.shouldShoot()) {
            shoot();
        } else if (conf.shouldSlurp()) {
            slurp();
        } else {
            stopShooter();
        }

        double angleThreshold = 3.0;
        // convention: getShooterPitch returns zero when arm is level, positive value
        // as arm goes up.

        if (conf.shouldAimUp()){
			raiseShooter();
            noDesiredPitch();
		} else if (conf.shouldAimDown()){
			lowerShooter();
            noDesiredPitch();
            /*
        } else if(hasDesiredPitch) {
            if(pitchState == 0) {
                // 0: init - arm needs to go down to zero out shooter gyro
                lowerShooter();

                if (conf.getShooterDownLimitSwitch().get()) {
                    pitchState = 1;
                }
            }else if(pitchState == 1) {
                // 1: at bottom - reset shooter gyro and turn off window motors
                conf.getShooterGyro().reset();
                stopRaiser();

                if (desiredPitch > conf.getShooterPitch()) {
                    pitchState = 2;
                }
            }else if(pitchState == 2) {
                // 2: going up! hope desired pitch isn't past
                raiseShooter();

                if(desiredPitch - conf.getShooterPitch() < angleThreshold ) {
                    pitchState = 3;
                }
            }else if(conf.getShooterPitch() - desiredPitch > 3){
                lowerShooter();
            }else if(desiredPitch - conf.getShooterPitch() > 3) {
                raiseShooter();
            }else {
                stopRaiser();
            }
            */
        }else{
            stopRaiser();
        }

        /*
        if(conf.getShooterDownLimitSwitch().get()) {
            conf.getShooterGyro().reset();
        }
        tailPeriodic();
        publishShooterPosition();
        */
    }

	public boolean shouldMoveShooter(){
        return false;
	}

    public void shoot() {
        conf.getLeftShooterTalon().set(getShootSpeed());
        conf.getRightShooterTalon().set(-getShootSpeed());
    }

    public void tailPeriodic(){
        System.out.println("enter tail periodic");
        if (tailState==0) {
            System.out.println("tail in");
            conf.getTailSpark().set(getTailInSpeed());

            if (conf.shouldShoot()){
                tailState=1;
            }

        }
        else if (tailState==1){
            System.out.println("tail out");
            conf.getTailSpark().set(getTailOutSpeed());
            if (conf.getTailLimitSwitch1().get()){
                tailState=0;
            }
        }
        System.out.println("exit tail periodic");
    }

    public void slurp() {
        conf.getLeftShooterTalon().set(getSlurpSpeed());
        conf.getRightShooterTalon().set(-getSlurpSpeed());
        conf.getRollerTalon().set(getRollerSlurpSpeed());
    }

    public void stopShooter() {
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

    public double getTailOutSpeed() {
        return Math.copySign(tail_speed, tail_out_dir);
    }

    public double getTailInSpeed() {
        return Math.copySign(tail_speed, -tail_out_dir);
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
            case "tailSpeed": {
                double dvalue = (double) value;
                tail_speed = Math.min(Math.abs(dvalue), 1.0);
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
            case "shooterPosition": {
                if(value.equals("bottom")){
                    hasDesiredPitch = true;
                    desiredPitch = bottomPosition;
                    desiredPitchName = (String) value;
                }else if(value.equals("pos1")) {
                    hasDesiredPitch = true;
                    desiredPitch = position1;
                    desiredPitchName = (String) value;
                }else if(value.equals("pos2")) {
                    hasDesiredPitch = true;
                    desiredPitch = position2;
                    desiredPitchName = (String) value;
                }else{
                    noDesiredPitch();
                }

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

    public void publishShooterPosition() {
        pitchPublishIncrement++;
        // publish once every half second or so
        if(pitchPublishIncrement % 30 == 1) {
            networkTable.putString("shooterArmPitch", desiredPitchName);
            pitchPublishIncrement = 0;
        }

    }

    public void noDesiredPitch() {
        hasDesiredPitch = false;
        desiredPitchName = "none";
    }

    public void raiseShooter() {
        System.out.println("raise shooter");
        conf.getLeftWindowMotorTalon().set(getArmPitchUpSpeed());
        conf.getRightWindowMotorTalon().set(getArmPitchUpSpeed());
    }

    public void lowerShooter() {
        System.out.println("lower shooter");
        conf.getLeftWindowMotorTalon().set(getArmPitchDownSpeed());
        conf.getRightWindowMotorTalon().set(getArmPitchDownSpeed());
    }

    public void stopRaiser() {
        System.out.println("Stopping raise");
        conf.getRightWindowMotorTalon().set(0.);
        conf.getLeftWindowMotorTalon().set(0.);
        System.out.println("Stopped raise");
    }
}
