

public class TickerThread extends Thread {
	private ClockStatus status;

	public TickerThread(ClockStatus status) {
		this.status = status;
	}

	@Override
	public void run() {
		while (true) {
			try {
				sleep(1000);
				status.tick();	
			} catch (InterruptedException e) {
				
			}
		}
	}
}
