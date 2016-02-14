package frc.team3223.robot2016;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.tables.ITable;
import edu.wpi.first.wpilibj.tables.ITableListener;

/* thieverized from navx example rotate to angle */
public class RotateToAngle implements ITableListener, PIDOutput{

    PIDController turnController;
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

    public RotateToAngle(PIDSource gyro) {

        turnController = new PIDController(kP, kI, kD, kF, gyro, this);
        turnController.setInputRange(-180.0f, 180.0f);
        turnController.setOutputRange(-1.0, 1.0);
        turnController.setAbsoluteTolerance(kToleranceDegrees);
        turnController.setContinuous(true);
        turnController.setSetpoint(desiredHeading);
        //turnController.enable();
    }

    @Override
    public void valueChanged(ITable source, String key, Object value, boolean isNew) {

        switch(key) {
            case "desired_heading": {
                desiredHeading = (double) value;
                turnController.setSetpoint(desiredHeading);
                break;
            }
            case "rotate_velocity": {
                rotateToAngleRate = (double) value;
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
        turnController.enable();
    }

    public void disable() {
        turnController.disable();
    }
}
