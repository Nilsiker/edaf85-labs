package lift;

public class LiftThread extends Thread {
	LiftView view;
	Lift lift;

	public LiftThread(LiftView view, Lift lift) {
		this.view = view;
		this.lift = lift;
	}
}
