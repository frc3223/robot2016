package frc.team3223.driving;

import java.util.ArrayList;
import java.util.List;

import edu.wpi.first.wpilibj.SpeedController;

public class MultiSpeedController implements SpeedController {

	private List<SpeedController> speedControllers = new ArrayList<>();
	
	public MultiSpeedController() {
	}
	
	public MultiSpeedController add(SpeedController speedController) {
		speedControllers.add(speedController);
		return this;
	}
	
	@Override
	public void pidWrite(double output) {
		speedControllers.forEach(sc -> sc.pidWrite(output));
	}

	@Override
	public double get() {
		return speedControllers.get(0).get();
	}

	@Override
	public void set(double speed, byte syncGroup) {
		speedControllers.forEach(sc -> sc.set(speed, syncGroup));
	}

	@Override
	public void set(double speed) {
		speedControllers.forEach(sc -> sc.set(speed));
	}

	@Override
	public void setInverted(boolean isInverted) {
		speedControllers.forEach(sc -> sc.setInverted(isInverted));
	}

	@Override
	public boolean getInverted() {
		return speedControllers.get(0).getInverted();
	}

	@Override
	public void disable() {
		speedControllers.forEach(SpeedController::disable);
	}

	@Override
	public void stopMotor() {
		speedControllers.forEach(SpeedController::stopMotor);
	}

}
