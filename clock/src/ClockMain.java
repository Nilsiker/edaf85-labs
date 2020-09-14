import clock.AlarmClockEmulator;
import clock.io.ClockInput;
import clock.io.ClockInput.UserInput;
import clock.io.ClockOutput;

public class ClockMain {
	public static void main(String[] args) throws InterruptedException {
		AlarmClockEmulator emulator = new AlarmClockEmulator();

		ClockInput in = emulator.getInput();
		ClockOutput out = emulator.getOutput();

		ClockStatus status = new ClockStatus(out);
		TickerThread ticker = new TickerThread(status);
		TimeThread updater = new TimeThread(status, out);
		ticker.start();
		updater.start();

		while (true) {
			in.getSemaphore().acquire();
			UserInput userInput = in.getUserInput();
			int choice = userInput.getChoice();

			int h = userInput.getHours();
			int m = userInput.getMinutes();
			int s = userInput.getSeconds();

			switch (choice) {
			case ClockInput.CHOICE_SET_ALARM:
				status.setAlarm(h, m, s);
				status.stopAlarm();
				break;
			case ClockInput.CHOICE_SET_TIME:
				status.setTime(h, m, s);
				status.stopAlarm();
				break;
			case ClockInput.CHOICE_TOGGLE_ALARM:
				boolean alarmStatus = status.toggleAlarm();
				out.setAlarmIndicator(alarmStatus);
				status.stopAlarm();
			}

			System.out.println("choice=" + choice + " h=" + h + " m=" + m + " s=" + s);
		}
	}
}
