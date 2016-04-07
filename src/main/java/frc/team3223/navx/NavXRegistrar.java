package frc.team3223.navx;

import edu.wpi.first.wpilibj.PIDSourceType;
import jaci.openrio.toast.core.ToastBootstrap;
import jaci.openrio.toast.lib.registry.Registrar;

import java.util.function.Supplier;

public class NavXRegistrar {
  public static volatile Registrar<Integer, INavX> navxRegistrar = new Registrar<>();

  public static INavX navX() {
    return navxRegistrar.fetch(1, INavX.class, supplier);

  }

  private static Supplier<INavX> supplier;

  /**
   * meant for making mocking possible.
   * 
   * @param supplier
   */
  public static void SetSupplier(Supplier<INavX> supplier) {
    NavXRegistrar.supplier = supplier;

  }

  static {
    if (ToastBootstrap.isSimulation) {
      System.out.println("simulating a navx");
      // simulation? eh, let's give it a braindead navx
      supplier = () -> {
        return new INavX() {
          @Override
          public float getYaw() {
            return 0;
          }

          @Override
          public float getPitch() {
            return 0;
          }

          @Override
          public float getRoll() {
            return 0;
          }

          @Override
          public float getWorldLinearAccelX() {
            return 0;
          }

          @Override
          public float getWorldLinearAccelY() {
            return 0;
          }

          @Override
          public float getWorldLinearAccelZ() {
            return 0;
          }

          @Override
          public float getVelocityX() {
            return 0;
          }

          @Override
          public float getVelocityY() {
            return 0;
          }

          @Override
          public float getVelocityZ() {
            return 0;
          }

          @Override
          public float getDisplacementX() {
            return 0;
          }

          @Override
          public float getDisplacementY() {
            return 0;
          }

          @Override
          public float getDisplacementZ() {
            return 0;
          }

          @Override
          public float getFusedHeading() {
            return 0;
          }

          @Override
          public double getAngle() {
            return 0;
          }

          @Override
          public void calibrate() {

          }

          @Override
          public void reset() {

          }

          @Override
          public double getRate() {
            return 0;
          }

          @Override
          public void free() {

          }

          @Override
          public void setPIDSourceType(PIDSourceType pidSource) {

          }

          @Override
          public PIDSourceType getPIDSourceType() {
            return null;
          }

          @Override
          public double pidGet() {
            return 0;
          }
        };
      };
    } else {
      System.out.println("not simulating a navx");
      supplier = () -> {
        return new NavX();
      };
    }

  }
}
