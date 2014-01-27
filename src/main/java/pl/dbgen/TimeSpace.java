package pl.dbgen;

import pl.dbgen.namesandpathes.SQLProcedureName;

import java.time.LocalDate;

/**
 * @author Lukasz Raduj
 */
public class TimeSpace {

    public static String changeDateInDBSystem(LocalDate toDate) {
        StringBuilder builder = new StringBuilder();

        builder.append(SQLProcedureName.SET_FALSE_SYSTEM_DATE);

        builder.append(0).append(", '")
                .append(toDate).append("';");

        return builder.toString();
    }
}
