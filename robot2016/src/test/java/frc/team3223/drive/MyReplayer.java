package frc.team3223.drive;

import frc.team3223.robot2016.RobotConfiguration;

public class MyReplayer extends Replayer{
    public MyReplayer(RobotConfiguration conf) {
        super(conf);
    }

    long now;

    @Override
    public long getNow() {
        return now;
    }

    public void setNow(long now) {
        this.now = now;
    }

    public void setEndTime(long time) {
        this.recordedEndTime = time;
    }
}
