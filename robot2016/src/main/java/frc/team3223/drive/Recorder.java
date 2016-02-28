package frc.team3223.drive;


import frc.team3223.robot2016.RobotConfiguration;

public class Recorder {
    RecorderContext context;
    private RobotConfiguration conf;
    private long startTime;
    boolean recording = false;

    public Recorder (RobotConfiguration conf){

        this.conf = conf;
    }

    public void setup(String name){
        context = new RecorderContext(name);
        context.add("RF", () -> conf.getFrontRightTalon().get());
        context.add("RR", () -> conf.getRearRightTalon().get());
        context.add("LF", () -> conf.getFrontLeftTalon().get());
        context.add("LR", () -> conf.getRearLeftTalon().get());
        context.add("RW", () -> conf.getRightWindowMotorTalon().get());
        context.add("LW", () -> conf.getLeftWindowMotorTalon().get());
        context.add("AclX", () -> conf.getNavX().getWorldLinearAccelX());
        context.add("AclY", () -> conf.getNavX().getWorldLinearAccelY());
        context.add("AclZ", () -> conf.getNavX().getWorldLinearAccelZ());
        context.add("Angle", () -> conf.getNavX().getAngle());
        context.add("Pitch", () -> conf.getNavX().getPitch());
        context.add("Roll", () -> conf.getNavX().getRoll());
        context.add("Yaw", () -> conf.getNavX().getYaw());

    }

    public void startRecording() {
        startTime = System.currentTimeMillis();
        recording = true;
    }

    public void record() {
        context.tick();

        if(System.currentTimeMillis() - startTime > 15000) {
            recording = false;
        }
    }

    public boolean isRecording() {
        return recording;
    }
}
