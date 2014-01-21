package pl.dbgen.entities;

import pl.dbgen.namesandpathes.SQLProcedureName;

/**
 * @author Lukasz Raduj
 */
public class Participant {
    private static int nextId = 1;
    private final int id = nextId++;
    private final int reservationId;
    private final Integer studentIdentyficator ;
    private final String firstName;
    private final String lastName;

    public Participant(int reservationId, Integer studentIdentyficator, String firstName, String lastName) {
        this.reservationId = reservationId;
        this.studentIdentyficator = studentIdentyficator;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String buildNewParticipantExecString() {
        StringBuilder builder = new StringBuilder();

        builder.append(SQLProcedureName.FILL_RESERVATION_WITH_PERSONALITIES);
        builder.append(reservationId).append(", '")
                .append(firstName).append("', '")
                .append(lastName).append("', ")
                .append(studentIdentyficator).append(";\n");

        return builder.toString();
    }

    public int getId() {
        return id;
    }

    public int getReservationId() {
        return reservationId;
    }

    public Integer getStudentIdentyficator() {
        return studentIdentyficator;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
