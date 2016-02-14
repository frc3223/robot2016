package frc.team3223.util;

import edu.wpi.first.wpilibj.Joystick;

import java.util.function.Consumer;

/**
 * Execute an action once when a button is pressed or released.
 * Action sequence looks like:
 * Button is not pressed: (nothing)
 * User presses button: onToggle()
 * User continues to hold button: (nothing)
 * User releases button: offToggle()
 * Button is not pressed: (nothing)
 */
public class ToggleButton {
    private Joystick joystick;
    private int button;
    private boolean wasButtonPressed;
    private Consumer<Integer> onToggle;
    private Consumer<Integer> offToggle;

    public ToggleButton(Joystick joystick, int button) {
        this.joystick = joystick;
        this.button = button;
        wasButtonPressed = false;
    }

    public ToggleButton onToggleOn(Consumer<Integer> onToggle) {
        this.onToggle = onToggle;
        return this;
    }
    public ToggleButton onToggleOff(Consumer<Integer> offToggle) {
        this.offToggle = offToggle;
        return this;
    }

    public void teleopPeriodic() {

        if(!wasButtonPressed && joystick.getRawButton(button))
        {
            this.onToggle.accept(1);
            wasButtonPressed = true;
        }
        if (wasButtonPressed && !joystick.getRawButton(button))
        {
            wasButtonPressed = false;

        }
    }
}
