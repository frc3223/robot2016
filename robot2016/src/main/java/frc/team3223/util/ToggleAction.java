package frc.team3223.util;

import edu.wpi.first.wpilibj.Joystick;

@FunctionalInterface
public interface ToggleAction {
    void apply(Joystick joystick, int buttonNumber);
}
