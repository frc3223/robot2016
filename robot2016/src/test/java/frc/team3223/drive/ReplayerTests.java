package frc.team3223.drive;

import frc.team3223.robot2016.RobotConfiguration;
import org.junit.Assert;
import org.junit.Test;
import static org.mockito.Mockito.*;

public class ReplayerTests {
    @Test
    public void testReplay() {
        RobotConfiguration conf = mock(RobotConfiguration.class);
        MyReplayer replayer = new MyReplayer(conf);
        replayer.recordings.put(Long.valueOf(3), new DriveMotorRecording(3, 4.4, -4.4, 4.4, -4.4));
        replayer.recordings.put(Long.valueOf(13), new DriveMotorRecording(13, 4.4, -4.4, 4.4, -4.4));
        replayer.recordings.put(Long.valueOf(13), new DriveMotorRecording(13, 4.4, -4.4, 4.4, -4.4));
        replayer.recordings.put(Long.valueOf(23), new DriveMotorRecording(23, 4.4, -4.4, 4.4, -4.4));
        replayer.recordings.put(Long.valueOf(33), new DriveMotorRecording(33, 4.4, -4.4, 4.4, -4.4));
        replayer.setEndTime(33);
        replayer.setNow(1);
        replayer.replayPeriodic();

        replayer.setNow(34);
        replayer.replayPeriodic();

        replayer.setNow(3);
        replayer.replayPeriodic();
    }
}
