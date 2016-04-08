package frc.team3223.robot2016;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import frc.team3223.navx.INavX;
import frc.team3223.navx.NavXRegistrar;
import jaci.openrio.toast.lib.registry.Registrar;

public class SensorManager {
  private INavX navX;
  private Encoder shooterRaiserEncoder;
  private DigitalInput tongueLimitSwitch;

  public SensorManager() {
    navX = NavXRegistrar.navX();
    this.shooterRaiserEncoder = new Encoder(0, 1);
    this.tongueLimitSwitch = Registrar.digitalInput(2);
  }

  public INavX getNavX() {
    return navX;
  }

  public Encoder getShooterRaiserEncoder() {
    return shooterRaiserEncoder;
  }

  public DigitalInput getTongueLimitSwitch() {
    return tongueLimitSwitch;
  }
}
