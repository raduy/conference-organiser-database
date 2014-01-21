package pl.dbgen.entities;

/**
 * @author Lukasz Raduj
 */
public class Payment {
    private final int reservationId;
    private int amountToPay;
    private int amountPaid;

    public Payment(int reservationId, int amountToPay, int amountPaid) {
        this.reservationId = reservationId;
        this.amountToPay = amountToPay;
        this.amountPaid = amountPaid;
    }

    public int getReservationId() {
        return reservationId;
    }

    public int getAmountToPay() {
        return amountToPay;
    }

    public void setAmountToPay(int amountToPay) {
        this.amountToPay = amountToPay;
    }

    public int getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(int amountPaid) {
        this.amountPaid = amountPaid;
    }
}
