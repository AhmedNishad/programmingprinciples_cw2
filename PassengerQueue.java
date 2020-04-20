package sample;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PassengerQueue {
    private ArrayList<Passenger> queueArray = new ArrayList<Passenger>();
    private int first;
    private int last;
    private int maxStayInQueue = 0;
    private int maxLength = 42;


    public HBox display(){
        HBox trainQueue = new HBox();
        trainQueue.setSpacing(10);
        Label title = new Label("Queue");
        title.setStyle("-fx-background-color:#cdffa3;");
        title.setAlignment(Pos.CENTER);
        // trainQueue.getChildren().add(title);
        for(int i = 0; i < queueArray.size(); i++){
            Passenger passenger = queueArray.get(i);
            Button pBtn = new Button(passenger.getName());
            pBtn.setStyle("-fx-background-color:#ffdea6;");
            pBtn.setId(String.valueOf(passenger.getSeatNumber()));
            trainQueue.getChildren().add(pBtn);
        }
        return trainQueue;
    }

    public void add(Passenger next) throws Exception {
        if(isFull())
            throw new Exception("Queue array is full cannot add Passenger");

        next.joinStamp = System.currentTimeMillis();
        queueArray.add(next);

        if(maxStayInQueue < queueArray.size())
            maxStayInQueue = queueArray.size();
    }

    public Passenger remove() throws Exception {
        if(isEmpty()){
            throw new Exception("Cannot Remove from Empty queue");
        }

        Passenger removed = queueArray.remove(0);
        long currentStamp = System.currentTimeMillis();
        int seconds = (int) ((currentStamp - removed.joinStamp) / 1000);
        removed.setSeconds(seconds);
        return removed;
    }

    public Passenger removeSim() throws Exception {
        if(isEmpty()){
            throw new Exception("Cannot Remove from Empty queue");
        }

        Passenger removed = queueArray.remove(0);
        return removed;
    }

    public boolean isEmpty(){
        boolean empty = false;
        if(queueArray.size() <= 0)
            empty = true;
        return empty;
    }

    public boolean isFull(){
        boolean full = false;
        if(queueArray.size() >= maxLength)
            full = true;
        return full;
    }

    public int getLength(){
        return this.queueArray.size();
    }

    public int getMaxStayInQueue(){
        return this.maxStayInQueue;
    }

    public void addDelayToAll(int del){
        for(int i = 0; i < queueArray.size(); i++){
            Passenger p = queueArray.get(i);
            p.incrementSeconds(del);
        }
    }

    public ArrayList<Passenger> getQueueArray(){
        return this.queueArray;
    }

    public void setArray(ArrayList<Passenger> passengers){
        this.queueArray = passengers;
    }

    public Passenger getAtIndex(int i){
        return this.queueArray.get(i);
    }

    public Passenger getBySeatNumber(int seat){
        int index = -1;
        for(int i = 0; i < queueArray.size(); i++){
            Passenger p = queueArray.get(i);
            if(p.getSeatNumber() == seat)
                index = i;
        }
        if(index == -1){
            return null;
        }

        return queueArray.get(index);
    }

    public ArrayList<Passenger> clear(){
        queueArray.clear();
        return  queueArray;
    }
}
