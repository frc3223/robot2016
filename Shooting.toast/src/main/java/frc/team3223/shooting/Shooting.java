package frc.team3223.shooting;

import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import frc.team3223.util.MultiSpeedController;
import frc.team3223.util.RelayRegistrar;
import jaci.openrio.toast.lib.log.Logger;
import jaci.openrio.toast.lib.module.IterativeModule;
import jaci.openrio.toast.lib.registry.Registrar;
import java.util.Arrays;
import java.util.List;

public class Shooting extends IterativeModule {
    public static Logger logger;
    private NetworkTable networkTable;

    private SpeedController shooter;
    private SpeedController roller;
    private Relay pitcherRight;
    private Relay pitcherLeft;
    private List<Relay> pitchers;

    @Override
    public String getModuleName() {
        return "Shooting";
    }

    @Override
    public String getModuleVersion() {
        return "0.0.1";
    }

    @Override
    public void robotInit() {
        logger = new Logger("Shooting", Logger.ATTR_DEFAULT);
        networkTable = NetworkTable.getTable("SmartDashboard");
        shooter = new MultiSpeedController().add(Registrar.talon(4)).add(Registrar.talon(5), -1.0);
        pitchers = Arrays.asList(pitcherRight = RelayRegistrar.relay(0),
            pitcherLeft = RelayRegistrar.relay(1));
    }

    @Override
    public void teleopInit() {
    }

    @Override
    public void teleopPeriodic() {
        final double rollerSpeed = 0.0;
        final Relay.Value pitcherDirection = Relay.Value.kOff;

        roller.set(rollerSpeed);
        pitchers.forEach(r -> r.set(pitcherDirection));
    }
}
