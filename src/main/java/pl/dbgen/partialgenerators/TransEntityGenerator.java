package pl.dbgen.partialgenerators;

import pl.dbgen.entities.Conference;
import pl.dbgen.entities.ConferenceDay;
import pl.dbgen.entities.Workshop;
import pl.dbgen.partialgenerators.PrimitiveDataGenerator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Lukasz Raduj
 */
public class TransEntityGenerator {
    private final static PrimitiveDataGenerator gen = PrimitiveDataGenerator.getInstance();

    public static Conference generateCompleteConference(LocalDate falseSystemDate) {
        Conference conference = generateConference(falseSystemDate);

        List<ConferenceDay> conferenceDays = generateConferenceDaysForConference(conference);
        for (ConferenceDay conferenceDay : conferenceDays) {
            conference.addConferenceDay(conferenceDay);
            List<Workshop> workshops = generateWorkshopsForConferenceDay(conference, conferenceDay);
            for (Workshop workshop : workshops) {
                conferenceDay.addWorkshop(workshop);
            }
        }

        return conference;
    }

    private static List<Workshop> generateWorkshopsForConferenceDay(Conference conference, ConferenceDay conferenceDay) {
        List<Workshop> workshops = new ArrayList<>();

        int countOfWorkshops = gen.nextInt(6) + 2;

        for (int i = 0; i < countOfWorkshops; i++) {
            LocalDateTime startDateTime = LocalDateTime.of(conference.getStartingDate().plusDays(conferenceDay.getWhichDay() - 1),
                    LocalTime.of(gen.nextInt(9) + 9, 0));

            LocalDateTime endDateTime = startDateTime.plusHours(gen.nextInt(4) + 1);

            Workshop workshop = new Workshop(conferenceDay.getId(), gen.nextInt(100) + 1,
                    startDateTime, endDateTime, gen.nextInt(300), gen.nextDescription());
            workshops.add(workshop);
        }

        return workshops;
    }

    private static Conference generateConference(LocalDate falseSystemDate) {
        int countOfDays = gen.nextInt(5) + 1;

        return new Conference(falseSystemDate.plusWeeks(3), countOfDays, gen.nextDescription());
    }

    private static List<ConferenceDay> generateConferenceDaysForConference(Conference conference) {
        List<ConferenceDay> conferenceDays = new ArrayList<>();

        int countOfDays = conference.getCountOfDays();
        for (int i = 0; i < countOfDays; i++) {
            ConferenceDay conferenceDay = new ConferenceDay(conference.getId(), gen.nextInt(40) + 40, i + 1, gen.nextDescription());
            conferenceDays.add(conferenceDay);
        }

        return conferenceDays;
    }
}
