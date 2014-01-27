package pl.dbgen.entities;

import pl.dbgen.namesandpathes.SQLProcedureName;
import pl.dbgen.partialgenerators.PrimitiveDataGenerator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Lukasz Raduj
 */
public class Workshop {
    private static int nextId = 1;
    private final int id = nextId++;
    private final int conferenceDayId;
    private int seats;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private int pricePerPerson;
    private final String shortDescription;
    private int freeSeats;

    public Workshop(int conferenceDayId, int seats, LocalDateTime startTime, LocalDateTime endTime, int pricePerPerson, String shortDescription) {
        this.conferenceDayId = conferenceDayId;
        this.seats = seats;
        this.startTime = startTime;
        this.endTime = endTime;
        this.pricePerPerson = pricePerPerson;
        this.shortDescription = shortDescription;
        this.freeSeats = seats;
    }

    public String buildWorkshopExecString() {
        StringBuilder builder = new StringBuilder();

        builder.append(SQLProcedureName.ADD_WORKSHOP);

        builder.append(conferenceDayId).append(", ")
                .append(seats).append(", '")
                .append(startTime.format(DateTimeFormatter.ISO_DATE_TIME)).append("', '")
                .append(endTime.format(DateTimeFormatter.ISO_DATE_TIME)).append("', ")
                .append(pricePerPerson).append(", '")
                .append(shortDescription).append("';\n");

        return builder.toString();
    }

    public int getConferenceDayId() {
        return conferenceDayId;
    }

    public int getSeats() {
        return seats;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public int getPricePerPerson() {
        return pricePerPerson;
    }

    public String getShortDescription() {
        return shortDescription;
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
}
