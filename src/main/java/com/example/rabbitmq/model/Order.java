package com.example.rabbitmq.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class Order {
    
    @JsonProperty("orderId")
    private String orderId;
    
    @JsonProperty("customerId")
    private String customerId;
    
    @JsonProperty("customerEmail")
    private String customerEmail;
    
    @JsonProperty("items")
    private List<OrderItem> items;
    
    @JsonProperty("totalAmount")
    private BigDecimal totalAmount;
    
    @JsonProperty("orderDate")
    private LocalDateTime orderDate;
    
    @JsonProperty("status")
    private OrderStatus status;

    public Order() {}

    public Order(String orderId, String customerId, String customerEmail, 
                List<OrderItem> items, BigDecimal totalAmount) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.customerEmail = customerEmail;
        this.items = items;
        this.totalAmount = totalAmount;
        this.orderDate = LocalDateTime.now();
        this.status = OrderStatus.PENDING;
    }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    @Override
    public String toString() {
        return "Order{" +
                "orderId='" + orderId + '\'' +
                ", customerId='" + customerId + '\'' +
                ", totalAmount=" + totalAmount +
                ", status=" + status +
                '}';
    }

    public static class OrderItem {
        @JsonProperty("productId")
        private String productId;
        
        @JsonProperty("productName")
        private String productName;
        
        @JsonProperty("quantity")
        private int quantity;
        
        @JsonProperty("price")
        private BigDecimal price;

        public OrderItem() {}

        public OrderItem(String productId, String productName, int quantity, BigDecimal price) {
            this.productId = productId;
            this.productName = productName;
            this.quantity = quantity;
            this.price = price;
        }

        public String getProductId() { return productId; }
        public void setProductId(String productId) { this.productId = productId; }

        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }

        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }

        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }
    }

    public enum OrderStatus {
        PENDING, PROCESSING, PAID, SHIPPED, DELIVERED, CANCELLED
    }
}