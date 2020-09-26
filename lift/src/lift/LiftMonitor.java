package lift;


public class LiftMonitor {

    int floor = 0, load = 0, direction = 1, enteringLift = 0, exitingLift = 0;
    boolean moving = true;

    LiftView view;
    int[] waitEntry = new int[LiftView.NBR_FLOORS],
            waitExit = new int[LiftView.NBR_FLOORS];


    public LiftMonitor(LiftView view) {
        this.view = view;
    }

    public synchronized boolean open() {
        if ((waitEntry[floor] > 0 && load < 4) || waitExit[floor] > 0) {
            System.out.println("Lift doors opening");
            moving = false;
            view.openDoors(floor);
            notifyAll();
            return true;
        }
        return false;
    }

    public synchronized void close() throws InterruptedException {
        while (waitExit[floor] > 0
                || waitEntry[floor] > 0
                && load != 4) wait();
        System.out.println("Lift doors closing");
        view.closeDoors();
        moving = true;
    }

    public synchronized int proceed() throws InterruptedException {
        while(noPeopleWaiting()
                && load != 4) wait();
        System.out.println("Lift proceeding to floor " + (floor+direction));
        floor += direction;
        if(floor==0 || floor==LiftView.NBR_FLOORS-1) direction *= -1;
        return floor;
    }

    public synchronized void arrive(Passenger pass) {
        int start = pass.getStartFloor();
        System.out.println("Calling lift from " + start);
        waitEntry[start]++;
        notifyAll();
    }

    public synchronized void enter(Passenger pass) throws InterruptedException {
        int start = pass.getStartFloor();
        while(load + enteringLift - exitingLift == 4
                || moving
                || floor != start) wait();
        enteringLift++;
        System.out.println("Entering lift on " + pass.getDestinationFloor());
        notifyAll();
    }

    public synchronized void entered(Passenger pass){
        enteringLift--;
        waitEntry[pass.getStartFloor()]--;
        waitExit[pass.getDestinationFloor()]++;
        load++;
        notifyAll();
    }

    public synchronized void leave(Passenger pass) throws InterruptedException {
        int dest = pass.getDestinationFloor();
        while(floor != dest
                || moving) wait();
        exitingLift++;
        System.out.println("Exiting lift on " + dest);
        notifyAll();
    }

    public synchronized void left(Passenger pass) {
        load--;
        exitingLift--;
        waitExit[floor]--;
        notifyAll();
    }

    private boolean noPeopleWaiting() {
        for (int i = 0; i < LiftView.NBR_FLOORS; i++) {
            if(waitEntry[i] + waitExit[i] > 0) return false;
        }
        return true;
    }


}
