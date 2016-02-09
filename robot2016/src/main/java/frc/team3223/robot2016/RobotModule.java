package frc.team3223.robot2016;

import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.tables.ITable;
import edu.wpi.first.wpilibj.tables.ITableListener;
import jaci.openrio.toast.lib.log.Logger;
import jaci.openrio.toast.lib.module.IterativeModule;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import jaci.openrio.toast.lib.registry.Registrar;
//import com.kauailabs.navx.frc.AHRS;

public class RobotModule extends IterativeModule implements ITableListener {

    public static Logger logger;

    NetworkTable networkTable;
    Talon[] talons;
    Joystick leftJoystick;
    Joystick rightJoystick;
    boolean isbuttonpressed;
    RobotDrive drive;

    int i;
    double arm_pitch_up_speed = 1.00;
    double arm_pitch_up_dir = -1;
    double arm_pitch_down_speed = 0.25;
    double arm_pitch_down_dir = 1;

    double arm_roller_out_speed = 0.25;
    double arm_roller_out_dir = -1;
    double arm_roller_in_speed = 0.25;
    double arm_roller_in_dir = 1;

    boolean normaljoystickorentation;


    @Override
    public String getModuleName() {
        return "robot2016";
    }

    @Override
    public String getModuleVersion() {
        return "0.0.1";
    }

    @Override
    public void robotInit() {
        logger = new Logger("robot2016", Logger.ATTR_DEFAULT);
        networkTable = NetworkTable.getTable("SmartDashboard");
        networkTable.addTableListener(this);
        i = 0;
        talons = new Talon[6];
        for(int j = 0; j < talons.length; j ++) {
            talons[j] = Registrar.talon(j);
            talons[j].setSafetyEnabled(false);
        }
        leftJoystick = new Joystick(0);
        rightJoystick = new Joystick(1);
        isbuttonpressed = false;
        drive = new RobotDrive(talons[0], talons[1], talons[2], talons[3]);
        normaljoystickorentation = true;

    }

    @Override
    public void autonomousInit() {
        publishValues();
    }

    @Override
    public void teleopPeriodic() {
        armPitch();
        armRoller();
        drive();
    }

    public void armPitch() {
        if(leftJoystick.getRawButton(3)) {
            talons[4].set(getArmPitchUpSpeed());
        }else if(rightJoystick.getRawButton(3)) {
            talons[4].set(getArmPitchDownSpeed());
        }else {
            talons[4].set(0.);
        }
    }

    public void armRoller() {
        if(leftJoystick.getRawButton(2)) {
            talons[5].set(getArmRollerOutSpeed());
        }else if(rightJoystick.getRawButton(2)) {
            talons[5].set(getArmRollerInSpeed());
        }else {
            talons[5].set(0.);
        }
    }

    public void drive() {
        if(!isbuttonpressed && leftJoystick.getRawButton(5))
        {
            normaljoystickorentation = !normaljoystickorentation;
            isbuttonpressed = true;
        }
        if (isbuttonpressed && !leftJoystick.getRawButton(5))
        {
            isbuttonpressed = false;

        }

        double n = 0.85;
        double leftvalue = leftJoystick.getAxis(Joystick.AxisType.kY) * n;
        double rightvalue = rightJoystick.getAxis(Joystick.AxisType.kY) * n;
        if( Math.abs(leftvalue - rightvalue) <= 0.5)
        {
            leftvalue = rightvalue;
        }
        if(!normaljoystickorentation){
            leftvalue=-leftvalue;
            rightvalue=-rightvalue;
        }
        drive.tankDrive(leftvalue, rightvalue, true);
    }

    @Override
    public void valueChanged(ITable table,
            String name, Object value, boolean isNew) {
        System.out.println("received " + name + ": " + value);
        switch(name) {
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

    public void publishValues() {
        System.out.println("yup publishing values");
        networkTable.putNumber("arm_pitch_up_speed", getArmPitchUpSpeed());
        networkTable.putNumber("arm_pitch_down_speed", getArmPitchDownSpeed());
        networkTable.putNumber("arm_roller_out_speed", getArmRollerOutSpeed());
        networkTable.putNumber("arm_roller_in_speed", getArmRollerInSpeed());
    }

}
