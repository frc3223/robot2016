package frc.team3223.drive;

import frc.team3223.util.ITimeProvider;

public class MyTimeProvider implements ITimeProvider {
  private long time;

  @Override
  public long getCurrentTimeMillis() {
    return time;
  }

  public void setCurrentTimeMillis(long time) {
    this.time = time;
  }
}
