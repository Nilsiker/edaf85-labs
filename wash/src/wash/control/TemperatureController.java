package wash.control;

import actor.ActorThread;
import wash.io.WashingIO;

public class TemperatureController extends ActorThread<WashingMessage> {

    /*
        dt = 10 s
        mu = 0,478  -> 0,678 med marginal. (grader celsius)
        ml = 0,00952
     */

    int dt = 10;
    double mu = 0.678;
    double ml = 0.0952;
    double targetTemp = 20;
    int currentMode = WashingMessage.TEMP_IDLE;
    boolean tempReached = false;


    WashingIO io;
    // TODO: add attributes

    public TemperatureController(WashingIO io) {
        this.io = io;
    }

    @Override
    public void run() {
        try {
            ActorThread<WashingMessage> tempRequester = null;
            while (true) {
                WashingMessage m = receiveWithTimeout(dt * 1000 / Settings.SPEEDUP);
                if (m != null) {
                    tempRequester = m.getSender();
                    int c = m.getCommand();
                    switch (c) {
                        case WashingMessage.TEMP_IDLE:
                            io.heat(false);
                            m.getSender().send(new WashingMessage(this, WashingMessage.ACKNOWLEDGMENT));
                            break;
                        case WashingMessage.TEMP_SET:
                            targetTemp = m.getValue();
                            tempReached = false;
                    }
                    currentMode = c;
                }

                // Periodically check temperature
                if (currentMode == WashingMessage.TEMP_SET) {
                    double currentTemp = io.getTemperature();
                    double lowerBound = targetTemp - 2 + ml;
                    double upperBound = targetTemp - mu;

                    if (currentTemp < lowerBound)
                        io.heat(true);
                    else if (currentTemp > upperBound) {
                        io.heat(false);
                        if(!tempReached){
                            tempReached = true;
                            System.out.println("Temp reached!");
                            tempRequester.send(new WashingMessage(this, WashingMessage.ACKNOWLEDGMENT));
                        }
                    }

                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
