package lift;

public class LiftThread extends Thread {
    LiftMonitor lift;
    LiftView view;
    int currentFloor = 0;
    int nextFloor = 0;

    public LiftThread(LiftView view, LiftMonitor lift) {
        this.view = view;
        this.lift = lift;
    }

    @Override
    public void run() {
        try {
            while (true) {
                if (lift.open()) {
                    lift.close();
                }

                nextFloor = lift.proceed();
                view.moveLift(currentFloor, nextFloor);
                currentFloor = nextFloor;
            }
        } catch (InterruptedException e) {
            // nothing
        }
    }
}
