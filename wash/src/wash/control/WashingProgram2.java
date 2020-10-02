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
            water.send(new WashingMessage(this, WashingMessage.WATER_FILL, 10));
            receive();
            temp.send(new WashingMessage(this, WashingMessage.TEMP_SET, 40));
            receive();
            receive();
            receive();


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
