

public class TickerThread extends Thread {
	private ClockStatus status;

	public TickerThread(ClockStatus status) {
		this.status = status;
	}

	@Override
	public void run() {
		long t, diff;
		t = System.currentTimeMillis();
		while (true) {
			try {
				t += 1000;
				diff = t - System.currentTimeMillis();
				if(diff > 0) sleep(diff);
				status.tick();	
			} catch (InterruptedException e) {
				
			}
		}
	}
}
