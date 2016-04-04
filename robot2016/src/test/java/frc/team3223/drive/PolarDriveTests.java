package frc.team3223.drive;

import frc.team3223.util.Pair;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;

public class PolarDriveTests {

  public void assertAlmostEqual(double expected, double actual) {
    Assert.assertEquals(expected, actual, 0.0001);
  }

  @Test
  public void testNormalizeDegrees() {
    assertAlmostEqual(.5, PolarTankDrive.normalizeDegrees(-359.5));
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
    assertAlmostEqual(-.5, PolarTankDrive.normalizeDegrees(-.5));
    assertAlmostEqual(-90.5, PolarTankDrive.normalizeDegrees(-90.5));
    assertAlmostEqual(-135.5, PolarTankDrive.normalizeDegrees(-135.5));
  }

  @Test
  public void testRightThrottle() {

    rtest(0., 0., 0.);
    rtest(0.5, -180., -0.5);
    rtest(0.5, -135., 0.);
    rtest(0.5, -125., 0.111111111);
    rtest(0.5, -45., 1.);
    rtest(0.5, -35., 0.888888888);
    rtest(0.5, 0., 0.5);
    rtest(0.5, 35., 0.111111111);
    rtest(0.5, 45., 0.);
    rtest(0.5, 125., -0.888888888);
    rtest(0.5, 135., -1.);
    rtest(0.5, 145., -0.888888888);
    rtest(0.5, 180., -0.5);
  }

  private void outputRightThrustCsv() {
    try {
      File file = new File("right.csv");
      System.out.println("writing to " + file.getAbsolutePath());
      FileOutputStream out = new FileOutputStream("right.csv");
      OutputStreamWriter bw = new OutputStreamWriter(out);
      BufferedWriter bout = new BufferedWriter(bw);
      for (double th = -180.0; th < 180.0; th += 1.0) {
        Pair<Double, Double> pair = PolarTankDrive.polarToThrust(th, 0.5);
        double rightThrust = pair.snd;
        bout.write(Double.toString(th));
        bout.write(",");
        bout.write(Double.toString(rightThrust));
        bout.newLine();
      }
      bout.close();
    } catch (FileNotFoundException ex) {
      System.out.println("could not find file!");
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  private void rtest(double radius, double theta, double expectedRightThrust) {
    Pair<Double, Double> pair = PolarTankDrive.polarToThrust(theta, radius);
    double rightThrust = pair.snd;
    assertAlmostEqual(expectedRightThrust, rightThrust);
  }

  @Test
  public void testLeftThrottle() {
    ltest(0., 0., 0.);
    ltest(0.5, -180., 0.5);
    ltest(0.5, -135., 1.);
    ltest(0.5, -125., 0.888888888);
    ltest(0.5, -45., 0.0);
    ltest(0.5, -35., -0.111111111);
    ltest(0.5, 0., -0.5);
    ltest(0.5, 35., -0.888888888);
    ltest(0.5, 45., -1.);
    ltest(0.5, 125., -0.111111111);
    ltest(0.5, 135., 0.);
    ltest(0.5, 145., 0.111111111);
    ltest(0.5, 180., 0.5);
  }

  private void ltest(double radius, double theta, double expectedLeftThrust) {
    Pair<Double, Double> pair = PolarTankDrive.polarToThrust(theta, radius);
    double leftThrust = -pair.fst;
    assertAlmostEqual(expectedLeftThrust, leftThrust);
  }
}
