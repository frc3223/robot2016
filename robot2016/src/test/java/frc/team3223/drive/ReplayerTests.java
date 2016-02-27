package frc.team3223.drive;

import frc.team3223.robot2016.RobotConfiguration;
import org.junit.Assert;
import org.junit.Test;
import static org.mockito.Mockito.*;

public class ReplayerTests {
    @Test
    public void testIndexOfNext() {
        RobotConfiguration conf = mock(RobotConfiguration.class);
        Replayer replayer = new Replayer(conf);
        replayer.powers.add(new DriveMotorPowers(3, 4.4, -4.4, 4.4, -4.4));
        replayer.powers.add(new DriveMotorPowers(13, 4.4, -4.4, 4.4, -4.4));
        replayer.powers.add(new DriveMotorPowers(13, 4.4, -4.4, 4.4, -4.4));
        replayer.powers.add(new DriveMotorPowers(23, 4.4, -4.4, 4.4, -4.4));
        replayer.powers.add(new DriveMotorPowers(33, 4.4, -4.4, 4.4, -4.4));

        Assert.assertEquals(0, replayer.indexOfNextBelow(1, 0));
        Assert.assertEquals(0, replayer.indexOfNextAbove(1, 0));

        Assert.assertEquals(0, replayer.indexOfNextBelow(3, 0));
        Assert.assertEquals(1, replayer.indexOfNextAbove(3, 0));

        Assert.assertEquals(0, replayer.indexOfNextBelow(4, 0));
        Assert.assertEquals(1, replayer.indexOfNextAbove(4, 1));

        Assert.assertEquals(1, replayer.indexOfNextBelow(13, 0));
        Assert.assertEquals(2, replayer.indexOfNextAbove(13, 0));

        Assert.assertEquals(3, replayer.indexOfNextBelow(24, 3));
        Assert.assertEquals(4, replayer.indexOfNextAbove(24, 3));

        Assert.assertEquals(4, replayer.indexOfNextBelow(34, 4));
        Assert.assertEquals(-1, replayer.indexOfNextAbove(34, 4));
    }
}
