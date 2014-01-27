package pl.dbgen.namesandpathes;

/**
 * @author Lukasz Raduj
 */
public enum SQLProcedureName {
    ADD_CLIENT("exec add_client "),
    ADD_WORKSHOP("exec add_workshop "),
    ADD_CONFERENCE("exec add_conference "),
    ADD_CONFERENCE_DAY("exec add_conference_day "),
    SET_FALSE_SYSTEM_DATE("exec set_false_system_date "),
    ADD_PRICE("exec add_price "),
    ADD_RESERVATION("exec add_reservation "),
    ADD_WORKSHOP_RESERVATION("exec add_workshop_reservation "),
    ADD_WORKSHOP_PARTICIPANT("exec add_workshop_participant "),
    FILL_RESERVATION_WITH_PERSONALITIES("exec fill_reservation_with_personalities "),
    LOG_INCOME_FROM_CLIENT("exec log_income_from_client "),
    TURN_OFF_FALSE_DB_SYSTEM_TIME("exec turn_off_false_system_time ");


    private String procedureCall;
    private SQLProcedureName(String procedureCall) {
           this.procedureCall = procedureCall;
    }

    @Override
    public String toString() {
        return procedureCall;
    }
}
