package wash.control;

import actor.ActorThread;
import wash.io.WashingIO;

public class WaterController extends ActorThread<WashingMessage> {

    WashingIO io;
    int currentMode = -1;
    int dt = 2;
    private final double FILL_RATE = 0.1f;
    private final double DRAIN_RATE = 0.2f;
    private double targetLevel = 0;
    private boolean levelReached = false;

    public WaterController(WashingIO io) {
        this.io = io;
    }

    @Override
    public void run() {
        try {
            ActorThread<WashingMessage> waterRequester = null;
            while (true) {

                WashingMessage m = receiveWithTimeout(dt * 1000 / Settings.SPEEDUP);

                if (m != null) {
                    waterRequester = m.getSender();
                    int c = m.getCommand();
                    switch (c) {
                        case WashingMessage.WATER_FILL:
                            targetLevel = m.getValue();
                            io.drain(false);
                            io.fill(true);
                            break;
                        case WashingMessage.WATER_DRAIN:
                            targetLevel = 0;
                            io.fill(false);
                            io.drain(true);
                            break;
                        case WashingMessage.WATER_IDLE:
                            io.drain(false);
                            io.fill(false);
                    }
                    levelReached = false;
                    currentMode = c;
                }

                // Periodically check water level
                if (currentMode == WashingMessage.WATER_FILL ) {
                    if (io.getWaterLevel() + FILL_RATE * dt > targetLevel  && !levelReached) {
                        io.fill(false);
                        levelReached = true;
                        waterRequester.send(new WashingMessage(this, WashingMessage.ACKNOWLEDGMENT));
                    }
                } else if (currentMode == WashingMessage.WATER_DRAIN) {
                    if (io.getWaterLevel() - DRAIN_RATE * dt < 0 && !levelReached) {
                        levelReached = true;
                        waterRequester.send(new WashingMessage(this, WashingMessage.ACKNOWLEDGMENT));
                    }
                }
            }
        } catch (
                InterruptedException e) {
            e.printStackTrace();
        }
    }
}
