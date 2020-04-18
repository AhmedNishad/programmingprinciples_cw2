package sample;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestData {
    public static ArrayList<Passenger> passengerList;

    static {
        try {
            passengerList = new ArrayList<Passenger>(
                    Arrays.asList(new Passenger("Calvin", "Harris",1),
                            new Passenger("Harry", "Styles",2),
                            new Passenger("Tinie", "Tempah",3),
                            new Passenger("Ed", "Sheeran",4),
                            new Passenger("Shawn", "Mendes",5),
                            new Passenger("Chris", "Martin",6),
                            new Passenger("Rita", "Ora",7),
                            new Passenger("Guy", "Barryman",8),
                            new Passenger("Johny", "Buckland",9),
                            new Passenger("The", "Script",10),
                            new Passenger("Imagine", "Dragons",11),
                            new Passenger("Will", "Champion",12),
                            new Passenger("camlla", "cabello",13),
                            new Passenger("Bey", "Once",14),
                            new Passenger("Jayy", "Zee",15),
                            new Passenger("Marshall", "Mathers",16),
                            new Passenger("Taylor", "Swift",17),
                            new Passenger("Justin", "Bieber",18),
                            new Passenger("Aviici", "Died", 19),
                            new Passenger("Kygo", "DJ", 20),
                            new Passenger("Twenty-One", "Pilots", 21),
                            new Passenger("More", "Jappy", 22),
                            new Passenger("Legend", "Legend", 23),
                            new Passenger("Edward", "Maya", 24),
                            new Passenger("DJ", "Snake", 25),
                            new Passenger("Majer", "Lazer", 26),
                            new Passenger("Anirudh", "", 27),
                            new Passenger("One", "Republic", 28),
                            new Passenger("Sanuka", "", 29),
                            new Passenger("Bathiya", "Santhus", 30)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
