package sample;

import java.util.ArrayList;
import java.util.Random;

public class PassengerSimulationManager {

    ArrayList<PassengerSimulation> simulationList = new ArrayList<PassengerSimulation>();
    public TrainStation station;
    public int minWaiting = 18;
    public int maxWaiting = 0;
    public int totalDelay = 0;
    public static int iterationCount = 0;
    public boolean running = true;
    private int currentBatchSize = 0;
    private int pastBatchSize = 0;
    private int iterationTime = 0;

    public PassengerSimulationManager(TrainStation station) throws Exception {
        this.station = station;

        // create an initial batch
        int initialBatch = 4;
        currentBatchSize = initialBatch;
        System.out.println("Initial iteration of size " + initialBatch);
        // Add the simlation objects
        for(int i = 0; i < initialBatch; i++) {
            Passenger passenger = station.getWaitingBySeatNumber(i);
            createAndAddSimulation(passenger);
        }
        iterationCount++;
        // Move the passengers
        station.moveToTrainQueue(initialBatch);
        // Iterate over simulationlist
        // PassengerSimulation can have a method that executes

        // Loop goes on until simulationlist is empty
        while(shouldIterate()){
            PassengerSimulation simulation = simulationList.get(0);
            simulation.execute();
        }
        PassengerSimulation.simulationCount = 0;
    }

    public boolean shouldIterate(){
        if(simulationList.size() > 0 && running)
            return  true;
        return false;
    }

    public void removeSim(PassengerSimulation removedSimulation, int delay){
        if(removedSimulation == null){
            running = false;
            return;
        }
        if(delay < minWaiting)
            minWaiting = delay;
        if(delay > maxWaiting)
            maxWaiting = delay;
        totalDelay += delay;
        int i = simulationList.indexOf(removedSimulation);
        System.out.println(removedSimulation.simId + " Removing simulation for " + removedSimulation.passenger.getName() + " waiting time " + delay);
        simulationList.remove(i);
    }

    public void nextIteration() throws Exception {
        if(station.isWaitingRoomEmpty()){
            throw new Exception("Waiting room is empty");
        }

        int nextBatch = station.getBatchSize();
        pastBatchSize = currentBatchSize;
        currentBatchSize = nextBatch;
        PassengerSimulation.iterationDuration = 0;
        System.out.println("Next iteration of size " + nextBatch);
        ArrayList<Passenger> moved = station.moveToTrainQueue(nextBatch);
        moved.forEach(passenger -> {
            createAndAddSimulation(passenger);
        });
        iterationCount++;
    }

    public int generateCondition(int pastBatch, int currentBatch){
        return (pastBatch + currentBatch)/2;
    }

    public void createAndAddSimulation(Passenger passenger){
        //new Random().nextInt(5)+ 1
        PassengerSimulation simulation = new PassengerSimulation(passenger, station.getTrainQueue(), this, generateCondition(pastBatchSize,currentBatchSize), station.boardedList);
        simulationList.add(simulation);
    }
}
