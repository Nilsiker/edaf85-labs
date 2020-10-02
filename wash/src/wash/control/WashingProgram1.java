package wash.control;

import actor.ActorThread;
import wash.io.WashingIO;


public class WashingProgram1 extends ActorThread<WashingMessage> {

    private WashingIO io;
    private ActorThread<WashingMessage> temp;
    private ActorThread<WashingMessage> water;
    private ActorThread<WashingMessage> spin;

    public WashingProgram1(WashingIO io,
                           ActorThread<WashingMessage> temp,
                           ActorThread<WashingMessage> water,
                           ActorThread<WashingMessage> spin) {
        this.io = io;
        this.temp = temp;
        this.water = water;
        this.spin = spin;
    }

    @Override
    public void run() {
        WashingMessage fillToTen = new WashingMessage(this, WashingMessage.WATER_FILL, 10);
        WashingMessage drain = new WashingMessage(this, WashingMessage.WATER_DRAIN);
        WashingMessage waterIdle = new WashingMessage(this, WashingMessage.WATER_IDLE);
        WashingMessage heatToForty = new WashingMessage(this, WashingMessage.TEMP_SET, 40);
        WashingMessage heatIdle = new WashingMessage(this, WashingMessage.TEMP_IDLE);
        WashingMessage wash = new WashingMessage(this, WashingMessage.SPIN_SLOW);
        WashingMessage centrifuge = new WashingMessage(this, WashingMessage.SPIN_FAST);
        WashingMessage spinIdle = new WashingMessage(this, WashingMessage.SPIN_OFF);


        try {
            // Lock the hatch
            io.lock(true);

            water.send(fillToTen);
            receive();


            System.out.println("Heat to 40 degrees");
            temp.send(heatToForty);
            receive();

            System.out.println("Start spin!");
            spin.send(wash);
            sleep(60000 * 30 / Settings.SPEEDUP);

            System.out.println("Turn of temperature stabilizer");
            temp.send(heatIdle);

            System.out.println("Drain");
            water.send(drain);
            receive();
            for (int i = 0; i < 5; i++) {
                System.out.println("Rinse start");
                water.send(fillToTen);
                receive();
                sleep(2 * 60000 / Settings.SPEEDUP);
                water.send(drain);
                receive();
                System.out.println("Rinse stop");
            }

            water.send(waterIdle);

            System.out.println("Centrifuge start");
            spin.send(centrifuge);
            sleep(5 * 60000);

            System.out.println("Stop spin");
            spin.send(spinIdle);


            io.lock(false);
        } catch (InterruptedException e) {

            // If we end up here, it means the program was interrupt()'ed:
            // set all controllers to idle

            temp.send(new WashingMessage(this, WashingMessage.TEMP_IDLE));
            water.send(new WashingMessage(this, WashingMessage.WATER_IDLE));
            spin.send(new WashingMessage(this, WashingMessage.SPIN_OFF));
            System.out.println("washing program terminated");
        }
    }
}
