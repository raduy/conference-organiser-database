package pl.dbgen.partialgenerators;

import pl.dbgen.entities.Reservation;
import pl.dbgen.helpers.ClientIncome;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lukasz Raduj
 */
public class IncomeGenerator {
    private final static PrimitiveDataGenerator gen = PrimitiveDataGenerator.getInstance();


    public static List<ClientIncome> generateClientsIncomes(List<Reservation> reservations) {
        List<ClientIncome> clientIncomes = new ArrayList<>();

        for (Reservation reservation : reservations) {
            int income = reservation.getPayment().getAmountToPay();

            //randomization
            //one for four client make bad payment
            if(gen.nextBoolean() && gen.nextBoolean()) {
                income = gen.nextInt(2 * income + 1);
            }
            ClientIncome clientIncome = new ClientIncome(reservation.getId(), Double.valueOf(1.5 * income).intValue());
            clientIncomes.add(clientIncome);
        }

        return clientIncomes;
    }
}
