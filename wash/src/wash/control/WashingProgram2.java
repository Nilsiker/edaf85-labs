    package wash.control;

import actor.ActorThread;
import wash.io.WashingIO;

/**
 * Program 3 for washing machine. This also serves as an example of how washing
 * programs can be structured.
 * 
 * This short program stops all regulation of temperature and water levels,
 * stops the barrel from spinning, and drains the machine of water.
 * 
 * It can be used after an emergency stop (program 0) or a power failure.
 */
public class WashingProgram2 extends ActorThread<WashingMessage> {

    private WashingIO io;
    private ActorThread<WashingMessage> temp;
    private ActorThread<WashingMessage> water;
    private ActorThread<WashingMessage> spin;

    public WashingProgram2(WashingIO io,
                           ActorThread<WashingMessage> temp,
                           ActorThread<WashingMessage> water,
                           ActorThread<WashingMessage> spin) 
    {
        this.io = io;
        this.temp = temp;
        this.water = water;
        this.spin = spin;
    }
    
    @Override
    public void run() {
        try {
            io.lock(true);
            wash(20, 40);
            wash(30,60);
            coldRinse(2,5);
            centrifugeSafely(5);
            unlockHatchSafely();
        } catch (InterruptedException e) {
            
            // If we end up here, it means the program was interrupt()'ed:
            // set all controllers to idle

            temp.send(new WashingMessage(this, WashingMessage.TEMP_IDLE));
            water.send(new WashingMessage(this, WashingMessage.WATER_IDLE));
            spin.send(new WashingMessage(this, WashingMessage.SPIN_OFF));
            System.out.println("washing program terminated");
        }
    }

    private void wash(int duration, int temperature) throws InterruptedException {
        // Fill
        water.send(fillTo(10));
        receive();

        // Heat
        temp.send(heatTo(temperature));
        receive();

        // Start spinning for duration mins
        spin.send(m(WashingMessage.SPIN_SLOW));
        receive();
        sleep(60000 * duration / Settings.SPEEDUP);

        spin.send(m(WashingMessage.SPIN_OFF));
        receive();

        // Turn off heating
        temp.send(m(WashingMessage.TEMP_IDLE));
        receive();

        water.send(m(WashingMessage.WATER_DRAIN));
        receive();
    }
    private void coldRinse(int duration, int times) throws InterruptedException {
        // Turn off heating
        temp.send(m(WashingMessage.TEMP_IDLE));
        receive();
        for (int i = 0; i < times; i++) {
            water.send(m(WashingMessage.WATER_DRAIN));
            receive();
            water.send(fillTo(10));
            receive();
            sleep(duration * 60000 / Settings.SPEEDUP);
        }
    }
    private void centrifugeSafely(int min) throws InterruptedException {
        water.send(m(WashingMessage.WATER_DRAIN));
        receive();
        // Centrifuge for min minutes!
        spin.send(m(WashingMessage.SPIN_FAST));
        receive();
        sleep(min * 60000 / Settings.SPEEDUP);
    }
    private void unlockHatchSafely() throws InterruptedException {
        water.send(m(WashingMessage.WATER_DRAIN));
        receive();

        // Close drain
        water.send(m(WashingMessage.WATER_IDLE));

        // Turn off spinning
        spin.send(m(WashingMessage.SPIN_OFF));
        receive();

        // Lock hatch
        io.lock(false);
    }



    private WashingMessage heatTo(double value){
        return new WashingMessage(this, WashingMessage.TEMP_SET, value);
    }
    private WashingMessage fillTo(double value){
        return new WashingMessage(this, WashingMessage.WATER_FILL, value);
    }
    private WashingMessage m(int type){
        return new WashingMessage(this, type);
    }
}
