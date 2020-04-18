package sample;

public class Passenger {
    private String firstName;
    private String surname = "";
    private int seatNumber;
    private int secondsInQueue = 0;
    public long joinStamp;

    public Passenger(String firstName, String surname, int seatNumber) throws Exception {
        if(!firstName.matches("(?<=\\s|^)[a-zA-Z]*(?=[.,;:]?\\s|$)") && !surname.matches("(?<=\\s|^)[a-zA-Z]*(?=[.,;:]?\\s|$)"))
            throw new Exception("Name cannot contain numbers");

        if(firstName.split(" ").length > 1)
            firstName = firstName.split(" ")[0];

        if(surname.split(" ").length > 1)
            surname = surname.split(" ")[0];

        setName(firstName, surname);
        if(seatNumber < 1 || seatNumber > 42)
            throw new Exception("Seat number invalid");
        this.seatNumber = seatNumber;
    }

    public Passenger(String name, int seatNumber) throws Exception {
        if(!name.matches("(?<=\\s|^)[a-zA-Z]*(?=[.,;:]?\\s|$)"))
            throw new Exception("Name cannot contain numbers");

        this.firstName = name;
        this.seatNumber = seatNumber;
    }

    public void display(){

    }

    public String getName(){
        return firstName + " " + surname;
    }

    public void setName(String name, String surname){
        this.firstName = name;
        this.surname = surname;
    }

    public int getSeconds(){
        return secondsInQueue;
    }

    public void setSeconds(int seconds){
        this.secondsInQueue = seconds;
    }

    public void incrementSeconds(int seconds){
        this.secondsInQueue += seconds;
    }

    public int getSeatNumber() {
        return this.seatNumber;
    }

    public void setSeatNumber(int number){
        if(number < 1 || number > 42)
            return;
        this.seatNumber = number;
    }

    public String getFirstName(){
        return this.firstName;
    }
    public String getSurname(){
        return this.surname;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        Passenger clone = null;
        try
        {
            clone = (Passenger) super.clone();

            //Copy new date object to cloned method
        }
        catch (CloneNotSupportedException e)
        {
            throw new RuntimeException(e);
        }
        return clone;
    }
}
