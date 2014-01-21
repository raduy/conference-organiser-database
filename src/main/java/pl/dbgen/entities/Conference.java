package pl.dbgen.entities;

import pl.dbgen.namesandpathes.SQLProcedureName;
import pl.dbgen.partialgenerators.PrimitiveDataGenerator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Lukasz Raduj
 */
public class Conference {
    private static int nextId = 1;
    private final int id = nextId++;
    private final LocalDate startingDate;
    private final int countOfDays;
    private final String shortDescription;
    private List<ConferenceDay> conferenceDays = new ArrayList<>();

    public Conference(LocalDate startingDate, int countOfDays, String shortDescription) {
        this.startingDate = startingDate;
        this.countOfDays = countOfDays;
        this.shortDescription = shortDescription;
    }

    public String buildConferenceExecString() {
        StringBuilder builder = new StringBuilder();

        builder.append(SQLProcedureName.ADD_CONFERENCE);

        builder.append("'").append(startingDate).append("', ")
                .append(countOfDays).append(", '")
                .append(shortDescription).append("';");

        return builder.toString();
    }

    public void addConferenceDay(ConferenceDay conferenceDay) {
        conferenceDays.add(conferenceDay);
    }

    public LocalDate getStartingDate() {
        return startingDate;
    }

    public int getCountOfDays() {
        return countOfDays;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public List<ConferenceDay> getConferenceDays() {
        return conferenceDays;
    }

    public int getId() {
        return id;
    }

    public static void main(String[] args) {
        PrimitiveDataGenerator generator = PrimitiveDataGenerator.getInstance();
        Conference conference = new Conference(LocalDate.now(), 4, generator.nextDescription());
        System.out.println(conference.getId());
        System.out.println(conference.buildConferenceExecString());
    }

}
