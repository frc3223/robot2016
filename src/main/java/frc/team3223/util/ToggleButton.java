package frc.team3223.util;

import edu.wpi.first.wpilibj.Joystick;

/**
 * Execute an action once when a button is pressed or released. Action sequence looks like: Button
 * is not pressed: (nothing) User presses button: onToggle() User continues to hold button:
 * (nothing) User releases button: offToggle() Button is not pressed: (nothing)
 */
public class ToggleButton {
  private Joystick joystick;
  private int button;
  private boolean wasButtonPressed;
  private ToggleAction onToggle = null;
  private ToggleAction offToggle = null;

  public ToggleButton(Joystick joystick, int button) {
    this.joystick = joystick;
    this.button = button;
    wasButtonPressed = false;
  }

  public ToggleButton onToggleOn(ToggleAction onToggle) {
    this.onToggle = onToggle;
    return this;
  }

  public ToggleButton onToggleOff(ToggleAction offToggle) {
    this.offToggle = offToggle;
    return this;
  }

  public void periodic() {

    if (!wasButtonPressed && joystick.getRawButton(button)) {
      if (onToggle != null) {
        this.onToggle.apply(joystick, button);
      }
      wasButtonPressed = true;
    }
    if (wasButtonPressed && !joystick.getRawButton(button)) {
      if (offToggle != null) {
        this.offToggle.apply(joystick, button);
      }
      wasButtonPressed = false;

    }
  }
}
