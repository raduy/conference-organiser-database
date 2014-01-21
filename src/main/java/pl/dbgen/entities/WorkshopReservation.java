package pl.dbgen.entities;

import pl.dbgen.namesandpathes.SQLProcedureName;

/**
 * @author Lukasz Raduj
 */
public class WorkshopReservation {
    private static int nextId = 1;
    private final int id = nextId++;
    private final int workshopId;
    private final int reservationId;
    private final int countOfPeople;

    public WorkshopReservation(int workshopId, int reservationId, int countOfPeople) {
        this.workshopId = workshopId;
        this.reservationId = reservationId;
        this.countOfPeople = countOfPeople;
    }

    public String buildNewWorkshopReservationExecString() {
        StringBuilder builder = new StringBuilder();

        builder.append(SQLProcedureName.ADD_WORKSHOP_RESERVATION);

        builder.append(workshopId).append(", ")
                .append(reservationId).append(", ")
                .append(countOfPeople).append(";\n");

        return builder.toString();
    }

    public int getId() {
        return id;
    }

    public int getWorkshopId() {
        return workshopId;
    }

    public int getReservationId() {
        return reservationId;
    }

    public int getCountOfPeople() {
        return countOfPeople;
    }
}
