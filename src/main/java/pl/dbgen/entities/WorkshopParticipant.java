package pl.dbgen.entities;

import pl.dbgen.namesandpathes.SQLProcedureName;

/**
 * @author Lukasz Raduj
 */
public class WorkshopParticipant {
    private final int workshopReservationId;
    private final int participantId;

    public WorkshopParticipant(int workshopReservationId, int participantId) {
        this.workshopReservationId = workshopReservationId;
        this.participantId = participantId;
    }

    public String buildNewWorkshopParticipantExecString() {
        StringBuilder builder = new StringBuilder();

        builder.append(SQLProcedureName.ADD_WORKSHOP_PARTICIPANT);

        builder.append(workshopReservationId).append(", ")
                .append(participantId).append(";\n");

        return builder.toString();
    }

    public int getWorkshopReservationId() {
        return workshopReservationId;
    }

    public int getParticipantId() {
        return participantId;
    }
}
