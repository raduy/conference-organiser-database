package pl.dbgen.partialgenerators;

import pl.dbgen.entities.Conference;
import pl.dbgen.entities.ConferenceDay;
import pl.dbgen.entities.Price;
import pl.dbgen.namesandpathes.SQLProcedureName;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lukasz Raduj
 */
public class PriceGenerator {
    private final static PrimitiveDataGenerator gen = PrimitiveDataGenerator.getInstance();

    public static List<Price> generateCompletePricesForConference(Conference conference) {

        List<Price> prices = new ArrayList<>();

        for (ConferenceDay conferenceDay : conference.getConferenceDays()) {

            int countOfPrices = gen.nextInt(3) + 1;

            int firstPrice = gen.nextInt(100);
            int daysBeforeConferenceStart = gen.nextInt(30) + 21;
            for (int i = 0; i < countOfPrices; i++) {
                Price price = new Price(conferenceDay.getId(), firstPrice, daysBeforeConferenceStart);

                firstPrice += gen.nextInt(100);
                daysBeforeConferenceStart -= gen.nextInt(3) + 21 / countOfPrices;
                prices.add(price);
            }
        }

        return prices;
    }
}
