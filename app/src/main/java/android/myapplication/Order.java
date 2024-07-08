package android.myapplication;

import java.time.LocalDateTime;
import java.util.List;

public class Order {
    private String orderId;
    private List<CartProduct> products;
    private double totalAmount;
    private String deliveryOption;
    private LocalDateTime timestamp;

    public Order() {
        // Default constructor required for calls to DataSnapshot.getValue(Order.class)
    }

    public Order(String orderId, List<CartProduct> products, double totalAmount, String deliveryOption, LocalDateTime timestamp) {
        this.orderId = orderId;
        this.products = products;
        this.totalAmount = totalAmount;
        this.deliveryOption = deliveryOption;
        this.timestamp = timestamp;
    }

    // Getters and setters
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public List<CartProduct> getProducts() { return products; }
    public void setProducts(List<CartProduct> products) { this.products = products; }
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public String getDeliveryOption() { return deliveryOption; }
    public void setDeliveryOption(String deliveryOption) { this.deliveryOption = deliveryOption; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}