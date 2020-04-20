package sample;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

class PassengerSimulation
{
    public Random rand = new Random();
    public int batchSize;
    private PassengerSimulationManager manager;
    private int minWaiting;
    private int maxWaiting;
    private int totalDelay;
    public Passenger passenger;
    public PassengerQueue trainQueue;
    public static int simulationCount = 0;
    public int simId;
    private int nextIterationCondition;
    private ArrayList<Passenger> boardedList;
    public static int iterationDuration = 0;

    public PassengerSimulation(Passenger passenger, PassengerQueue trainQueue, PassengerSimulationManager manager, int nextIterationCondition, ArrayList<Passenger> boardedList){
        this.nextIterationCondition = nextIterationCondition;
        this.boardedList = boardedList;
        simulationCount++;
        simId = simulationCount;
        this.passenger = passenger;
        this.trainQueue = trainQueue;
        this.batchSize = batchSize;
        this.manager = manager;
    }
    public void execute() throws Exception {
        // Execute method delays for everyone in the queue, removes the current simulation and passenger from queue
        // Based on magic number of queue length it calls the next iteration from manger
        int delay = rand.nextInt(16) + 3;
        trainQueue.addDelayToAll(delay);
        try {
            Passenger removed = trainQueue.removeSim();
            boardedList.add(removed);
            passenger = removed;
        } catch (Exception e) {
             System.out.println(e.getMessage());
        }
        if(shouldIterate()){
            manager.nextIteration();
            System.out.println("Iteration Condition " + nextIterationCondition);
        }
        manager.removeSim(this, delay);
        iterationDuration += delay;
    }

    public boolean shouldIterate(){
        // Todo - Improve logic
        if(trainQueue.getLength() < nextIterationCondition && !manager.station.isWaitingRoomEmpty())
            return true;
        return  false;
    }

}