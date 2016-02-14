package frc.team3223.drive;

import org.junit.Assert;

public class PolarDriveTests {

    public void assertAlmostEqual(double expected, double actual){
        Assert.assertEquals(expected, actual, 0.0001);
    }

    /*
    @Test
    public void testNormalizeDegrees() {
        assertAlmostEqual(0.0, PolarTankDrive.normalizeDegrees(0.));
        assertAlmostEqual(45.5, PolarTankDrive.normalizeDegrees(45.5));
        assertAlmostEqual(90.5, PolarTankDrive.normalizeDegrees(90.5));
        assertAlmostEqual(135.5, PolarTankDrive.normalizeDegrees(135.5));
        assertAlmostEqual(-180., PolarTankDrive.normalizeDegrees(180.));
        assertAlmostEqual(-180., PolarTankDrive.normalizeDegrees(-180.));
        assertAlmostEqual(-135., PolarTankDrive.normalizeDegrees(225.));
        assertAlmostEqual(-85., PolarTankDrive.normalizeDegrees(275.));
        assertAlmostEqual(-.5, PolarTankDrive.normalizeDegrees(359.5));
        assertAlmostEqual(19.5, PolarTankDrive.normalizeDegrees(379.5));
        assertAlmostEqual(.5, PolarTankDrive.normalizeDegrees(-359.5));
        assertAlmostEqual(-.5, PolarTankDrive.normalizeDegrees(-.5));
        assertAlmostEqual(-90.5, PolarTankDrive.normalizeDegrees(-90.5));
        assertAlmostEqual(-135.5, PolarTankDrive.normalizeDegrees(-135.5));
    }

    @Test
    public void testRightThrottle() {
        rtest(0., 0., 0.);
        rtest(1., -180., -0.5);
        rtest(1., -135., 0.);
        rtest(1., -125., 0.111111111);
        rtest(1., -45., 1.);
        rtest(1., -35., 0.888888888);
        rtest(1., 0., 0.5);
        rtest(1., 35., 0.111111111);
        rtest(1., 45., 0.);
        rtest(1., 125., -0.888888888);
        rtest(1., 135., -1.);
        rtest(1., 145., -0.888888888);
        rtest(1., 180., -0.5);
    }

    private void rtest(double radius, double theta, double expectedRightThrust) {
        Pair<Double, Double> pair = PolarTankDrive.polarToThrust(theta, radius);
        double rightThrust = pair.snd;
        assertAlmostEqual(expectedRightThrust, rightThrust);
    }

    @Test
    public void testLeftThrottle() {
        ltest(0., 0., 0.);
        ltest(1., -180., 0.5);
        ltest(1., -135., 1.);
        ltest(1., -125., 0.888888888);
        ltest(1., -45., 0.0);
        ltest(1., -35., -0.111111111);
        ltest(1., 0., -0.5);
        ltest(1., 35., -0.888888888);
        ltest(1., 45., -1.);
        ltest(1., 125., -0.111111111);
        ltest(1., 135., 0.);
        ltest(1., 145., 0.111111111);
        ltest(1., 180., 0.5);
    }

    private void ltest(double radius, double theta, double expectedRightThrust) {
        Pair<Double, Double> pair = PolarTankDrive.polarToThrust(theta, radius);
        double rightThrust = pair.fst;
        assertAlmostEqual(expectedRightThrust, rightThrust);
    }
    */
}
