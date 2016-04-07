package frc.team3223.util;

public class TimeProvider implements ITimeProvider{
    @Override
    public long getCurrentTimeMillis() {
        return System.currentTimeMillis();
    }
}
