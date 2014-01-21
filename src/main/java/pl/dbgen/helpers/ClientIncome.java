package pl.dbgen.helpers;

import pl.dbgen.namesandpathes.SQLProcedureName;

/**
 * @author Lukasz Raduj
 */
public class ClientIncome {
    private final int reservationId;
    private int income;

    public ClientIncome(int reservationId, int income) {
        this.reservationId = reservationId;
        this.income = income;
    }

    public String buildLogPaymentFromClientExecString() {
        StringBuilder builder = new StringBuilder();

        builder.append(SQLProcedureName.LOG_INCOME_FROM_CLIENT);

        builder.append(reservationId).append(", ")
                .append(income).append(";\n");

        return builder.toString();
    }
}
