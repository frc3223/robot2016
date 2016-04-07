package frc.team3223.drive;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Created by alex on 2/23/16.
 */
public class MotionProfiler {
  public static final int ARRAY_PROFILE_TYPE = 0;
  public static final int STEPS_POWER_PROFILE_TYPE = 1;

  private double[] profile;
  private int step = 0;

  public MotionProfiler(double[] profile) {
    this.profile = profile;
  }

  public MotionProfiler(String path, int profileType) {
    try {
      try (Scanner s = new Scanner(new File(path))) {
        if (profileType == ARRAY_PROFILE_TYPE) {
          int numSteps = 0;
          while (s.hasNext())
            numSteps++;
          this.profile = new double[numSteps];
          s.reset();
          numSteps = 0;
          while (s.hasNextInt()) {
            this.profile[numSteps] = s.nextInt();
            numSteps++;
          }
        } else if (profileType == STEPS_POWER_PROFILE_TYPE) {

        }
      }
    } catch (FileNotFoundException e) {
      this.profile = null;
      System.out.println("Could not load Profile file!!");
    }
  }

  public double step() {
    if (this.step < this.profile.length) {
      return this.profile[this.step];
      // this.step++;
    } else return 1;
  }

  public void reset() {
    this.step = 0;
  }
}
