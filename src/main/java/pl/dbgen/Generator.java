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
        List<Client> clients = generateRandomClients(50, 100);
        for (Client client : clients) {
            sqlQuery.append(client.buildClientExecString()).append("\n");
        }

        //start creating conferences between this dates
        LocalDate from = LocalDate.of(2011, 06, 19);
        LocalDate to = LocalDate.of(2014, 06, 19);

        LocalDate falseSystemDate = from;

        //change time in DB
        sqlQuery.append(TimeSpace.changeDateInDBSystem(from)).append('\n');
        int countOfConferences = (to.getMonthValue() + (to.getYear() - from.getYear()) * 12 + (12 - from.getDayOfMonth())) * 2;

        for(int i = 0; i < countOfConferences; i++) {
            //create conference
            Conference conference = TransEntityGenerator.generateCompleteConference(falseSystemDate);

            //build EXEC string to create conference
            sqlQuery.append(conference.buildConferenceExecString()).append('\n');
            for (ConferenceDay conferenceDay : conference.getConferenceDays()) {
                sqlQuery.append(conferenceDay.buildNewConferenceDayExecString()).append('\n');
                for (Workshop workshop : conferenceDay.getWorkshops()) {
                    sqlQuery.append(workshop.buildWorkshopExecString()).append('\n');
                }
            }
            //create prices
            List<Price> prices = PriceGenerator.generateCompletePricesForConference(conference);

            //build EXEC string to create prices
            for (Price price : prices) {
                sqlQuery.append(SQLProcedureName.ADD_PRICE);

                sqlQuery.append(price.getConferenceDayId()).append(", ")
                        .append(price.getPrice()).append(", ")
                        .append(price.getDaysBeforeConferenceStart()).append(";\n");
            }

            //create reservations for conference
            List<Reservation> reservations = ReservationGenerator.generateCompleteReservationsForConference(conference, clients);
            //build EXEC string to create reservations
            sqlQuery.append(TimeSpace.changeDateInDBSystem(conference.getStartingDate().minusDays(35)));
            for (Reservation reservation : reservations) {
                sqlQuery.append(reservation.buildNewReservationExecString()).append('\n');

                int skipDays = gen.nextInt(30);
                sqlQuery.append(TimeSpace.changeDateInDBSystem(conference.getStartingDate().minusDays(skipDays))).append('\n');
            }

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

            //generate incomes from clients
            List<ClientIncome> clientIncomes = IncomeGenerator.generateClientsIncomes(reservations);
            //and build EXEC string
            for (ClientIncome clientIncome : clientIncomes) {
                sqlQuery.append(clientIncome.buildLogPaymentFromClientExecString());
            }

            falseSystemDate = falseSystemDate.plusWeeks(2);
        }

        File output = new File("C:\\Users\\raduy\\IdeaProjects\\db-generator\\src\\main\\resources\\output");
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(output))){
            writer.write(sqlQuery.toString());

        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
//        System.out.println(sqlQuery);
    }

    private static List<Client> generateRandomClients(int min, int max) {
        List<Client> clients = new ArrayList<>();

        int count = gen.nextInt(max - min) + min;
        for (int i = 0; i < count; i++) {

            boolean isCompany = gen.nextBoolean() || gen.nextBoolean(); //75% that is company

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
