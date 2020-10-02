package wash.control;

import actor.ActorThread;
import wash.io.WashingIO;

public class WaterController extends ActorThread<WashingMessage> {

    WashingIO io;
    int currentMode = -1;
    int dt = 2;
    private final double FILL_RATE = -1f;
    private final double DRAIN_RATE = 0.2f;
    private double targetLevel = 0;

    public WaterController(WashingIO io) {
        this.io = io;
    }

    @Override
    public void run() {
        try {
            while (true) {


                WashingMessage m = receiveWithTimeout(dt * 1000 / Settings.SPEEDUP);

                if (m != null) {
                    int c = m.getCommand();
                    switch (c) {
                        case WashingMessage.WATER_FILL:
                            targetLevel = m.getValue();
                            io.fill(true);
                            while (io.getWaterLevel() + FILL_RATE * dt < targetLevel);    // TODO busy wait
                            io.fill(false);
                            m.getSender().send(new WashingMessage(this, WashingMessage.ACKNOWLEDGMENT));
                            break;
                        case WashingMessage.WATER_DRAIN:
                            targetLevel = 0;
                            io.drain(true);
                            while (io.getWaterLevel() - DRAIN_RATE * dt > targetLevel);  // TODO busy wait
                            io.drain(false);
                            m.getSender().send(new WashingMessage(this, WashingMessage.ACKNOWLEDGMENT)); // TODO message when empty
                    }
                    currentMode = c;
                }
            }
        } catch (
                InterruptedException e) {
            e.printStackTrace();
        }
    }
}
