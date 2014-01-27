package pl.dbgen.partialgenerators;

import pl.dbgen.entities.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Lukasz Raduj
 */
public class ReservationGenerator {
    private final static PrimitiveDataGenerator gen = PrimitiveDataGenerator.getInstance();

    public static List<Reservation> generateCompleteReservationsForConference(Conference conference, List<Client> clients) {
        List<Reservation> reservations = new ArrayList<>();

        for (ConferenceDay conferenceDay : conference.getConferenceDays()) {
            conferenceDay.setFreeSeats(conferenceDay.getFreeSeats() - 1);
            reservations.addAll(createReservations(conferenceDay, clients));
        }
        return reservations;
    }

    private static List<Reservation> createReservations(ConferenceDay conferenceDay, List<Client> clients) {
        List<Reservation> reservations = new ArrayList<>();

        int freeSeats = conferenceDay.getFreeSeats();
        int countOfReservations = gen.nextInt(freeSeats / 2) + freeSeats / 2;

        for (int i = 0; i < countOfReservations && conferenceDay.getFreeSeats() > 10; i++) {
            createSingleReservation(reservations, conferenceDay, clients);
        }
        return reservations;
    }

    private static void createSingleReservation(List<Reservation> reservations, ConferenceDay conferenceDay, List<Client> clients) {
        int freeSeats = conferenceDay.getFreeSeats();

        Client randomClient = clients.get(gen.nextInt(clients.size()));

        int countOfPeople = Math.min((randomClient.isCompany() ? gen.nextInt(6) + 1 : 1), freeSeats);

//        last arg is not important
        Reservation reservation =
                new Reservation(conferenceDay.getId(), randomClient.getClientID(), 3, countOfPeople, LocalDate.now());

        conferenceDay.setFreeSeats(conferenceDay.getFreeSeats() - countOfPeople);

        createWorkshopReservations(conferenceDay, reservation);

        reservations.add(reservation);
    }

    private static void createWorkshopReservations(ConferenceDay conferenceDay, Reservation reservation) {
        List<Workshop> workshops = conferenceDay.getWorkshops();

        for (Workshop workshop : workshops) {
            if (gen.nextBoolean()) { //sign client to half of workshops
                createSingleWorkshopReservation(workshop, reservation);
            }
        }
    }

    private static void createSingleWorkshopReservation(Workshop workshop, Reservation reservation) {
        //do not allow to create reservation when there are no free seats
        if (workshop.getFreeSeats() <= 0) {
            return;
        }

        int freeSeats = workshop.getFreeSeats();
        int randomCountOfPeople = Math.min((gen.nextInt(10) + 1), freeSeats);
        workshop.setFreeSeats(workshop.getFreeSeats() - randomCountOfPeople);

        WorkshopReservation workshopReservation =
                new WorkshopReservation(workshop.getId(), reservation.getId(), randomCountOfPeople);

        reservation.getPayment().setAmountToPay(reservation.getPayment().getAmountToPay() +
                workshop.getPricePerPerson() * randomCountOfPeople);

        reservation.addWorkshopReservation(workshopReservation);
    }
}
