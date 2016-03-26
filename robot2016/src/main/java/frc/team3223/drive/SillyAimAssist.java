package frc.team3223.drive;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.interfaces.Gyro;
import edu.wpi.first.wpilibj.tables.ITable;
import edu.wpi.first.wpilibj.tables.ITableListener;
import frc.team3223.robot2016.RobotConfiguration;

public class SillyAimAssist implements IDrive, ITableListener{

    private RobotConfiguration conf;
    private Gyro gyro;
    private double theta = 0;
    private double k = 1 / 44.;

    public SillyAimAssist(RobotConfiguration conf) {
        this.conf = conf;
        this.gyro = conf.getSensorManager().getNavX();
    }

    public void drive() {
        double value = conf.getLeftJoystick().getAxis(Joystick.AxisType.kY);
        double correction = theta * k;
        double left = value - correction;
        double right = -value + correction;
        conf.getFrontRightTalon().set(right);
        conf.getRearRightTalon().set(right);
        conf.getFrontLeftTalon().set(left);
        conf.getRearLeftTalon().set(left);
    }

    @Override
    public void enable() {

    }

    @Override
    public void disable() {

    }

    @Override
    public void valueChanged(ITable source, String key, Object value, boolean isNew) {
        if(key.equals("target_theta")) {
            double t = (double) value;
            // "no data" value is 1000
            if(Math.abs(t) <= 100) {
                // is this + or -?
                theta = gyro.getAngle() + t;
            }
        }
    }
}
