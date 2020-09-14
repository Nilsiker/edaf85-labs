import clock.io.ClockOutput;

public class TimeThread extends Thread {
	private ClockStatus status;
	private ClockOutput output;
	
	public TimeThread(ClockStatus status, ClockOutput output) {
		this.status = status;
		this.output = output;
	}
	
	@Override
	public void run() {
		while(true) {
			try {
				status.updated().acquire();			// signal semaphore
				int[] time = status.getTime();
				output.displayTime(time[0], time[1], time[2]);
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}	
		}
	}
}
