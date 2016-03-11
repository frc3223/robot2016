package frc.team3223.drive;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.interfaces.Gyro;
import edu.wpi.first.wpilibj.tables.ITable;
import edu.wpi.first.wpilibj.tables.ITableListener;
import frc.team3223.navx.INavX;

/* thieverized from navx example rotate to angle */
public class RotateToAngle implements ITableListener, PIDOutput, IDrive{

    PIDController turnController;
    private PIDSource pidSource;
    private Gyro gyro;
    private SimpleDrive simpleDrive;
    double rotateToAngleRate;

    /* The following PID Controller coefficients will need to be tuned */
    /* to match the dynamics of your drive system.  Note that the      */
    /* SmartDashboard in Test mode has support for helping you tune    */
    /* controllers by displaying a form where you can enter new P, I,  */
    /* and D constants and test the mechanism.                         */

    double kP = 0.03;
    double kI = 0.00;
    double kD = 0.00;
    double kF = 0.00;

    static final double kToleranceDegrees = 2.0f;


    double desiredHeading = 0.0;

    public RotateToAngle(INavX navx, SimpleDrive simpleDrive) {
        this.pidSource = navx;
        this.gyro = navx;
        this.simpleDrive = simpleDrive;
        makeController();

    }

    private void makeController() {
        if(turnController != null) {
            turnController.disable();
        }
        turnController = new PIDController(kP, kI, kD, kF, pidSource, this);
        turnController.setInputRange(-180.0f, 180.0f);
        turnController.setOutputRange(-1.0, 1.0);
        turnController.setAbsoluteTolerance(kToleranceDegrees);
        turnController.setContinuous(true);
        turnController.setSetpoint(desiredHeading);
    }

    @Override
    public void valueChanged(ITable source, String key, Object value, boolean isNew) {

        switch(key) {
            /*
            case "desired_heading": {
                double dvalue = (double) value;
                if(Math.abs(dvalue) > 180) {
                    turnController.setSetpoint(gyro.getAngle());
                }else{
                    desiredHeading = dvalue;
                    turnController.setSetpoint(desiredHeading);
                }
                break;
            }
            */
            case "target_theta": {
                double dvalue = (double) value;
                if(Math.abs(dvalue) < 100) {
                    desiredHeading = gyro.getAngle() + dvalue; // or is it minus?
                    turnController.setSetpoint(desiredHeading);
                }
                break;
            }
            case "rotate_velocity": {
                rotateToAngleRate = (double) value;
                break;
            }
            case "rotate_proportional": {
                kP = (double) value;
                makeController();
                break;
            }
            case "rotate_derivative": {
                kD = (double) value;
                makeController();
                break;
            }
            case "rotate_integral": {
                kI = (double) value;
                makeController();
                break;
            }
        }
    }

    @Override
    public void pidWrite(double output) {
        rotateToAngleRate = output;
    }

    public double getRotateToAngleRate() {
        return rotateToAngleRate;
    }

    public void enable() {
        simpleDrive.enable();
        turnController.enable();
    }

    public void disable() {

        simpleDrive.disable();
        turnController.disable();
    }


    public void rotate() {
        double left = getRotateToAngleRate();
        double right = -left;
        simpleDrive.drive(left, right);
    }
}
