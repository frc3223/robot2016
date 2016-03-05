package frc.team3223.robot2016;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.tables.ITable;
import edu.wpi.first.wpilibj.tables.ITableListener;

public class Shooter implements ITableListener {
    public static enum State {
        IDLE, SLURPING, SHOOTING_INIT, SHOOTING_TAIL_OUT, SHOOTING_TAIL_IN, SLURP_INIT, SHOOTING_TAIL_IN_TO_STOP
    }

    private State state = State.IDLE;

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public void setStateAndStart(State state, long currentTime) {
        setState(state);
        this.stateStartTime = currentTime;
    }

    public double slurpSpeed = .4;
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
    double tail_out_dir = -1;
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

    private long stateStartTime;

    public Shooter(RobotConfiguration conf, NetworkTable networkTable) {

        this.conf = conf;
        this.networkTable = networkTable;
    }

    public void teleopPeriodic() {
        publishValues();
        long currentTime = System.currentTimeMillis();
        long tailOscillationTime = 800;
        long tailRetractTime = 500;

        switch (this.getState()) {
            case IDLE:
                stopShooter();
                stopTail();

                if (conf.shouldSlurp()) {
                    this.setStateAndStart(State.SLURP_INIT, currentTime);
                }
                if(conf.shouldShoot()) {
                    this.setStateAndStart(State.SHOOTING_INIT, currentTime);
                }
                break;
            case SLURP_INIT:
                stopShooter();
                tailIn();

                if (currentTime - stateStartTime > tailRetractTime) {
                //if (conf.isTailRetracted()) {
                    this.setStateAndStart(State.SLURPING, currentTime);
                }
                break;
            case SLURPING:
                slurp();
                tailIn();

                if(!conf.shouldSlurp()) {
                    this.setStateAndStart(State.IDLE, currentTime);
                }
                break;
            case SHOOTING_INIT:
                shoot();
                tailIn();

                if(!conf.shouldShoot()) {
                    this.setStateAndStart(State.IDLE, currentTime);
                }else if (currentTime - stateStartTime > tailRetractTime) {
                //}else if (conf.isTailRetracted()) {
                    this.setStateAndStart(State.SHOOTING_TAIL_OUT, currentTime);
                }
                break;
            case SHOOTING_TAIL_OUT:
                shoot();
                tailOut();

                if(!conf.shouldShoot()) {
                    this.setStateAndStart(State.SHOOTING_TAIL_IN_TO_STOP, currentTime);
                } else if(currentTime - stateStartTime > tailOscillationTime) {
                //} else if(conf.isTailExtended()) {
                    this.setStateAndStart(State.SHOOTING_TAIL_IN, currentTime);
                }
                break;
            case SHOOTING_TAIL_IN:
                shoot();
                tailIn();

                if(!conf.shouldShoot()) {
                    this.setStateAndStart(State.SHOOTING_TAIL_IN_TO_STOP, currentTime);
                }else if(currentTime - stateStartTime > tailOscillationTime) {
                //}else if (conf.isTailRetracted()) {
                    this.setStateAndStart(State.SHOOTING_TAIL_OUT, currentTime);
                }
                break;
            case SHOOTING_TAIL_IN_TO_STOP:
                stopShooter();
                tailIn();
                //if (conf.isTailRetracted()) {
                if(currentTime - stateStartTime > tailOscillationTime) {
                    this.setStateAndStart(State.IDLE, currentTime);
                }
                break;
        }

        double angleThreshold = 3.0;
        // convention: getShooterPitch returns zero when arm is level, positive value
        // as arm goes up.

        if (conf.shouldAimUp()) {
            raiseShooter();
            noDesiredPitch();
        } else if (conf.shouldAimDown()) {
            lowerShooter();
            noDesiredPitch();
        } else {
            stopRaiser();
        }
    }

    public void shoot() {
        conf.getLeftShooterTalon().set(getShootSpeed());
        conf.getRightShooterTalon().set(getShootSpeed());
    }

    public void tailOut() {
        if(conf.isTailExtended()) {
            stopTail();
        }else {
            conf.getTailMotor().set(getTailOutSpeed());
        }
    }

    public void tailIn() {
        if(conf.isTailRetracted()) {
            stopTail();
        }else{
            conf.getTailMotor().set(getTailInSpeed());
        }
    }

    public void slurp() {
        conf.getLeftShooterTalon().set(getSlurpSpeed());
        conf.getRightShooterTalon().set(getSlurpSpeed());
        conf.getRollerTalon().set(getRollerSlurpSpeed());
    }

    public void stopShooter() {
        conf.getRightShooterTalon().set(0.);
        conf.getLeftShooterTalon().set(0.);
        conf.getRollerTalon().set(0.);
    }

    public void stopTail() {
        conf.getTailMotor().set(0.);
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
        switch (name) {
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
                if (value.equals("bottom")) {
                    hasDesiredPitch = true;
                    desiredPitch = bottomPosition;
                    desiredPitchName = (String) value;
                } else if (value.equals("pos1")) {
                    hasDesiredPitch = true;
                    desiredPitch = position1;
                    desiredPitchName = (String) value;
                } else if (value.equals("pos2")) {
                    hasDesiredPitch = true;
                    desiredPitch = position2;
                    desiredPitchName = (String) value;
                } else {
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
        networkTable.putBoolean("tailExtendedLimit", conf.isTailExtended());
        networkTable.putBoolean("tailRetractedLimit", conf.isTailRetracted());
    }

    public void publishShooterPosition() {
        pitchPublishIncrement++;
        // publish once every half second or so
        if (pitchPublishIncrement % 30 == 1) {
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
//        System.out.println("Stopping raise");
        conf.getRightWindowMotorTalon().set(0.);
        conf.getLeftWindowMotorTalon().set(0.);
//        System.out.println("Stopped raise");
    }


}
