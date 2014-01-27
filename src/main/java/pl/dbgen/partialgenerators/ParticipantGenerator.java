package pl.dbgen.partialgenerators;

import pl.dbgen.entities.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lukasz Raduj
 */
public class ParticipantGenerator {
    private static final PrimitiveDataGenerator gen = PrimitiveDataGenerator.getInstance();

    public static List<Participant> generateParticipantsForReservations(List<Reservation> reservations,
                                                                        List<WorkshopParticipant> workshopParticipants){
        List<Participant> participants = new ArrayList<>();

        for (Reservation reservation : reservations) {
            participants.addAll(fillReservationWithPersonalData(reservation, workshopParticipants));
        }
        return participants;
    }

    private static List<Participant> fillReservationWithPersonalData(Reservation reservation, List<WorkshopParticipant> workshopParticipants) {
        List<Participant> participants = new ArrayList<>();

        int countOfParticipants = reservation.getCountOfPeople();

//        randomization
        if (gen.nextBoolean() && gen.nextBoolean()) {
            countOfParticipants += gen.nextInt(3) - 3;
        }

        for (int i = 0; i < countOfParticipants; i++) {
            Participant participant =
                    new Participant(reservation.getId(), gen.nextStudentIdentyficatorOrNull(), gen.nextFirstName(), gen.nextLastName());

            participants.add(participant);
        }

        List<WorkshopReservation> workshopReservations = reservation.getWorkshopReservations();

        if (participants.size() > 0) {
            signParticipantsToWorkshops(participants, workshopReservations, workshopParticipants);
        }

        return participants;
    }

    private static void signParticipantsToWorkshops(List<Participant> participants, List<WorkshopReservation> workshopReservations,
                                                    List<WorkshopParticipant> workshopParticipants) {
        for (WorkshopReservation workshopReservation : workshopReservations) {
            int countOfPeople = workshopReservation.getCountOfPeople();

            for (int i = 0; i < countOfPeople; i++) {
                Participant participant = participants.get(gen.nextInt(participants.size()));
                WorkshopParticipant workshopParticipant = new WorkshopParticipant(workshopReservation.getId(), participant.getId());
                workshopParticipants.add(workshopParticipant);
            }
        }
    }
}
