package isi.shoppingCart.infrastructure.services;

import isi.shoppingCart.usecases.dto.PaymentRequest;
import isi.shoppingCart.usecases.dto.PaymentResult;
import isi.shoppingCart.usecases.ports.PaymentGateway;

public class SimulatedPaymentService implements PaymentGateway {
    @Override
    public PaymentResult pay(PaymentRequest request) {
        if (request.getAmount() <= 3000) {
            return new PaymentResult(true, "Transacción aprobada", request.getAmount(), "TXN-001");
        }
        return new PaymentResult(false, "Fondos insuficientes", request.getAmount(), "TXN-002");
    }
}

