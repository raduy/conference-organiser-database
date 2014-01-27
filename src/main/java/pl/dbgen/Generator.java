package pl.dbgen;

import pl.dbgen.entities.*;
import pl.dbgen.helpers.ClientIncome;
import pl.dbgen.namesandpathes.SQLProcedureName;
import pl.dbgen.partialgenerators.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Lukasz Raduj
 */
public class Generator {
    private final static PrimitiveDataGenerator gen = PrimitiveDataGenerator.getInstance();

    public static void main(String[] args) {
        StringBuilder sqlQuery = new StringBuilder();

        //create clients
        List<Client> clients = generateRandomClients(500, 1000);
        for (Client client : clients) {
            sqlQuery.append(client.buildClientExecString());
        }

        //start creating conferences between this dates
        LocalDate from = LocalDate.of(2011, 6, 19);
        LocalDate to = LocalDate.of(2014, 6, 19);

        LocalDate falseSystemDate = from;

        //change time in DB
        sqlQuery.append(TimeSpace.changeDateInDBSystem(from)).append('\n');
        int countOfConferences = (to.getMonthValue() + (to.getYear() - from.getYear()) * 12 + (12 - from.getDayOfMonth())) * 2;

        for(int i = 0; i < countOfConferences; i++) {
            sqlQuery.append('\n');
            Conference conference = insertConference(sqlQuery, falseSystemDate);
            insertPrices(sqlQuery, conference);
            List<Reservation> reservations = insertReservations(sqlQuery, clients, conference);
            insertParticipantsPersonalities(sqlQuery, reservations);
            insertIncomesFromClients(sqlQuery, reservations);

            falseSystemDate = falseSystemDate.plusWeeks(2);
            sqlQuery.append(TimeSpace.changeDateInDBSystem(falseSystemDate)).append('\n');
        }

        sqlQuery.append(SQLProcedureName.TURN_OFF_FALSE_DB_SYSTEM_TIME).append('\n');
        File output = new File("C:\\Users\\raduy\\IdeaProjects\\db-generator\\src\\main\\resources\\output");
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(output))){
            writer.write(sqlQuery.toString());

        } catch (IOException e) {
            System.out.println("Oops! Exception occurred !");
        }
    }

    private static void insertIncomesFromClients(StringBuilder sqlQuery, List<Reservation> reservations) {
        //generate incomes from clients
        List<ClientIncome> clientIncomes = IncomeGenerator.generateClientsIncomes(reservations);
        //and build EXEC string
        for (ClientIncome clientIncome : clientIncomes) {
            sqlQuery.append(clientIncome.buildLogPaymentFromClientExecString()).append('\n');
        }
    }

    private static void insertParticipantsPersonalities(StringBuilder sqlQuery, List<Reservation> reservations) {
        //fill reservations with personalities - create participants
        List<WorkshopParticipant> workshopParticipants = new ArrayList<>();
        List<Participant> participants = ParticipantGenerator.generateParticipantsForReservations(reservations, workshopParticipants);
        //build EXEC string to create personalities
        for (Participant participant : participants) {
            sqlQuery.append(participant.buildNewParticipantExecString());
        }
        //and sign people to concrete workshops
        for (WorkshopParticipant workshopParticipant : workshopParticipants) {
            sqlQuery.append(workshopParticipant.buildNewWorkshopParticipantExecString());
        }
    }

    private static List<Reservation> insertReservations(StringBuilder sqlQuery, List<Client> clients, Conference conference) {
        //create reservations for conference
        List<Reservation> reservations = ReservationGenerator.generateCompleteReservationsForConference(conference, clients);
        //build EXEC string to create reservations
        final int daysBeforeConference = 40;

        sqlQuery.append(TimeSpace.changeDateInDBSystem(conference.getStartingDate().minusDays(daysBeforeConference)))
                .append('\n');
        int skippedDays = daysBeforeConference;

        for (Reservation reservation : reservations) {
            if (skippedDays == 7) {
                sqlQuery.append(TimeSpace.changeDateInDBSystem(conference.getStartingDate().minusDays(daysBeforeConference)))
                        .append('\n');
                skippedDays = daysBeforeConference;
            }

            sqlQuery.append(reservation.buildNewReservationExecString());

            int daysToSkip = gen.nextInt(3);
            skippedDays -= daysToSkip;
            sqlQuery.append(TimeSpace.changeDateInDBSystem(conference.getStartingDate().minusDays(skippedDays))).append('\n');
        }
        return reservations;
    }

    private static void insertPrices(StringBuilder sqlQuery, Conference conference) {
        //create prices
        List<Price> prices = PriceGenerator.generateCompletePricesForConference(conference);

        //build EXEC string to create prices
        for (Price price : prices) {
            sqlQuery.append(price.buildNewPriceExecString());
        }
    }

    private static Conference insertConference(StringBuilder sqlQuery, LocalDate falseSystemDate) {
        //create conference
        Conference conference = ConferenceGenerator.generateCompleteConference(falseSystemDate);

        //build EXEC string to create conference
        sqlQuery.append(conference.buildConferenceExecString());
        for (ConferenceDay conferenceDay : conference.getConferenceDays()) {
            sqlQuery.append(conferenceDay.buildNewConferenceDayExecString());
            for (Workshop workshop : conferenceDay.getWorkshops()) {
                sqlQuery.append(workshop.buildWorkshopExecString());
            }
        }
        return conference;
    }

    private static List<Client> generateRandomClients(int min, int max) {
        List<Client> clients = new ArrayList<>();

        int count = gen.nextInt(max - min) + min;
        for (int i = 0; i < count; i++) {

            boolean isCompany = gen.nextBoolean() || gen.nextBoolean(); //75% probability that is company

            Client client;

            if(isCompany) {
                client = new Client(gen.nextCompanyName(), true, gen.nextPhone(), gen.nextCountry(), gen.nextStreet(), gen.nextCity());
            }  else {
                client = new Client(gen.nextSinglePersonName(), false, gen.nextPhone(), gen.nextCountry(), gen.nextStreet(), gen.nextCity());
            }

            clients.add(client);
        }

        return clients;
    }
}
