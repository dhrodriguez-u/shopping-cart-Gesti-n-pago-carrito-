package isi.shoppingCart.usecases.services;

import isi.shoppingCart.entities.Cart;
import isi.shoppingCart.entities.CartItem;
import isi.shoppingCart.entities.Customer;
import isi.shoppingCart.entities.Product;
import isi.shoppingCart.entities.Purchase;
import isi.shoppingCart.entities.PurchaseItem;
import isi.shoppingCart.usecases.dto.OperationResult;
import isi.shoppingCart.usecases.dto.PaymentRequest;
import isi.shoppingCart.usecases.dto.PaymentResult;
import isi.shoppingCart.usecases.ports.CartRepository;
import isi.shoppingCart.usecases.ports.CustomerRepository;
import isi.shoppingCart.usecases.ports.PaymentGateway;
import isi.shoppingCart.usecases.ports.ProductRepository;
import isi.shoppingCart.usecases.ports.PurchaseRepository;

import java.util.List;

public class ConfirmarCompraUseCase {
    private CartRepository cartRepository;
    private CustomerRepository customerRepository;
    private PurchaseRepository purchaseRepository;
    private ProductRepository productRepository;
    private PaymentGateway paymentGateway;

    public ConfirmarCompraUseCase(CartRepository cartRepository,
                                  CustomerRepository customerRepository,
                                  PurchaseRepository purchaseRepository,
                                  ProductRepository productRepository,
                                  PaymentGateway paymentGateway) {
        this.cartRepository = cartRepository;
        this.customerRepository = customerRepository;
        this.purchaseRepository = purchaseRepository;
        this.productRepository = productRepository;
        this.paymentGateway = paymentGateway;
    }

    public OperationResult execute() {
        Cart cart = cartRepository.getCart();

        if (cart == null || !cart.isReadyForPayment()) {
            return OperationResult.fail("El carrito no está listo para iniciar pago.");
        }

        Customer customer = customerRepository.getCustomer();

        if (customer == null) {
            return OperationResult.fail("No hay cliente registrado.");
        }

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

        PaymentRequest paymentRequest = new PaymentRequest(total, customer.getId());
        PaymentResult paymentResult = paymentGateway.pay(paymentRequest);

        if (!paymentResult.isApproved()) {
            return OperationResult.fail("Pago rechazado: " + paymentResult.getMessage());
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
