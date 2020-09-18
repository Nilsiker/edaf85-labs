package lift;

import java.util.Random;

public class PassengerThread extends Thread {
	LiftView view;
	Lift lift;

	public PassengerThread(LiftView view, Lift lift) {
		this.view = view;
		this.lift = lift;
	}

	@Override
	public void run() {
		try {
			Passenger pass = view.createPassenger();
			int delayInSeconds = new Random().nextInt(5);
			sleep(delayInSeconds * 1000);
			pass.begin();

		} catch (InterruptedException e) {
			// TODO: handle exception
		}
	}
}
