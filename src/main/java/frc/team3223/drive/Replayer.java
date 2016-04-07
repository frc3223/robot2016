package frc.team3223.drive;

import frc.team3223.robot2016.RobotConfiguration;
import jaci.openrio.toast.core.io.Storage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class Replayer {
  public HashMap<Long, DriveMotorRecording> recordings;
  public RobotConfiguration conf;
  protected boolean replaying = false;
  private long startTime;
  protected long recordedEndTime;
  private int currentIndex;

  public Replayer(RobotConfiguration conf) {
    this.conf = conf;
    this.recordings = new HashMap<>();
  }

  public long getNow() {
    long now = System.currentTimeMillis() - startTime;
    return now;
  }

  public void replayPeriodic() {
    long now = getNow();
    DriveMotorRecording r1, r2;

    long recordedPrev = now;
    long recordedNext = now;

    while (!this.recordings.containsKey(recordedPrev) && recordedPrev >= 0) {
      recordedPrev--;
    }
    r1 = (recordedPrev > 0) ? this.recordings.get(recordedPrev)
        : new DriveMotorRecording(0, 0, 0, 0, 0);

    while (!this.recordings.containsKey(recordedNext) && recordedNext <= this.recordedEndTime) {
      recordedNext++;
    }
    r2 = (recordedNext <= this.recordedEndTime) ? this.recordings.get(recordedNext)
        : new DriveMotorRecording(now, 0, 0, 0, 0);

    DriveMotorRecording r = r1.interpolate(now, r2);

    // System.out.printf("T=%s (vs %s), RF=%.2f, RR=%.2f, LF=%.2f, LR=%.2f\n", getNow(), r.tick,
    // r.frontRight, r.backRight, r.frontLeft, r.backLeft);

    conf.getFrontLeftTalon().set(r.frontLeft);
    conf.getFrontRightTalon().set(r.frontRight);
    conf.getRearLeftTalon().set(r.backLeft);
    conf.getRearRightTalon().set(r.backRight);

    if (now > this.recordedEndTime) {
      replaying = false;
      return;
    }
  }

  public void setup(String name) {
    File target_file = Storage.highestPriority("system/recorder/" + name + ".csv");
    try (BufferedReader reader = new BufferedReader(new FileReader(target_file))) {
      String line;
      boolean header = true;
      int timeIndex = 0;
      int rightFrontIndex = 1;
      int rightRearIndex = 2;
      int leftFrontIndex = 3;
      int leftRearIndex = 4;
      this.recordedEndTime = 0;
      while ((line = reader.readLine()) != null) {
        if (header) {
          String[] columns = line.split(",");
          if (!columns[timeIndex].equals("Time")) {
            throw new Exception("Bad file!");
          }
          if (!columns[rightFrontIndex].equals("RF")) {
            throw new Exception("Bad file!");
          }
          if (!columns[rightRearIndex].equals("RR")) {
            throw new Exception("Bad file!");
          }
          if (!columns[leftFrontIndex].equals("LF")) {
            throw new Exception("Bad file!");
          }
          if (!columns[leftRearIndex].equals("LR")) {
            throw new Exception("Bad file!");
          }

          header = false;
        } else {
          String[] columns = line.split(",");
          long time = Long.parseLong(columns[timeIndex]);
          double leftFront = Double.parseDouble(columns[leftFrontIndex]);
          double rightFront = Double.parseDouble(columns[rightFrontIndex]);
          double leftRear = Double.parseDouble(columns[leftRearIndex]);
          double rightRear = Double.parseDouble(columns[rightRearIndex]);
          DriveMotorRecording p =
              new DriveMotorRecording(time, leftFront, rightFront, leftRear, rightRear);
          this.recordings.put(time, p);
          if (time > this.recordedEndTime)
            this.recordedEndTime = time;
        }
      }
    } catch (IOException ex) {

    } catch (Exception ex) {
      // probably bad file
    }
  }

  public void start() {
    replaying = true;
    startTime = System.currentTimeMillis();
    currentIndex = 0;
  }

  public boolean isReplaying() {
    return replaying;
  }
}
