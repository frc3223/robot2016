package frc.team3223.robot2016;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.tables.ITable;
import edu.wpi.first.wpilibj.tables.ITableListener;

public class Shooter implements ITableListener, PIDOutput {

  public double slurpSpeed = 1;
  public double slurpDirection = -1;
  public double shootSpeed = 1;
  public double shootDirection = -1;
  public double arm_stay_speed = 0.5;

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
  boolean shouldHoldShooterUp;
  boolean hasDesiredPitch = false;
  String desiredPitchName;
  int pitchState = 0;
  int pitchPublishIncrement = 0;

  private long stateStartTime;

  ShootStateMachine shootStateMachine;

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
    this.shootStateMachine = new ShootStateMachine(this, conf);
    this.shouldHoldShooterUp = false;
    /*
     * this.raiseController = new PIDController(kP, kI, kD, kF,
     * conf.getSensorManager().getShooterRaiserEncoder(), this);
     * this.raiseController.setOutputRange(-1.0, 1.0); this.raiseController.setContinuous(true);
     * this.raiseController.setSetpoint(raiseSetpoint); this.raiseController.enable();
     */
  }

  public ShootStateMachine getShootStateMachine() {
    return this.shootStateMachine;
  }

  public void makePIDController() {
    if (this.raiseController != null) {
      this.raiseController.disable();
    }
    this.raiseController =
        new PIDController(kP, kI, kD, kF, conf.getSensorManager().getShooterRaiserEncoder(), this);
    this.raiseController.setOutputRange(-1.0, 1.0);
    this.raiseController.setContinuous(true);
    this.raiseController.setSetpoint(raiseSetpoint);
    this.raiseController.enable();
  }

  public void teleopPeriodic() {
    shootStateMachine.periodic();
    if (conf.shouldAimUp()) {
      raiseShooter();
    } else if (conf.shouldAimDown()) {
      lowerShooter();
    } else if(shouldHoldShooterUp) {
      holdRaiser();
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

  public void stopShooterLeft() {
    conf.getLeftShooterTalon().set(0.);
  }

  public void stopShooterRight() {
    conf.getRightShooterTalon().set(0.);
  }

  public void stopShooter() {
    stopShooterLeft();
    stopShooterRight();
  }

  public void stopTongue() {
    conf.getTongueMotor().set(0.);
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
  public void valueChanged(ITable table, String name, Object value, boolean isNew) {
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
    networkTable.putString("shoot_state", state.toString());
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
  }

  public void stopRaiserLeft() {
    conf.getLeftRaiseShooterTalon().set(0);
  }

  public void rotateTongue() {
    conf.getTongueMotor().set(1);
  }

  public void stopRaiser() {
    stopRaiserLeft();
    stopRaiserRight();
  }

  public void holdRaiserLeft() {
    conf.getLeftRaiseShooterTalon().set(getArmPitchStaySpeed());
  }

  public void holdRaiserRight() {
    conf.getRightRaiseShooterTalon().set(-getArmPitchStaySpeed());
  }

  public void holdRaiser() {
    holdRaiserLeft();
    holdRaiserRight();
  }

  @Override
  public void pidWrite(double output) {
    conf.getRightRaiseShooterTalon().set(output);
    conf.getLeftRaiseShooterTalon().set(-output);

  }


  public void toggleHoldMode() {
    shouldHoldShooterUp = !shouldHoldShooterUp;
  }
}
