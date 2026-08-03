package models;

public class Split {

    private final User participant;
    private final double amount;

    public Split(User participant, double amount) {
        this.participant = participant;
        this.amount = amount;
    }

    public User getParticipant() {
        return participant;
    }

    public double getAmount() {
        return amount;
    }
}