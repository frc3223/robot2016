package frc.team3223.util;

import edu.wpi.first.wpilibj.Relay;
import jaci.openrio.toast.lib.registry.Registrar;

public final class RelayRegistrar {
  private RelayRegistrar() {};
  
  public static volatile Registrar<Integer, Relay> relayRegistrar = new Registrar<>();
  
  /**
   * Get a Spike style Relay instance from the Registrar
   * @param pwmPort the channel to use
   */
  public static Relay relay(int relayPort) {
      return relayRegistrar.fetch(relayPort, Relay.class, () -> { return new Relay(relayPort); });
  }
}
