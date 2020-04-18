package sample;

import com.mongodb.BasicDBObject;
import com.mongodb.client.*;
import com.mongodb.client.model.UpdateOptions;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.awt.image.AreaAveragingScaleFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.*;

import static com.mongodb.client.model.Filters.eq;

public class TrainStation extends Application {
    public ArrayList<Passenger> waitingRoom = new ArrayList<Passenger>();
    public PassengerQueue trainQueue = new PassengerQueue();
    public ArrayList<Passenger> boardedList = new ArrayList<>();

    public ArrayList<Passenger> waitingRoomClone = new ArrayList<Passenger>();
    public ArrayList<Passenger> trainQueueArrayClone = new ArrayList<Passenger>();
    public ArrayList<Passenger> boardedArrayClone = new ArrayList<Passenger>();

    public int batchSize = 2;

    public static int HEIGHT = 600;
    public static int WIDTH = 500;
    public static FlowPane secondRoot = new FlowPane();
    public static Scene GlobalScene = new Scene (new FlowPane(), WIDTH, HEIGHT);
    public static Stage newStage = new Stage();
    @Override
    public void start(Stage primaryStage) throws Exception{
        populateWaitingList();
        batchSize = getBatchSize();
        //trainStation.populateWaitingList(TestData.passengerList);
        boolean running = true;
        Scanner scanner = new Scanner(System.in);
        String menuMessage = "A to Add Passenger, V to View Train Queue, D to Delete Passenger from queue," +
                "S to store Data, L to load Data, R to Simulate and Report, Q to Quit";
        System.out.println(menuMessage);

        // Menu
        while(running){
            System.out.println("Enter Option ");
            String option = scanner.next().toUpperCase();
            switch (option){
                case "A":
                    addPassengerToTrainQueue();
                    break;
                case "V":
                    viewTrainQueue();
                    break;
                case "D":
                    deletePassengerFromQueue();
                    break;
                case "S":
                    storeData();
                    break;
                case "L":
                    loadData();
                    break;
                case "R":
                    simulateAndReport();
                    break;
                case "Q":
                    running = false;
                    break;
                case "H":
                    System.out.println(menuMessage);
                    break;
                default:
                    System.out.println("Unknown Command, Enter H for help");
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

// region ---Functionality---
    // region Helpers
    private FlowPane generateWaitingRoom(Stage stage){
        FlowPane waitingRoomRoot = new FlowPane();
        waitingRoomRoot.setHgap(10);
        waitingRoomRoot.setVgap(10);
        waitingRoomRoot.setPadding(new Insets(0, 10, 0, 10));
        Label title = new Label("Waiting Room");
        title.setMinWidth(WIDTH);
        title.setAlignment(Pos.CENTER);
        waitingRoomRoot.getChildren().add(title);
        waitingRoomRoot.setAlignment(Pos.CENTER);

        for(int i = 0; i < waitingRoom.size(); i++){
            Passenger passenger = waitingRoom.get(i);
            Button passengerBtn = new Button(String.valueOf(passenger.getSeatNumber()));
            passengerBtn.setStyle("-fx-background-color:#a3c6ff;");
            passengerBtn.setOnMouseClicked((e)-> {
                try {
                    if(getTrainQueue().getLength() == batchSize){
                        final Stage dialog = new Stage();
                        dialog.initModality(Modality.APPLICATION_MODAL);
                        dialog.initOwner(stage);
                        VBox dialogVbox = new VBox(20);
                        dialogVbox.getChildren().add(new Label("Queue is full"));
                        Scene dialogScene = new Scene(dialogVbox, 300, 200);
                        dialog.setScene(dialogScene);
                        dialog.show();
                        return;
                    }
                    moveToQueue(passenger);
                    stage.getScene().setRoot(refreshScene(stage));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
            waitingRoomRoot.getChildren().add(passengerBtn);
        }

        return waitingRoomRoot;
    }

    private HBox generateTrainQueue(){
        HBox trainQueueRoot = new HBox();
        trainQueueRoot.setSpacing(10);
        Label title = new Label("Queue");
        title.setStyle("-fx-background-color:#cdffa3;");
        title.setAlignment(Pos.CENTER);
        // trainQueue.getChildren().add(title);
        for(int i = 0; i < trainQueue.getLength(); i++){
            Passenger passenger = trainQueue.getAtIndex(i);
            Button pBtn = new Button(passenger.getName());
            pBtn.setStyle("-fx-background-color:#ffdea6;");
            pBtn.setId(String.valueOf(passenger.getSeatNumber()));
            trainQueueRoot.getChildren().add(pBtn);
        }
        return trainQueueRoot;
    }

    private BorderPane refreshScene(Stage stage){
        BorderPane root = new BorderPane();

//        MenuItem colombo = new MenuItem("Colombo");
//        MenuItem kandy = new MenuItem("Kandy");
//        MenuItem galle = new MenuItem("Galle");
//        MenuItem jaffna = new MenuItem("Jaffna");
//        MenuButton trips = new MenuButton("Select Trip", null,colombo, galle, kandy, jaffna);

        // trips.setAlignment(Pos.CENTER);
        // root.setTop(new FlowPane(trips));
        root.setCenter(generateWaitingRoom(stage));
        root.setBottom( trainQueue.display()); //generateTrainQueue());
        Button board = new Button("Board");
        board.setPadding(new Insets(20));
        board.setStyle("-fx-background-color:#cdffa3;");
        board.setDisable(trainQueue.getLength() != batchSize);
        board.setAlignment(Pos.BOTTOM_CENTER);
        board.setOnMouseClicked((e)->{
            boardAll();
            batchSize = getBatchSize();
            // System.out.println(trainStation.getTrainQueue().getLength());
            stage.getScene().setRoot(refreshScene(stage));
        });
        Label batchCount = new Label(String.valueOf(batchSize));
        root.setLeft(new VBox(board, batchCount));
        return root;
    }
    // endregion

    public void addPassengerToTrainQueue(){
        newStage.setResizable(true);
        newStage.setTitle("Add Passengers");
        BorderPane root = new BorderPane();
//        Button stop = new Button("Select Stop");
//        stop.setAlignment(Pos.TOP_CENTER);
//        root.setTop(new HBox(stop));
        root.setCenter(generateWaitingRoom(newStage));
        root.setBottom(trainQueue.display());   //generateTrainQueue());
        Button board = new Button("Board");
        board.setOnMouseClicked((e)->{
            board();
        });
        board.setAlignment(Pos.BOTTOM_CENTER);
        root.setLeft(new VBox(board));

        Scene scene = new Scene(refreshScene(newStage), WIDTH, HEIGHT);
        newStage.setScene(scene);
        newStage.showAndWait();
    }

    public void viewTrainQueue(){
        secondRoot = new FlowPane();
        secondRoot.setStyle("-fx-font-weight:bold");
        Stage newStage = new Stage();
        newStage.setResizable(false);
        newStage.setTitle("View Seats");
        secondRoot.setAlignment(Pos.CENTER);
        secondRoot.setHgap(20);
        secondRoot.setVgap(20);
        for(int i = 1; i < 43; i++){
            Button button = new Button(i + " - Empty");
            //ffa1f6
            button.setStyle("-fx-background-color:#ffa1f6;");
            String state = "empty";
            Passenger boarded = getBoardedBySeatNumber(i);
            if(boarded != null)
                state = "boarded";
            Passenger queue = trainQueue.getBySeatNumber(i);
            if(queue != null)
                state = "queue";
            Passenger waiting = getWaitingBySeatNumber(i);
            if(waiting != null)
                state = "waiting";

            switch (state){
                case "boarded":
                    button = new Button(i + " - " + boarded.getName() +"\nBoarded in " + boarded.getSeconds() + " seconds");
                    button.setStyle("-fx-background-color:#a3c6ff;");
                    break;
                case "queue":
                    button = new Button(i + " - " + queue.getName() +"\nIn-Queue");
                    button.setStyle("-fx-background-color:#cdffa3;");
                    break;
                case "waiting":
                    button = new Button(i + " - " + waiting.getName() +"\nWaiting");
                    button.setStyle("-fx-background-color:#ffdea6;");
                    break;
            }
            button.setMinSize(80,40);
            secondRoot.getChildren().add(button);
        }

        Scene scene = new Scene (secondRoot, 600, 600);
        newStage.setScene(scene);
        newStage.showAndWait();
    }

    public  void deletePassengerFromQueue(){
        System.out.println("Enter seat number you want to remove");
        int seatNumber = new Scanner(System.in).nextInt();
        Passenger removed = removeFromQueue(seatNumber);
        if(removed == null) {
            Passenger p = removeFromBoarded(seatNumber);
            if(p == null){
                System.out.println("Could not find passenger for number");
                return;
            }
            System.out.println(p.getName() + " has been removed from boarded");
        }else{
            System.out.println(removed.getName() + " has been removed from queue");
        }
    }

    public void storeData(){
        String connectionString = System.getProperty("mongodb.uri");

        System.out.println("Saving data...");
        try (MongoClient mongoClient = MongoClients.create(connectionString)) {
            MongoDatabase sampleTrainingDB = mongoClient.getDatabase("test");
            MongoCollection<Document> stationCollection = sampleTrainingDB.getCollection("station");

            MongoCursor cursor = stationCollection.find().cursor();
            while (cursor.hasNext()) {
                stationCollection.deleteOne((Bson) cursor.next());
            }

            for(int i = 0; i < waitingRoom.size(); i++){
                Passenger passenger =  waitingRoom.get(i);

                Bson filter = eq("id", getDocumentId(passenger));

                Bson update =  new Document("$set",
                        new Document("id", getDocumentId(passenger))
                                .append("first_name", passenger.getFirstName())
                                .append("last_name", passenger.getSurname())
                                .append("seat_number", passenger.getSeatNumber())
                                .append("area", "waiting"));
                UpdateOptions options = new UpdateOptions().upsert(true);
                stationCollection.updateOne(filter, update, options);
            }
            for(int i = 0; i < trainQueue.getLength(); i++){
                Passenger passenger =  trainQueue.getAtIndex(i);

                Bson filter = eq("id", getDocumentId(passenger));

                Bson update =  new Document("$set",
                        new Document("id", getDocumentId(passenger))
                                .append("first_name", passenger.getFirstName())
                                .append("last_name", passenger.getSurname())
                                .append("seat_number", passenger.getSeatNumber())
                                .append("area", "queue"));
                UpdateOptions options = new UpdateOptions().upsert(true);
                stationCollection.updateOne(filter, update, options);
            }
            for(int i = 0; i < boardedList.size(); i++){
                Passenger passenger =  boardedList.get(i);

                Bson filter = eq("id", getDocumentId(passenger));

                Bson update =  new Document("$set",
                        new Document("id", getDocumentId(passenger))
                                .append("first_name", passenger.getFirstName())
                                .append("last_name", passenger.getSurname())
                                .append("seat_number", passenger.getSeatNumber())
                                .append("area", "boarded"));
                UpdateOptions options = new UpdateOptions().upsert(true);
                stationCollection.updateOne(filter, update, options);
            }
            System.out.println("Successfully saved data");
        }catch(Exception e){
            System.out.println("Something went wrong! Continue...");
        }
    }

    public void loadData(){
        String connectionString = System.getProperty("mongodb.uri");

        resetTrainStation();

        System.out.println("Loading data...");
        try (MongoClient mongoClient = MongoClients.create(connectionString)) {
            MongoDatabase sampleTrainingDB = mongoClient.getDatabase("test");
            MongoCollection<Document> stationCollection = sampleTrainingDB.getCollection("station");

            List<Document> passengers = stationCollection.find().into(new ArrayList<>());

            for(int i = 0; i < passengers.size(); i++){
                Document doc = passengers.get(i);
                Passenger passenger;
                String surname = doc.get("last_name").toString();
                String area = doc.get("area").toString();
                if(surname.equals("")){
                    passenger = new Passenger(doc.get("first_name").toString() ,Integer.valueOf(doc.get("seat_number").toString()));
                }else{
                    passenger = new Passenger(doc.get("first_name").toString() ,doc.get("last_name").toString(),Integer.valueOf(doc.get("seat_number").toString()));
                }

                if(area.equals("waiting")){
                    addToWaitingRoom(passenger);
                }else if(area.equals("queue")){
                    addToQueue(passenger);
                }else if(area.equals("boarded")){
                    addToBoarding(passenger);
                }
            }
            System.out.println("Load successful");
        }catch(Exception e){
            System.out.println("Oops! Something went wrong.");
        }
    }

    public void simulateAndReport() throws IOException {
        // Populate train station waiting room with passengers
        populateWaitingList(TestData.passengerList);
        int initialWaitingSize = getWaitingRoomLength();

        PassengerSimulationManager manager = null;
        try {
            manager = new PassengerSimulationManager(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        manager.iterationCount = 0;
        // region Intuitive Implementation
//        while(true){
//            if(isWaitingRoomEmpty())
//                break;
//
//            // Randomize how many passengers join train queue at a time
//            // Move said passengers to train queue
//            int passengerBatch = getBatchSize();
//            //System.out.println("Taking " + passengerBatch + " out of waiting list");
//            moveToTrainQueue(passengerBatch);
//
//            for(int i = 0; i < passengerBatch; i++) {
//                // Randomize processing delay and add next to board
//                int delay = rand.nextInt(16) + 3;
//                if(delay < minWaiting)
//                    minWaiting = delay;
//                if(delay > maxWaiting)
//                    maxWaiting = delay;
//                //System.out.println("Delay of " + delay);
//                totalDelay += delay;
//                // Add Processing Delay to all train queue
//                trainQueue.addDelayToAll(delay);
//                // Remove next passenger from queue
//                try {
//                    trainQueue.remove();
//                } catch (Exception e) {
//                    // System.out.println("Empty");
//                }
//            }
//        }
        //endregion

        double avgTime = 0;
        if(initialWaitingSize != 0){
            avgTime = manager.totalDelay/ initialWaitingSize;
        }
        newStage = new Stage();
        VBox dialogVbox = new VBox(20);
        dialogVbox.setAlignment(Pos.CENTER);
        dialogVbox.getChildren().add(new Label("Average waiting time"));
        dialogVbox.getChildren().add(new Label(String.valueOf(avgTime)));
        dialogVbox.getChildren().add(new Label("Maximum length of the queue"));
        dialogVbox.getChildren().add(new Label(String.valueOf(trainQueue.getMaxStayInQueue())));
        dialogVbox.getChildren().add(new Label("Maximum waiting time in queue"));
        dialogVbox.getChildren().add(new Label(String.valueOf(manager.maxWaiting)));
        dialogVbox.getChildren().add(new Label("Minimum waiting time in queue"));
        dialogVbox.getChildren().add(new Label(String.valueOf(manager.minWaiting)));
        Scene dialogScene = new Scene(dialogVbox, 300, 400);
        newStage.setScene(dialogScene);
        newStage.showAndWait();

//        System.out.println("Max length " + trainQueue.getMaxStayInQueue());
//        System.out.println("Average time stayed in queue " + avgTime );

        FileWriter fw = new FileWriter("simulation.txt",true);
        fw.append("\n\n" + new Timestamp(System.currentTimeMillis()) + " - Maximum Length : " + avgTime + "\n, Average time in queue : " + trainQueue.getMaxStayInQueue() + "\n, " +
                "Maximum time in queue : " + manager.maxWaiting + "\n, Minimum time in queue : " + manager.minWaiting);
        fw.close();
        //print a summary of the maximum length of the queue attained, the maximum
        // waiting time, the minimum waiting time, and the average waiting time of all the passengers.
        System.out.println("Refresh Underway!");
        populateWaitingList();
    }
    //endregion

//region --- State Methods ---
    //region Get
    public int getWaitingRoomLength(){
        return this.waitingRoom.size();
    }
    public Passenger getBoardedBySeatNumber(int seatNumber){
        int index = -1;
        for(int i = 0; i < boardedList.size(); i++){
            Passenger p = boardedList.get(i);
            if(p.getSeatNumber() == seatNumber)
                index = i;
        }
        if(index == -1){
            return null;
        }

        return boardedList.get(index);
    }

    public Passenger getWaitingBySeatNumber(int seatNumber){
        int index = -1;
        for(int i = 0; i < waitingRoom.size(); i++){
            Passenger p = waitingRoom.get(i);
            if(p.getSeatNumber() == seatNumber)
                index = i;
        }
        if(index == -1){
            return null;
        }

        return waitingRoom.get(index);
    }

    public PassengerQueue getTrainQueue(){
        return this.trainQueue;
    }
    //endregion

    //region Add
    public void addToWaitingRoom(Passenger passenger){
        waitingRoom.add(passenger);
    }

    public void addToQueue(Passenger passenger) throws Exception {
        trainQueue.add(passenger);
    }

    public void addToBoarding(Passenger passenger){
        boardedList.add(passenger);
    }
    //endregion

    //region Remove
    public Passenger removeFromQueue(int seatNumber){
        int index = -1;
        for(int i = 0; i < trainQueue.getLength(); i++){
            Passenger p = trainQueue.getAtIndex(i);
            if(p.getSeatNumber() == seatNumber)
                index = i;
        }
        if(index == -1){
            return null;
        }
        Passenger removed = trainQueue.getQueueArray().remove(index);
        waitingRoom.add(removed);
        return removed;
    }

    public Passenger removeFromBoarded(int seatNumber){
        int index = -1;
        for(int i = 0; i < boardedList.size(); i++){
            Passenger p = boardedList.get(i);
            if(p.getSeatNumber() == seatNumber)
                index = i;
        }
        if(index == -1){
            return null;
        }
        Passenger removed = boardedList.remove(index);
        waitingRoom.add(removed);
        return removed;
    }
    //endregion

    //region Helpers
    private void clearAll(){
        trainQueue.clear();
        waitingRoom.clear();
        boardedList.clear();
    }

    public void populateWaitingList(ArrayList<Passenger> passengerList){
        this.waitingRoom = copyPassengers(passengerList);
    }

    private void populateWaitingList(){
        clearAll();
        String connectionString = System.getProperty("mongodb.uri");

        System.out.println("Loading data...");
        try (MongoClient mongoClient = MongoClients.create(connectionString)) {
            MongoDatabase sampleTrainingDB = mongoClient.getDatabase("test");
            MongoCollection<Document> bookingCollection = sampleTrainingDB.getCollection("bookings");

            // List<Document> bookings = bookingCollection.find().into(new ArrayList<>());
            BasicDBObject searchQuery = new BasicDBObject();
            LocalDate date = LocalDate.now();
            searchQuery.put("booked_date", date.toString());
            MongoCursor cursor = bookingCollection.find(searchQuery).cursor();

            ArrayList<Passenger> passengers = new ArrayList<Passenger>();
            while (cursor.hasNext()) {
                Document doc = (Document) cursor.next();
                //System.out.println(doc);
                String firstName = "";
                String lastName = "";

                // Johny
                String[] name = doc.get("customer_name").toString().split(" ");

                if(name.length == 1){
                    firstName = name[0];
                }else{
                    firstName = name[0];
                    lastName = name[1];
                }

                Passenger passenger = new Passenger(firstName, lastName ,Integer.valueOf(doc.get("seat_number").toString()));
                if(getWaitingBySeatNumber(passenger.getSeatNumber()) != null){
                    System.out.println("Already entered for seat number " + passenger.getSeatNumber());
                    return;
                }
                passengers.add(passenger);
            }

            populateWaitingList(passengers);
            System.out.println("Load successful");
        }catch(Exception e){
            System.out.println(e);
            System.out.println("Oops! Something went wrong.");
        }
    }

    public void board(){
        Passenger boarded = null;
        try {
            boarded = trainQueue.remove();
        } catch (Exception e) {
            e.printStackTrace();
        }
        boardedList.add(boarded);
    }

    public void boardAll(){
        while(!trainQueue.isEmpty()){
            try {
                boardedList.add(trainQueue.remove());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void resetTrainStation(){
        waitingRoom.clear();
        trainQueue.clear();
        boardedList.clear();
    }

    public ArrayList<Passenger> moveToTrainQueue(int passengerCount){
        ArrayList<Passenger> moved = new ArrayList<>();
        if(waitingRoom.size() < 1 && waitingRoom.size() < passengerCount)
            throw new Error("Cannot move to train queue");

        for(int i = 0; i < passengerCount; i++){
            try{
                Passenger next = waitingRoom.remove(i);
                trainQueue.add(next);
                moved.add(next);
            }catch(Exception e){
                break;
            }
        }
        return moved;
    }

    public void moveToQueue(Passenger passenger) throws Exception {
        Passenger next = waitingRoom.remove(waitingRoom.indexOf(passenger));
        trainQueue.add(next);
    }

    public boolean isWaitingRoomEmpty(){
        if(this.waitingRoom.size() == 0)
            return  true;

        return false;
    }
    //endregion
//endregion

// region --- Utility ---
    public int getBatchSize(){
        if(6 > getWaitingRoomLength()){
            return getWaitingRoomLength();
        }else{
            return new Random().nextInt(6) + 1;
        }
    }

    public String getDocumentId(Passenger passenger){
        return passenger.getName()+passenger.getSeatNumber();
    }

    public ArrayList<Passenger> copyPassengers(ArrayList<Passenger> list){
        ArrayList<Passenger> passengerListClone = new ArrayList<Passenger>();
        Iterator<Passenger> iterator = list.iterator();

        while(iterator.hasNext())
        {
            passengerListClone.add(iterator.next());
        }
        return  passengerListClone;
    }
    //endregion
}
