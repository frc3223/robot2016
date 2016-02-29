package frc.team3223.driving;

import frc.team3223.core.Pair;

public class PolarTank {

  public static Pair<Double, Double> speeds(final double power, final double thetaDesired,
      final double thetaCurrent, final double thetaSmoothThreshold) {
    final double thetaRelative = (thetaDesired - thetaCurrent + 180) % 360 - 180;
    final double theta = Math.copySign(
        (Math.abs(thetaRelative) <= thetaSmoothThreshold) ? thetaSmoothThreshold : 180,
        thetaRelative);
    return thetaToThrust(theta, power);
  }

  public static Pair<Double, Double> thetaToThrust(final double theta, final double power) {
    return new Pair<>(clamp(power * drivingTriangle(theta + 135), -1, 1),
        clamp(power * drivingTriangle(theta - 135), -1, 1));
  }

  public static double clamp(final double x, final double lo, final double hi) {
    return Math.max(lo, Math.min(hi, x));
  }

  public static double drivingTriangle(final double theta) {
    final double c = theta / 360;
    return 8 * Math.abs(c - Math.floor(c + 0.5)) - 2;
  }

}
