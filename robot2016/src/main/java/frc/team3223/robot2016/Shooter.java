package frc.team3223.robot2016;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.tables.ITable;
import edu.wpi.first.wpilibj.tables.ITableListener;

public class Shooter implements ITableListener, PIDOutput{

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

    public double slurpSpeed = .6;
    public double slurpDirection = -1;
    public double shootSpeed = 1;
    public double shootDirection = -1;
    public double arm_stay_speed = 0.75;

    double arm_pitch_up_speed = 1.0;
    double arm_pitch_up_dir = 1;
    double arm_pitch_down_speed = 0.45;
    double arm_pitch_down_dir = 1;
    double arm_pitch_off_speed = 0.55;
    double arm_pitch_off_dir = 1;

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
    int pitchPublishIncrement = 0;

    private long stateStartTime;

    PIDController raiseController;
    double kP = 0.03;
    double kD = 0;
    double kI = 0;
    double kF = 0;
    double raiseSetpoint = 0;

    double bottomPosition = -183;
    double breachPosition = -150;
    double topPosition = 0;

    double raiseLimit = -50;

    public Shooter(RobotConfiguration conf, NetworkTable networkTable) {

        this.conf = conf;
        this.networkTable = networkTable;
        /*
        this.raiseController = new PIDController(kP, kI, kD, kF,
                conf.getSensorManager().getShooterRaiserEncoder(), this);
        this.raiseController.setOutputRange(-1.0, 1.0);
        this.raiseController.setContinuous(true);
        this.raiseController.setSetpoint(raiseSetpoint);
        this.raiseController.enable();
        */
    }
    public void makePIDController() {
        if(this.raiseController != null) {
            this.raiseController.disable();
        }
        this.raiseController = new PIDController(kP, kI, kD, kF,
                conf.getSensorManager().getShooterRaiserEncoder(), this);
        this.raiseController.setOutputRange(-1.0, 1.0);
        this.raiseController.setContinuous(true);
        this.raiseController.setSetpoint(raiseSetpoint);
        this.raiseController.enable();
    }

    public void teleopPeriodic() {
        long currentTime = System.currentTimeMillis();
        long tailOscillationTime = 0;
        long tailRetractTime = 0;

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
                    this.setStateAndStart(State.SHOOTING_TAIL_OUT, currentTime);
                }
                break;
            case SHOOTING_TAIL_OUT:
                shoot();
                tailOut();

                if(!conf.shouldShoot()) {
                    this.setStateAndStart(State.SHOOTING_TAIL_IN_TO_STOP, currentTime);
                } else if(currentTime - stateStartTime > tailOscillationTime) {
                    this.setStateAndStart(State.SHOOTING_TAIL_IN, currentTime);
                }
                break;
            case SHOOTING_TAIL_IN:
                shoot();
                tailIn();

                if(!conf.shouldShoot()) {
                    this.setStateAndStart(State.SHOOTING_TAIL_IN_TO_STOP, currentTime);
                }else if(currentTime - stateStartTime > tailOscillationTime) {
                    this.setStateAndStart(State.SHOOTING_TAIL_OUT, currentTime);
                }
                break;
            case SHOOTING_TAIL_IN_TO_STOP:
                stopShooter();
                tailIn();
                if(currentTime - stateStartTime > tailOscillationTime) {
                    this.setStateAndStart(State.IDLE, currentTime);
                }
                break;
        }

        if (conf.shouldAimUp()) {
            raiseShooter();
        } else if (conf.shouldAimDown()) {
            lowerShooter();
        } else {
            stopRaiser();
        }
    }

    public void shootLeft() {
        conf.getLeftShooterTalon().set(getShootSpeed());
    }
    public void shootRight() {
        conf.getRightShooterTalon().set(-getShootSpeed());
    }
    public void shoot() {
        shootLeft();
        shootRight();
    }

    public void tailOut() {
        conf.getTailMotor().set(getTailOutSpeed());
    }

    public void tailIn() {
        conf.getTailMotor().set(getTailInSpeed());
    }

    public void slurpLeft() {
        conf.getLeftShooterTalon().set(-getSlurpSpeed());
    }

    public void slurpRight() {
        conf.getRightShooterTalon().set(getSlurpSpeed());
    }

    public void slurp() {
        slurpLeft();
        slurpRight();
    }

    public void stopShooter() {
        conf.getRightShooterTalon().set(0.);
        conf.getLeftShooterTalon().set(0.);
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

    public double getArmPitchUpSpeed() {
        return Math.copySign(arm_pitch_up_speed, arm_pitch_up_dir);
    }

    public double getArmPitchStaySpeed() {
        return Math.copySign(arm_stay_speed, arm_pitch_up_dir);
    }

    public double getArmPitchDownSpeed() {
        return Math.copySign(arm_pitch_down_speed, arm_pitch_down_dir);
    }

    public double getArmPitchOffSpeed() {
        return Math.copySign(arm_pitch_off_speed, arm_pitch_off_dir);
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
            case "raise_proportional": {
                kP = (double) value;
                makePIDController();
                break;
            }
            case "raise_derivative": {
                kD = (double) value;
                makePIDController();
                break;
            }
            case "raise_integral": {
                kI = (double) value;
                makePIDController();
                break;
            }
            case "shooterPosition": {
                if (value.equals("bottom")) {
                    raiseSetpoint = bottomPosition;
                    desiredPitchName = (String) value;
                } else if (value.equals("pos1")) {
                    hasDesiredPitch = true;
                    raiseSetpoint = breachPosition;
                    desiredPitchName = (String) value;
                } else if (value.equals("pos2")) {
                    hasDesiredPitch = true;
                    raiseSetpoint = topPosition;
                    desiredPitchName = (String) value;
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
    public void raiseShooterLeft() {
        conf.getLeftRaiseShooterTalon().set(getArmPitchUpSpeed());
    }

    public void raiseShooterRight() {
        conf.getRightRaiseShooterTalon().set(-getArmPitchUpSpeed());
    }

    public void raiseShooter() {
        raiseShooterLeft();
        raiseShooterRight();
    }

    public void lowerShooterLeft() {
        conf.getLeftRaiseShooterTalon().set(-getArmPitchDownSpeed());
    }

    public void lowerShooterRight() {
        conf.getRightRaiseShooterTalon().set(getArmPitchDownSpeed());
    }

    public void lowerShooter() {
        lowerShooterLeft();
        lowerShooterRight();
    }

    public void offBearingShooterLeft() {
        conf.getLeftRaiseShooterTalon().set(-getArmPitchOffSpeed());
    }

    public void offBearingShooterRight() {
        conf.getRightRaiseShooterTalon().set(getArmPitchOffSpeed());
    }

    public void offBearingShooter() {
        offBearingShooterLeft();
        offBearingShooterRight();
    }

    public void stopRaiserRight() {
        conf.getRightRaiseShooterTalon().set(0);
        //conf.getRightRaiseShooterTalon().set(getArmPitchStaySpeed());
    }

    public void stopRaiserLeft() {
        //conf.getLeftRaiseShooterTalon().set(getArmPitchStaySpeed());
        conf.getLeftRaiseShooterTalon().set(0);
    }

    public void stopRaiser() {
        stopRaiserLeft();
        stopRaiserRight();
    }

    @Override
    public void pidWrite(double output) {
        conf.getRightRaiseShooterTalon().set(output);
        conf.getLeftRaiseShooterTalon().set(-output);

    }
}
