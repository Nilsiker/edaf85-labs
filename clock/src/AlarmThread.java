
import clock.io.ClockOutput;

public class AlarmThread extends Thread {
	private ClockOutput out;
	
	public AlarmThread(ClockOutput out) {
		this.out = out;
	}
	
	@Override
	public void run() {
		try {
			for (int i = 0; i < 20; i++) {
				new Thread(() -> out.alarm()).start();
				sleep(1000);
			}
		} catch (InterruptedException e) {
			// terminates
		}
	}
}
