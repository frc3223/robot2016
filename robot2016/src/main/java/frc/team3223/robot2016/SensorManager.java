package frc.team3223.robot2016;

import edu.wpi.first.wpilibj.ADXL362;
import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.interfaces.Accelerometer;
import edu.wpi.first.wpilibj.interfaces.Gyro;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.tables.ITable;
import frc.team3223.navx.INavX;
import frc.team3223.navx.NavXRegistrar;

/**
 * Created by alex on 2/23/16.
 */
public class SensorManager {
    private INavX navX;
    private Encoder shooterRaiserEncoder;

    public SensorManager(){
        navX = NavXRegistrar.navX();
        this.shooterRaiserEncoder = new Encoder(0, 1);
    }

    public INavX getNavX() {
        return navX;
    }

    public Encoder getShooterRaiserEncoder() {
        return shooterRaiserEncoder;
    }
}
