package programs;

import lift.*;

public class LiftProgram {
	public static void main(String[] args) {
		LiftView view = new LiftView();
		Lift lift = new Lift();
		
		for (int i = 0; i < 5; i++) {
			new PassengerThread(view, lift).start();
		}
	
	}
}
