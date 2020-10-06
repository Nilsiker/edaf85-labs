package wash.control;

import actor.ActorThread;
import wash.io.WashingIO;

public class SpinController extends ActorThread<WashingMessage> {

    // TODO: add attributes
    WashingIO io;
    ActorThread<WashingMessage> programThread;

    public SpinController(WashingIO io) {
        this.io = io;
        this.programThread = programThread;
    }

    @Override
    public void run() {
        try {

            int direction = WashingIO.SPIN_LEFT;
            int currentMode = WashingMessage.SPIN_OFF;

            while (true) {
                // wait for up to a (simulated) minute for a WashingMessage
                WashingMessage m = receiveWithTimeout(60000 / Settings.SPEEDUP);

                // if m is null, it means a minute passed and no message was received
                if (m != null && m.getCommand() <= WashingMessage.SPIN_FAST) {
                    System.out.println("got " + m);
                    int c = m.getCommand();
                    switch (c) {
                        case WashingMessage.SPIN_OFF -> io.setSpinMode(WashingIO.SPIN_IDLE);
                        case WashingMessage.SPIN_SLOW -> io.setSpinMode(WashingIO.SPIN_LEFT);
                        case WashingMessage.SPIN_FAST -> io.setSpinMode(WashingIO.SPIN_FAST);
                    }
                    currentMode = c;
                    m.getSender().send(new WashingMessage(this, WashingMessage.ACKNOWLEDGMENT));
                }

                if (currentMode == WashingMessage.SPIN_SLOW) {
                    direction += (direction == WashingIO.SPIN_LEFT) ? 1 : -1;
                    io.setSpinMode(direction);
                }

            }
        } catch (
                InterruptedException unexpected) {
            // we don't expect this thread to be interrupted,
            // so throw an error if it happens anyway
            throw new Error(unexpected);
        }
    }
}
