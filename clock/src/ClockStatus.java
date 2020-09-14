import java.util.concurrent.Semaphore;

import clock.io.ClockOutput;

public class ClockStatus {
	private int h, m, s = 0;
	private int ah, am, as = 0;
	private boolean alarmStatus = false;
	private Semaphore mutex = new Semaphore(1);
	private Semaphore updated = new Semaphore(0);
	private AlarmThread alarm;
	private ClockOutput out;
	
	public ClockStatus(ClockOutput out) {
		this.out = out;
	}

	public void tick() throws InterruptedException {
		mutex.acquire();
		s++;
		if (s == 60) {
			s = 0;
			m++;
			if (m == 60) {
				m = 0;
				h++;
				if (h == 24)
					h = 0;
			}
		}
		
		if (alarmStatus && timeForAlarm())
		{
			alarm = new AlarmThread(out);	
			alarm.start();
		}
		
		mutex.release();
		updated.release();
	}

	private boolean timeForAlarm(){
		return ah == h && am == m && as == s;
	}
	
	public void stopAlarm() {
		if(alarm != null && alarm.isAlive()) alarm.interrupt();
	}

	public void setTime(int h, int m, int s) throws InterruptedException {
		mutex.acquire();
		this.h = h;
		this.m = m;
		this.s = s;
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

	public int[] getTime() throws InterruptedException {
		int[] temp = null;
		mutex.acquire();
		temp = new int[] { h, m, s };
		mutex.release();
		return temp;
	}

	public Semaphore updated() {
		return updated;
	}
}
