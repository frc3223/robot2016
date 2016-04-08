package frc.team3223.robot2016;

/**
 * Created by Samantha on 4/8/2016.
 */
public class ShootStateMachine {
    Shooter shooter;
    private RobotConfiguration conf;

    private State state = State.IDLE;

    private long stateStartTime;

    public ShootStateMachine(Shooter shooter, RobotConfiguration conf) {
        this.shooter = shooter;
        this.conf = conf;
    }

    public static enum State {
        IDLE, SLURPING, SHOOTING_INIT, SHOOTING_ACTIVATION, DRIVE_TONGUE, SLURP_INIT, SHOOTING_WAIT_FOR_NOT_BACK, SHOOTING_TAIL_IN_TO_STOP
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public void setStateAndStart(State state, long currentTime) {
        setState(state);
        this.stateStartTime = currentTime;
    }

    public void periodic() {
        long currentTime = System.currentTimeMillis();
        long tongueOscillationTime = 1000;
        long spinUpTime = 100;

        switch (this.getState()) {
            case IDLE:
                shooter.stopShooter();
                shooter.stopTongue();

                if (conf.shouldSlurp()) {
                    this.setStateAndStart(State.SLURPING, currentTime);
                }
                if (conf.shouldShoot()) {
                    this.setStateAndStart(State.SHOOTING_INIT, currentTime);
                }
                if(conf.shouldSpinTongue()) {
                    this.setStateAndStart(State.DRIVE_TONGUE, currentTime);
                }
                break;
            case SLURPING:
                shooter.slurp();
                shooter.stopTongue();

                if (!conf.shouldSlurp()) {
                    this.setStateAndStart(State.IDLE, currentTime);
                }
                break;
            case SHOOTING_INIT:
                shooter.shoot();
                shooter.stopTongue();

                if (currentTime - stateStartTime > spinUpTime) {
                    if(conf.isTongueBack()) {
                        this.setStateAndStart(State.SHOOTING_WAIT_FOR_NOT_BACK, currentTime);
                    } else {
                        this.setStateAndStart(State.SHOOTING_ACTIVATION, currentTime);
                    }
                }
                break;
            case SHOOTING_WAIT_FOR_NOT_BACK:
                shooter.shoot();
                shooter.rotateTongue();
                if(!conf.isTongueBack()) {
                    this.setStateAndStart(State.SHOOTING_ACTIVATION, currentTime);
                }
                break;
            case SHOOTING_ACTIVATION:
                shooter.shoot();
                shooter.rotateTongue();

                if (conf.isTongueBack()) {
                    if(!conf.shouldShoot()) {
                        this.setStateAndStart(State.IDLE, currentTime);
                    }else{
                        this.setStateAndStart(State.SHOOTING_WAIT_FOR_NOT_BACK, currentTime);
                    }
                }
                break;
            case DRIVE_TONGUE:
                shooter.stopShooter();
                shooter.rotateTongue();

                if (!conf.shouldSpinTongue()) {
                    this.setStateAndStart(State.IDLE, currentTime);
                }
                break;
			default:
				shooter.stopShooter();
				shooter.stopTongue();
				break;
        }
    }
}
