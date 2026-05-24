package isi.shoppingCart.usecases.dto;

public class PaymentRequest {
    private double amount;
    private int customerId;

    public PaymentRequest(double amount, int customerId) {
        this.amount = amount;
        this.customerId = customerId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }
}

