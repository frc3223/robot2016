package frc.team3223.robot2016;

import edu.wpi.first.wpilibj.ADXL362;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.interfaces.Accelerometer;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.tables.ITable;

/**
 * Created by alex on 2/23/16.
 */
public class SensorManager {
    public static final SPI.Port ACCELEROMETER_PORT = SPI.Port.kOnboardCS0;


    private ADXL362 accelerometer;

    public SensorManager(){
        this.accelerometer = new ADXL362(SensorManager.ACCELEROMETER_PORT, Accelerometer.Range.k4G);
        NetworkTable table = NetworkTable.getTable("SmartDashboard");
        table.putNumberArray("Accelerometer[x,y,z]",this.getAccelerometerValues());
    }

    public double[] getAccelerometerValues(){
        return new double[]{this.accelerometer.getX(),this.accelerometer.getY(),this.accelerometer.getZ()};
    }


}
