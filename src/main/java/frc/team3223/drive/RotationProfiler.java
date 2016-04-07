package frc.team3223.drive;

import frc.team3223.util.ITimeProvider;

public class RotationProfiler {
  public double vPeak; // degress per second
  public double vMax; // degress per second
  public double aMax; // degress per second squared
  public long accelDuration; // milliseconds
  public long peakDuration; // milliseconds
  public long decelDuration; // milliseconds

  public long startTime;
  public boolean isStarted = false;
  public boolean directionClockwise;

  ITimeProvider timeProvider;

  public RotationProfiler(ITimeProvider timeProvider) {
    this.timeProvider = timeProvider;
    aMax = 20;
    vMax = 120;
    directionClockwise = true;
  }

  public void compute(double degrees) {
    directionClockwise = degrees > 0;
    degrees = Math.abs(degrees);
    
    // time to accelerate from 0 to max velocity
    double accelTime = vMax / aMax;
    // rotation traveled during acceleration from 0 to max velocity
    // (ditto during deceleration)
    double maxAccelRotation = 0.5 * aMax * accelTime * accelTime;
    if (maxAccelRotation * 2 > degrees) {
      // profile is a triangle
      decelDuration = accelDuration = 1000 * (long) Math.sqrt(degrees / aMax);
      peakDuration = 0;
      vPeak = aMax * accelDuration / 1000.;
    } else {
      // profile is a trapezoid

      decelDuration = accelDuration = 1000 * (long) accelTime;
      vPeak = vMax;
    }
  }

  public void start() {
    startTime = timeProvider.getCurrentTimeMillis();
    isStarted = true;
  }

  public double getVelocity() {
    long elapsedTime = timeProvider.getCurrentTimeMillis() - startTime;
    double velocity;
    if (elapsedTime >= 0 && elapsedTime < accelDuration) {
      velocity = aMax * elapsedTime / 1000.;
    } else if (elapsedTime >= accelDuration && elapsedTime < (accelDuration + peakDuration)) {
      velocity = vMax;
    } else if (elapsedTime >= (accelDuration + peakDuration)
        && elapsedTime < (accelDuration + peakDuration + decelDuration)) {
      long elapsedSegment = elapsedTime - (accelDuration + peakDuration); // milliseconds
      velocity = vPeak - aMax * elapsedSegment / 1000.;
    } else {
      velocity = 0;
    }
    return velocity;
  }

  public double velocityToSignal(double velocity) {
    double[][] vSignalMapping = new double[][] {new double[] {0, 0.0}, new double[] {1, 0.4},
        new double[] {50, 0.5}, new double[] {107, 0.6}, new double[] {183, 0.7}};
    double signal = 0;
    boolean broken = false;

    if (velocity < vSignalMapping[0][0]) {
      return 0;
    }
    for (int i = 1; i < vSignalMapping.length; i++) {
      double velocity1 = vSignalMapping[i - 1][0];
      double velocity2 = vSignalMapping[i][0];
      double signal1 = vSignalMapping[i - 1][1];
      double signal2 = vSignalMapping[i][1];
      if (velocity <= velocity2) {
        signal = signal1 + (signal2 - signal1) * (velocity - velocity1) / (velocity2 - velocity1);
        broken = true;
        break;
      }
    }
    if (!broken) {
      signal = 1;
    }

    return signal;
  }

  public double getRightSignal() {
    double velocity = getVelocity();
    double signal = this.velocityToSignal(velocity);
    return Math.copySign(signal, directionClockwise ? -1 : 1);
  }

  public double getLeftSignal() {
    double velocity = getVelocity();
    double signal = this.velocityToSignal(velocity);
    return Math.copySign(signal, directionClockwise ? 1 : -1);
  }

  public void drive(SimpleDrive simpleDrive) {
    simpleDrive.driveRaw(getLeftSignal(), getRightSignal());
  }

  public boolean isDone() {
    if (!isStarted)
      return false;
    long elapsedTime = timeProvider.getCurrentTimeMillis() - startTime;
    return elapsedTime > (accelDuration + peakDuration + decelDuration);
  }
}
