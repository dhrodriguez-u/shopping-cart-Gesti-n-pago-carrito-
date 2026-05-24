package isi.shoppingCart.usecases.ports;

import isi.shoppingCart.usecases.dto.PaymentRequest;
import isi.shoppingCart.usecases.dto.PaymentResult;

public interface PaymentGateway {
    PaymentResult pay(PaymentRequest request);
}