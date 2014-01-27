package pl.dbgen.entities;

import pl.dbgen.namesandpathes.SQLProcedureName;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Lukasz Raduj
 */
public class Reservation {
    private static int nextId = 1;
    private final int id = nextId++;
    private final int conferenceDayId;
    private final int clientId;
    private final int stateOfReservationId;
    private final int countOfPeople;
    private final LocalDate startingDate;
    private List<WorkshopReservation> workshopReservations = new ArrayList<>();
    private Payment payment;


    public Reservation(int conferenceDayId, int clientId, int stateOfReservationId, int countOfPeople, LocalDate startingDate) {
        this.conferenceDayId = conferenceDayId;
        this.clientId = clientId;
        this.stateOfReservationId = stateOfReservationId;
        this.countOfPeople = countOfPeople;
        this.startingDate = startingDate;
        this.payment = new Payment(this.getId(), 0, 0);
    }

    public String buildNewReservationExecString() {
        StringBuilder builder = new StringBuilder();

        builder.append(SQLProcedureName.ADD_RESERVATION);

        builder.append(conferenceDayId).append(", ")
                .append(clientId).append(", ")
                .append(countOfPeople).append(";\n");

        builder.append(buildWorkshopReservationsExecString());

        return builder.toString();
    }

    private String buildWorkshopReservationsExecString() {
        StringBuilder builder = new StringBuilder();
        for (WorkshopReservation workshopReservation : workshopReservations) {
            builder.append(workshopReservation.buildNewWorkshopReservationExecString());
        }

        return builder.toString();
    }

    public void addWorkshopReservation(WorkshopReservation reservation) {
        workshopReservations.add(reservation);
    }

    public List<WorkshopReservation> getWorkshopReservations() {
        return workshopReservations;
    }

    public int getId() {
        return id;
    }

    public int getConferenceDayId() {
        return conferenceDayId;
    }

    public int getClientId() {
        return clientId;
    }

    public int getStateOfReservationId() {
        return stateOfReservationId;
    }

    public int getCountOfPeople() {
        return countOfPeople;
    }

    public LocalDate getStartingDate() {
        return startingDate;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }
}

