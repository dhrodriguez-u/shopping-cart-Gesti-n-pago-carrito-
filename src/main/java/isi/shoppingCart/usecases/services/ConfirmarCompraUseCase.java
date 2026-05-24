package isi.shoppingCart.usecases.services;

import isi.shoppingCart.entities.Cart;
import isi.shoppingCart.entities.CartItem;
import isi.shoppingCart.entities.Customer;
import isi.shoppingCart.entities.Product;
import isi.shoppingCart.entities.Purchase;
import isi.shoppingCart.entities.PurchaseItem;
import isi.shoppingCart.usecases.dto.OperationResult;
import isi.shoppingCart.usecases.ports.CartRepository;
import isi.shoppingCart.usecases.ports.CustomerRepository;
import isi.shoppingCart.usecases.ports.ProductRepository;
import isi.shoppingCart.usecases.ports.PurchaseRepository;
import isi.shoppingCart.usecases.dto.PaymentResult;

import java.util.List;

public class ConfirmarCompraUseCase {
    private CartRepository cartRepository;
    private CustomerRepository customerRepository;
    private PurchaseRepository purchaseRepository;
    private ProductRepository productRepository;

    public ConfirmarCompraUseCase(CartRepository cartRepository,
                                  CustomerRepository customerRepository,
                                  PurchaseRepository purchaseRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.customerRepository = customerRepository;
        this.purchaseRepository = purchaseRepository;
        this.productRepository = productRepository;
    }
//Integrante 3 - Integrante 2
private PaymentResult processPayment(double total) {

    if (total <= 3000) {

        return new PaymentResult(
                true,
                "Transacción aprobada",
                total,
                "TXN-001"
        );
    }

    return new PaymentResult(
            false,
            "Fondos insuficientes",
            total,
            "TXN-002"
    );
}



    public OperationResult execute() {
        Cart cart = cartRepository.getCart();

        //Integrante 1
        if (cart == null || !cart.isReadyForPayment()) {
            return OperationResult.fail("El carrito no está listo para iniciar pago.");
        }

        Customer customer = customerRepository.getCustomer();

        if (customer == null) {
            return OperationResult.fail("No hay cliente registrado.");
        }
//Intrante 3
        List<CartItem> items = cart.getItems();
        int i;

        for (i = 0; i < items.size(); i++) {
            CartItem item = items.get(i);
            Product product = item.getProduct();

            if (item.getQuantity() > product.getAvailableQuantity()) {
                return OperationResult.fail(
                        "No hay disponibilidad suficiente para: "
                                + product.getName() + "."
                );
            }
        }

        double total = cart.getTotal();

        PaymentResult paymentResult = processPayment(total);

        if (!paymentResult.isApproved()) {

            return OperationResult.fail(
                    "Pago rechazado: "
                            + paymentResult.getMessage()
            );
        }

        Purchase purchase = new Purchase(purchaseRepository.getNextId(), customer);

        for (i = 0; i < items.size(); i++) {
            CartItem item = items.get(i);
            Product product = item.getProduct();

            product.decreaseAvailableQuantity(item.getQuantity());
            purchase.addItem(new PurchaseItem(product, item.getQuantity(), product.getPrice()));
            productRepository.save(product);

        }

        purchaseRepository.save(purchase);
        cartRepository.save(new Cart());

        return OperationResult.ok(
                "Pago aprobado. Compra "
                        + purchase.getId()
                        + " confirmada para "
                        + customer.getName()
                        + ". Total: $ "
                        + purchase.getTotal()
                        + ". Transacción: "
                        + paymentResult.getTransactionId()
        );
    }
}
