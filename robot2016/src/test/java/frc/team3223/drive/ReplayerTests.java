package frc.team3223.drive;

import edu.wpi.first.wpilibj.Talon;
import frc.team3223.robot2016.RobotConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.mockito.Mockito.*;

public class ReplayerTests {

    public MyReplayer setupReplayer() {
        RobotConfiguration conf = mock(RobotConfiguration.class);
        Talon frontLeft = mock(Talon.class);
        Talon frontRight = mock(Talon.class);
        Talon backLeft = mock(Talon.class);
        Talon backRight = mock(Talon.class);

        when(conf.getFrontLeftTalon()).thenReturn(frontLeft);
        when(conf.getFrontRightTalon()).thenReturn(frontRight);
        when(conf.getRearLeftTalon()).thenReturn(backLeft);
        when(conf.getRearRightTalon()).thenReturn(backRight);

        MyReplayer replayer = new MyReplayer(conf);
        replayer.recordings.put(Long.valueOf(3), new DriveMotorRecording(3, 4.4, -4.4, 4.4, -4.4));
        replayer.recordings.put(Long.valueOf(13), new DriveMotorRecording(13, 4.4, -4.4, 4.4, -4.4));
        replayer.recordings.put(Long.valueOf(13), new DriveMotorRecording(13, 3.4, -3.4, 3.4, -3.4));
        replayer.recordings.put(Long.valueOf(23), new DriveMotorRecording(23, 4.4, -4.4, 4.4, -4.4));
        replayer.recordings.put(Long.valueOf(33), new DriveMotorRecording(33, 4.4, -4.4, 4.4, -4.4));
        replayer.setEndTime(33);

        return replayer;
    }

    public DriveMotorRecording verifyMotors(MyReplayer replayer) {
        ArgumentCaptor<Double> frontLeftCaptor = ArgumentCaptor.forClass(Double.class);
        ArgumentCaptor<Double> frontRightCaptor = ArgumentCaptor.forClass(Double.class);
        ArgumentCaptor<Double> backRightCaptor = ArgumentCaptor.forClass(Double.class);
        ArgumentCaptor<Double> backLeftCaptor = ArgumentCaptor.forClass(Double.class);
        verify(replayer.conf.getFrontLeftTalon()).set(frontLeftCaptor.capture());
        verify(replayer.conf.getFrontRightTalon()).set(frontRightCaptor.capture());
        verify(replayer.conf.getRearLeftTalon()).set(backLeftCaptor.capture());
        verify(replayer.conf.getRearRightTalon()).set(backRightCaptor.capture());

        return new DriveMotorRecording(0,
                frontLeftCaptor.getValue().doubleValue(),
                frontRightCaptor.getValue().doubleValue(),
                backLeftCaptor.getValue().doubleValue(),
                backRightCaptor.getValue().doubleValue()
        );
    }

    @Test
    public void testReplay1() {
        MyReplayer replayer = setupReplayer();

        replayer.setNow(1);
        replayer.replayPeriodic();
        DriveMotorRecording r = verifyMotors(replayer);

        Assert.assertEquals(1.466, r.frontLeft, 0.001);
        Assert.assertEquals(-1.466, r.frontRight, 0.001);
        Assert.assertEquals(1.466, r.backLeft, 0.001);
        Assert.assertEquals(-1.466, r.backRight, 0.001);
    }

    @Test
    public void testReplay3() {
        MyReplayer replayer = setupReplayer();

        replayer.setNow(3);
        replayer.replayPeriodic();
        DriveMotorRecording r = verifyMotors(replayer);

        Assert.assertEquals(4.4, r.frontLeft, 0.001);
        Assert.assertEquals(-4.4, r.frontRight, 0.001);
        Assert.assertEquals(4.4, r.backLeft, 0.001);
        Assert.assertEquals(-4.4, r.backRight, 0.001);
    }

    @Test
    public void testReplay13() {
        MyReplayer replayer = setupReplayer();

        replayer.setNow(13);
        replayer.replayPeriodic();
        DriveMotorRecording r = verifyMotors(replayer);

        Assert.assertEquals(3.4, r.frontLeft, 0.001);
        Assert.assertEquals(-3.4, r.frontRight, 0.001);
        Assert.assertEquals(3.4, r.backLeft, 0.001);
        Assert.assertEquals(-3.4, r.backRight, 0.001);
    }

    @Test
    public void testReplay17() {
        MyReplayer replayer = setupReplayer();

        replayer.setNow(17);
        replayer.replayPeriodic();
        DriveMotorRecording r = verifyMotors(replayer);

        Assert.assertEquals(3.8, r.frontLeft, 0.001);
        Assert.assertEquals(-3.8, r.frontRight, 0.001);
        Assert.assertEquals(3.8, r.backLeft, 0.001);
        Assert.assertEquals(-3.8, r.backRight, 0.001);
    }

    @Test
    public void testReplay34() {
        MyReplayer replayer = setupReplayer();

        replayer.setNow(34);
        Assert.assertEquals(true, replayer.isReplaying());
        replayer.replayPeriodic();
        DriveMotorRecording r = verifyMotors(replayer);

        Assert.assertEquals(0, r.frontLeft, 0.001);
        Assert.assertEquals(0, r.frontRight, 0.001);
        Assert.assertEquals(0, r.backLeft, 0.001);
        Assert.assertEquals(0, r.backRight, 0.001);
        Assert.assertEquals(false, replayer.isReplaying());
    }
}
