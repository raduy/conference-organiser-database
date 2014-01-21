package pl.dbgen.entities;

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
