package programs;


import java.util.HashSet;
import java.util.Set;

import lift.*;

public class LiftProgram {
	public static void main(String[] args) {
		LiftView view = new LiftView();
		LiftMonitor lift = new LiftMonitor(view);
		Set<Thread> pts = new HashSet<>();

		Thread lt = new LiftThread(view, lift);
		lt.start();

		for (int i = 0; i < 50; i++) {
			pts.add(new PassengerThread(view, lift));
		}
		
		for(Thread pt : pts) pt.start();
	}
}
