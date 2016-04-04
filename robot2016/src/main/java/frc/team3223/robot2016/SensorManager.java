package frc.team3223.robot2016;

import edu.wpi.first.wpilibj.Encoder;
import frc.team3223.navx.INavX;
import frc.team3223.navx.NavXRegistrar;

/**
 * Created by alex on 2/23/16.
 */
public class SensorManager {
    private INavX navX;
    private Encoder shooterRaiserEncoder;

    public SensorManager(){
        navX = NavXRegistrar.navX();
        this.shooterRaiserEncoder = new Encoder(0, 1);
    }

    public INavX getNavX() {
        return navX;
    }

    public Encoder getShooterRaiserEncoder() {
        return shooterRaiserEncoder;
    }
}
