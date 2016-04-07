package frc.team3223.drive;

import org.junit.Assert;
import org.junit.Test;

public class RotationProfilerTests {
    @Test
    public void testComputeClockwise() {
        MyTimeProvider timeProvider = new MyTimeProvider();
        RotationProfiler profiler = new RotationProfiler(timeProvider);
        timeProvider.setCurrentTimeMillis(100);

        profiler.aMax = 10;
        profiler.vMax = 80;
        Assert.assertEquals(false, profiler.isDone());
        profiler.compute(90);

        PolarDriveTests.assertAlmostEqual(30, profiler.vPeak);
        Assert.assertEquals(3000, profiler.accelDuration);
        Assert.assertEquals(3000, profiler.decelDuration);
        Assert.assertEquals(0, profiler.peakDuration);
        Assert.assertEquals(false, profiler.isDone());

        profiler.start();

        Assert.assertEquals(100, profiler.startTime);

        PolarDriveTests.assertAlmostEqual(0, profiler.getVelocity());
        Assert.assertEquals(false, profiler.isDone());
        timeProvider.setCurrentTimeMillis(-1);
        PolarDriveTests.assertAlmostEqual(0, profiler.getVelocity());
        Assert.assertEquals(false, profiler.isDone());
        timeProvider.setCurrentTimeMillis(1600);
        PolarDriveTests.assertAlmostEqual(15, profiler.getVelocity());
        timeProvider.setCurrentTimeMillis(3100);
        PolarDriveTests.assertAlmostEqual(30, profiler.getVelocity());
        PolarDriveTests.assertAlmostEqual(-0.459183, profiler.getRightSignal());
        PolarDriveTests.assertAlmostEqual(0.459183, profiler.getLeftSignal());
        Assert.assertEquals(false, profiler.isDone());
        timeProvider.setCurrentTimeMillis(4600);
        PolarDriveTests.assertAlmostEqual(15, profiler.getVelocity());
        PolarDriveTests.assertAlmostEqual(-0.42857, profiler.getRightSignal());
        PolarDriveTests.assertAlmostEqual(0.42857, profiler.getLeftSignal());
        Assert.assertEquals(false, profiler.isDone());
        timeProvider.setCurrentTimeMillis(6100);
        PolarDriveTests.assertAlmostEqual(0, profiler.getVelocity());
        Assert.assertEquals(false, profiler.isDone());
        timeProvider.setCurrentTimeMillis(6200);
        PolarDriveTests.assertAlmostEqual(0, profiler.getVelocity());
        Assert.assertEquals(true, profiler.isDone());

    }

    @Test
    public void testComputeCounterClockwise() {
        MyTimeProvider timeProvider = new MyTimeProvider();
        RotationProfiler profiler = new RotationProfiler(timeProvider);
        timeProvider.setCurrentTimeMillis(100);

        profiler.aMax = 10;
        profiler.vMax = 80;
        Assert.assertEquals(false, profiler.isDone());
        profiler.compute(-90);

        PolarDriveTests.assertAlmostEqual(30, profiler.vPeak);
        Assert.assertEquals(3000, profiler.accelDuration);
        Assert.assertEquals(3000, profiler.decelDuration);
        Assert.assertEquals(0, profiler.peakDuration);
        Assert.assertEquals(false, profiler.isDone());

        profiler.start();

        Assert.assertEquals(100, profiler.startTime);

        PolarDriveTests.assertAlmostEqual(0, profiler.getVelocity());
        Assert.assertEquals(false, profiler.isDone());
        timeProvider.setCurrentTimeMillis(-1);
        PolarDriveTests.assertAlmostEqual(0, profiler.getVelocity());
        Assert.assertEquals(false, profiler.isDone());
        timeProvider.setCurrentTimeMillis(1600);
        PolarDriveTests.assertAlmostEqual(15, profiler.getVelocity());
        timeProvider.setCurrentTimeMillis(3100);
        PolarDriveTests.assertAlmostEqual(30, profiler.getVelocity());
        PolarDriveTests.assertAlmostEqual(0.459183, profiler.getRightSignal());
        PolarDriveTests.assertAlmostEqual(-0.459183, profiler.getLeftSignal());
        Assert.assertEquals(false, profiler.isDone());
        timeProvider.setCurrentTimeMillis(4600);
        PolarDriveTests.assertAlmostEqual(15, profiler.getVelocity());
        PolarDriveTests.assertAlmostEqual(0.42857, profiler.getRightSignal());
        PolarDriveTests.assertAlmostEqual(-0.42857, profiler.getLeftSignal());
        Assert.assertEquals(false, profiler.isDone());
        timeProvider.setCurrentTimeMillis(6100);
        PolarDriveTests.assertAlmostEqual(0, profiler.getVelocity());
        Assert.assertEquals(false, profiler.isDone());
        timeProvider.setCurrentTimeMillis(6200);
        PolarDriveTests.assertAlmostEqual(0, profiler.getVelocity());
        Assert.assertEquals(true, profiler.isDone());

    }

    @Test
    public void testVelocityToSignal() {
        MyTimeProvider timeProvider = new MyTimeProvider();
        RotationProfiler profiler = new RotationProfiler(timeProvider);

        PolarDriveTests.assertAlmostEqual(0, profiler.velocityToSignal(-1));
        PolarDriveTests.assertAlmostEqual(0, profiler.velocityToSignal(0));
        PolarDriveTests.assertAlmostEqual(0.4, profiler.velocityToSignal(1));
        PolarDriveTests.assertAlmostEqual(0.40204, profiler.velocityToSignal(2));
        PolarDriveTests.assertAlmostEqual(0.56140, profiler.velocityToSignal(85));
        PolarDriveTests.assertAlmostEqual(0.69605, profiler.velocityToSignal(180));
        PolarDriveTests.assertAlmostEqual(1, profiler.velocityToSignal(500));

    }
}
