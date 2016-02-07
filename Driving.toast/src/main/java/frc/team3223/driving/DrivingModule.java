package frc.team3223.driving;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Talon;
import frc.team3223.navx.NavX;
import jaci.openrio.toast.lib.module.IterativeModule;
import jaci.openrio.toast.lib.log.Logger;
import jaci.openrio.toast.lib.registry.Registrar;

public class DrivingModule extends IterativeModule {

    public static Logger logger;

    NetworkTable networkTable;
    Talon[] talons;
    Joystick leftJoystick;
    Joystick rightJoystick;

    AHRS navX;

    RobotDrive drive;

    @Override
    public String getModuleName() {
        return "Driving";
    }

    @Override
    public String getModuleVersion() {
        return "0.0.1";
    }

    @Override
    public void robotInit() {
        logger = new Logger("driving", Logger.ATTR_DEFAULT);
        networkTable = NetworkTable.getTable("SmartDashboard");
        talons = new Talon[6];
        for(int j = 0; j < talons.length; j++) {
            talons[j] = Registrar.talon(j);
        }
        leftJoystick = new Joystick(1);
        rightJoystick = new Joystick(0);
        drive = new RobotDrive(talons[0], talons[1], talons[2], talons[3]);

        navX = NavX.navX();
    }

    @Override
    public void teleopPeriodic() {
        drive.tankDrive(leftJoystick, rightJoystick);
    }

}
