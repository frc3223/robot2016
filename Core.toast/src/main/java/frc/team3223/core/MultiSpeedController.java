package frc.team3223.core;

import frc.team3223.core.Pair;
import java.util.ArrayList;
import java.util.List;

import edu.wpi.first.wpilibj.SpeedController;

public class MultiSpeedController implements SpeedController {
	private List<Pair<SpeedController, Double>> speedControllers = new ArrayList<>();
	
	public MultiSpeedController() {
	}
	
	public MultiSpeedController add(SpeedController speedController) {
		speedControllers.add(new Pair<>(speedController, 1.0));
		return this;
	}
	
	public MultiSpeedController add(SpeedController speedController, double speedModifier) {
		speedControllers.add(new Pair<>(speedController, speedModifier));
		return this;
	}
	
	@Override
	public void pidWrite(double output) {
		speedControllers.forEach(sc -> sc.fst.pidWrite(output * sc.snd));
	}

	@Override
	public double get() {
		return speedControllers.get(0).fst.get();
	}

	@Override
	public void set(double speed, byte syncGroup) {
		speedControllers.forEach(sc -> sc.fst.set(speed * sc.snd, syncGroup));
	}

	@Override
	public void set(double speed) {
		speedControllers.forEach(sc -> sc.fst.set(speed * sc.snd));
	}

	@Override
	public void setInverted(boolean isInverted) {
		speedControllers.forEach(sc -> sc.fst.setInverted(isInverted));
	}

	@Override
	public boolean getInverted() {
		return speedControllers.get(0).fst.getInverted();
	}

	@Override
	public void disable() {
		speedControllers.forEach(sc -> sc.fst.disable());
	}

	@Override
	public void stopMotor() {
		speedControllers.forEach(sc -> sc.fst.disable());
	}

}
