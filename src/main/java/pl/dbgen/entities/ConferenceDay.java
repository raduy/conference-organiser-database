package pl.dbgen.entities;

import pl.dbgen.namesandpathes.SQLProcedureName;
import pl.dbgen.partialgenerators.PrimitiveDataGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lukasz Raduj
 */
public class ConferenceDay {
    private static int nextId = 1;
    private final int id = nextId++;
    private final int conferenceId;
    private int seats;
    private int whichDay;
    private String shortDescription;
    private List<Workshop> workshops = new ArrayList<>();
    private int freeSeats;

    public ConferenceDay(int conferenceId, int seats, int whichDay, String shortDescription) {
        this.conferenceId = conferenceId;
        this.seats = seats;
        this.whichDay = whichDay;
        this.shortDescription = shortDescription;
        this.freeSeats = seats;
        System.out.println("conference " + conferenceId + " " + seats);
    }

    public String buildNewConferenceDayExecString() {
        StringBuilder builder = new StringBuilder();

        builder.append(SQLProcedureName.ADD_CONFERENCE_DAY);

        builder.append(conferenceId).append(", ")
                .append(seats).append(", ")
                .append(whichDay).append(", '")
                .append(shortDescription).append("';");

        return builder.toString();
    }

    public void addWorkshop(Workshop workshop) {
        workshops.add(workshop);
    }

    public int getConferenceId() {
        return conferenceId;
    }

    public int getSeats() {
        return seats;
    }

    public int getWhichDay() {
        return whichDay;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public List<Workshop> getWorkshops() {
        return workshops;
    }

    public int getId() {
        return id;
    }

    public int getFreeSeats() {
        return freeSeats;
    }

    public void setFreeSeats(int freeSeats) {
        this.freeSeats = freeSeats;
    }

    public static void main(String[] args) {
        PrimitiveDataGenerator generator = PrimitiveDataGenerator.getInstance();
        ConferenceDay conferenceDay = new ConferenceDay(1, 342, 1, generator.nextDescription());

        System.out.println(conferenceDay.buildNewConferenceDayExecString());
    }
}
