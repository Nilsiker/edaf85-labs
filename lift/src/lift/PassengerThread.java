package lift;

import java.util.Random;

public class PassengerThread extends Thread {
	LiftView view;
	LiftMonitor lift;

	public PassengerThread(LiftView view, LiftMonitor lift) {
		this.view = view;
		this.lift = lift;
	}

	@Override
	public void run() {
		try {
			Passenger pass = view.createPassenger();
			
			int delayInSeconds = new Random().nextInt(5);
			sleep(delayInSeconds*1000);

			pass.begin();
			lift.arrive(pass);

			lift.enter(pass);
			pass.enterLift();
			lift.entered(pass);

			lift.leave(pass);
			pass.exitLift();
			lift.left(pass);
			pass.end();

		} catch (InterruptedException e) {
			// TODO: handle exception
		}
	}
}
