package frc.team3223.robot2016;

import edu.wpi.first.wpilibj.ADXL362;
import edu.wpi.first.wpilibj.AnalogGyro;
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
    public static final SPI.Port ACCELEROMETER_PORT = SPI.Port.kOnboardCS0;


    private ADXL362 accelerometer;
    private Gyro shooterGyro;
    private INavX navX;

    public SensorManager(){
        this.accelerometer = new ADXL362(SensorManager.ACCELEROMETER_PORT, Accelerometer.Range.k2G);
        navX = NavXRegistrar.navX();
        shooterGyro = new AnalogGyro(0);
    }

    public double[] getAccelerometerValues(){
        return new double[]{this.accelerometer.getX(),this.accelerometer.getY(),this.accelerometer.getZ()};
    }

    public INavX getNavX() {
        return navX;
    }

    public Gyro getShooterGyro() {
        return shooterGyro;
    }
}
