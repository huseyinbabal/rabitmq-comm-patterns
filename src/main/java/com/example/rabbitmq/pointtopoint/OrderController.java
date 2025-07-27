package com.example.rabbitmq.pointtopoint;

import com.example.rabbitmq.model.Order;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Point-to-Point Pattern", description = "Order processing using direct queue communication")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @Operation(summary = "Create a new order", 
               description = "Creates an order and sends it to the payment processing queue")
    public ResponseEntity<Order> createOrder(@RequestBody CreateOrderRequest request) {
        List<Order.OrderItem> items = request.getItems().stream()
                .map(item -> new Order.OrderItem(
                        item.getProductId(),
                        item.getProductName(), 
                        item.getQuantity(),
                        item.getPrice()))
                .toList();

        Order order = orderService.createOrder(
                request.getCustomerId(),
                request.getCustomerEmail(), 
                items);

        return ResponseEntity.ok(order);
    }

    @PostMapping("/sample")
    @Operation(summary = "Create a sample order", 
               description = "Creates a predefined sample order for demonstration")
    public ResponseEntity<Order> createSampleOrder() {
        Order order = orderService.createSampleOrder();
        return ResponseEntity.ok(order);
    }

    public static class CreateOrderRequest {
        private String customerId;
        private String customerEmail;
        private List<OrderItemRequest> items;

        public String getCustomerId() { return customerId; }
        public void setCustomerId(String customerId) { this.customerId = customerId; }

        public String getCustomerEmail() { return customerEmail; }
        public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

        public List<OrderItemRequest> getItems() { return items; }
        public void setItems(List<OrderItemRequest> items) { this.items = items; }
    }

    public static class OrderItemRequest {
        private String productId;
        private String productName;
        private int quantity;
        private BigDecimal price;

        public String getProductId() { return productId; }
        public void setProductId(String productId) { this.productId = productId; }

        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }

        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }

        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }
    }
}