import java.util.concurrent.Semaphore;

import clock.io.ClockOutput;

public class ClockStatus {
	private int time = 0;
	private int h, m, s = 0;
	private int ah, am, as = 0;
	private int remainingBeeps = 20;
	private boolean alarmStatus = false;
	private boolean fireAlarm = false;
	private Semaphore mutex = new Semaphore(1);
	private Semaphore updated = new Semaphore(0);
	private ClockOutput out;
	
	public ClockStatus(ClockOutput out) {
		this.out = out;
	}

	public void tick() throws InterruptedException {
		mutex.acquire();
		time++;
		s = time % 60;
		m = time / 60;
		m %= 60;
		h = time / 3600;
		h %= 24;
		
		if(alarmStatus && timeForAlarm()) {
			fireAlarm = true;
		}
		out.displayTime(h, m, s);
		if (fireAlarm && remainingBeeps > 0)
		{
			out.alarm();
			remainingBeeps--;
		}
		mutex.release();
	}

	private boolean timeForAlarm(){
		return ah == h && am == m && as == s;
	}
	
	public void stopAlarm() {
		fireAlarm = false;
	}

	public void setTime(int h, int m, int s) throws InterruptedException {
		mutex.acquire();
		time = h * 3600 + m*60 + s;
		mutex.release();
	}

	public void setAlarm(int h, int m, int s) throws InterruptedException {
		mutex.acquire();
		this.ah = h;
		this.am = m;
		this.as = s;
		mutex.release();
	}

	public boolean toggleAlarm() throws InterruptedException {
		boolean temp = false;
		mutex.acquire();
		alarmStatus = !alarmStatus;
		temp = alarmStatus;
		mutex.release();
		return temp;
	}

	public Semaphore updated() {
		return updated;
	}
}
