package frc.team3223.drive;

/**
 * Created by alex on 2/23/16.
 */
public class DriveMotorRecording {
    public long tick;
    public double frontLeft;
    public double frontRight;
    public double backLeft;
    public double backRight;

    public DriveMotorRecording(long tick, double frontLeft, double frontRight, double backLeft, double backRight){
        this.tick = tick;
        this.frontLeft = frontLeft;
        this.backLeft = backLeft;
        this.frontRight = frontRight;
        this.backRight = backRight;
    }

    public DriveMotorRecording interpolate(long now, DriveMotorRecording next){
        if(this.tick != next.tick) {
            long Dt = next.tick - this.tick;
            double Dfl = next.frontLeft - this.frontLeft;
            double Dfr = next.frontRight - this.frontRight;
            double Dbl = next.backLeft - this.backLeft;
            double Dbr = next.backRight - this.backRight;

            double mfl = Dfl / Dt;
            double mfr = Dfr / Dt;
            double mbl = Dbl / Dt;
            double mbr = Dbr / Dt;

            return new DriveMotorRecording(
                    now,
                    mfl * now + this.frontLeft - mfl * this.tick,
                    mfr * now + this.frontRight - mfr * this.tick,
                    mbl * now + this.backLeft - mbl * this.tick,
                    mbr * now + this.backRight - mbr * this.tick
            );
        }
        else return this;
    }
}
