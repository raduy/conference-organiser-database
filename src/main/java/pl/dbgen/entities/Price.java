package pl.dbgen.entities;

import pl.dbgen.namesandpathes.SQLProcedureName;

/**
 * @author Lukasz Raduj
 */
public class Price {
    private static int nextId = 1;
    private final int id = nextId++;
    private final int conferenceDayId;
    private int price;
    private final int daysBeforeConferenceStart;

    public Price(int conferenceDayId, int price, int daysBeforeConferenceStart) {
        this.conferenceDayId = conferenceDayId;
        this.price = price;
        this.daysBeforeConferenceStart = daysBeforeConferenceStart;
    }

    public String buildNewPriceExecString() {
        StringBuilder builder = new StringBuilder();
        builder.append(SQLProcedureName.ADD_PRICE);

        builder.append(getConferenceDayId()).append(", ")
                .append(getPrice()).append(", ")
                .append(getDaysBeforeConferenceStart()).append(";\n");


        return builder.toString();
    }

    public int getId() {
        return id;
    }

    public int getConferenceDayId() {
        return conferenceDayId;
    }

    public int getPrice() {
        return price;
    }

    public int getDaysBeforeConferenceStart() {
        return daysBeforeConferenceStart;
    }
}
