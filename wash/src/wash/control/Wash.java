package wash.control;

import actor.ActorThread;
import wash.io.WashingIO;
import wash.simulation.WashingSimulator;

public class Wash {

    public static void main(String[] args) throws InterruptedException {
        WashingSimulator sim = new WashingSimulator(Settings.SPEEDUP);

        WashingIO io = sim.startSimulation();

        TemperatureController temp = new TemperatureController(io);
        WaterController water = new WaterController(io);
        SpinController spin = new SpinController(io);

        temp.start();
        water.start();
        spin.start();


        ActorThread<WashingMessage> currProg = null;

        while (true) {
            int n = io.awaitButton();
            System.out.println("user selected program " + n);

            // if the user presses buttons 1-3, start a washing program
            switch (n) {
                case 1 -> currProg = new WashingProgram1(io, temp, water, spin);
                case 2 -> currProg = new WashingProgram2(io, temp, water, spin);
                case 3 -> currProg = new WashingProgram3(io, temp, water, spin);
            }

            // if the user presses button 0, and a program has been started, stop it
            if (n != 0)
                currProg.start();
            else if (currProg.isAlive()){
                currProg.interrupt();
                currProg.join();
            }
        }
    }
};
