package isi.shoppingCart.usecases.dto;

public class PaymentResult {

    private boolean approved;
    private String message;
    private double amount;
    private String transactionId;

    public PaymentResult(
            boolean approved,
            String message,
            double amount,
            String transactionId
    ) {
        this.approved = approved;
        this.message = message;
        this.amount = amount;
        this.transactionId = transactionId;
    }

    public boolean isApproved() {
        return approved;
    }

    public String getMessage() {
        return message;
    }

    public double getAmount() {
        return amount;
    }

    public String getTransactionId() {
        return transactionId;
    }
}
