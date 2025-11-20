package com.recnaile.accountService.service;

import com.recnaile.accountService.exception.ResourceNotFoundException;
import com.recnaile.accountService.model.*;
import com.recnaile.accountService.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final RestTemplate restTemplate;
    private final AddressService addressService;

    

    private static final String PRODUCT_SERVICE_URL = "https://api.recnaile.com/api/products/unique/";


    public Order placeOrder(String userId, List<OrderRequest.Item> items,
                            String shippingAddressId, String billingAddressId,
                            String paymentMethod) {


        // Create order items with product details
        List<Order.OrderItem> orderItems = items.stream()
                .map(this::createOrderItem)
                .collect(Collectors.toList());

        // Calculate totals
        OrderTotals totals = calculateTotals(orderItems);

        // Create and save order
        Order order = new Order();
        order.setUserId(userId);
        order.setOrderNumber(generateOrderNumber());
        order.setOrderDate(LocalDateTime.now());
        order.setDeliveryDate(LocalDateTime.now().plusDays(3));
        order.setItems(orderItems);
        order.setShippingAddressId(shippingAddressId);
        order.setBillingAddressId(billingAddressId);
        order.setSubtotal(totals.subtotal);
        order.setTax(totals.tax);
        order.setShippingCharge(totals.shippingCharge);
        order.setTotal(totals.total);
        order.setPaymentMethod(paymentMethod);
        order.setPaymentStatus("PENDING");
        order.setOrderStatus("PROCESSING");

        return orderRepository.save(order);
    }

    public List<Order> getUserOrders(String userId) {
        return orderRepository.findByUserId(userId);
    }

    private Order.OrderItem createOrderItem(OrderRequest.Item item) {
        ProductDTO product = fetchProductDetails(item.getUniqueProductName());

        Order.OrderItem orderItem = new Order.OrderItem();

        orderItem.setUniqueProductName(product.getUniqueProductName());
        orderItem.setProductName(product.getProductName());

        // Set thumbnail to first image URL if available, otherwise use productThumbnail
        if (product.getImageUrls() != null && !product.getImageUrls().isEmpty()) {
            orderItem.setProductThumbnail(product.getImageUrls().get(0));
        } else {
            orderItem.setProductThumbnail(product.getProductThumbnail());
        }

        orderItem.setQuantity(item.getQuantity());
        orderItem.setMrpRate(product.getMrpRate());
        orderItem.setDiscountAmount(product.getDiscountAmount());
        orderItem.setFinalPrice(product.getMrpRate() - product.getDiscountAmount());

        return orderItem;
    }
    private OrderTotals calculateTotals(List<Order.OrderItem> items) {
        double subtotal = items.stream()
                .mapToDouble(item -> item.getFinalPrice() * item.getQuantity())
                .sum();

        double tax = subtotal * 0.1; // 10% tax
        double shippingCharge = subtotal > 5000 ? 0 : 100; // Free shipping over 5000
        double total = subtotal + tax + shippingCharge;

        return new OrderTotals(subtotal, tax, shippingCharge, total);
    }

    private String generateOrderNumber() {
        return "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private ProductDTO fetchProductDetails(String uniqueProductName) {
        try {
            return restTemplate.getForObject(
                    PRODUCT_SERVICE_URL + uniqueProductName,
                    ProductDTO.class
            );
        } catch (Exception e) {
            throw new ResourceNotFoundException("Failed to fetch product details: " + e.getMessage());
        }
    }
     public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }


    public Order getOrderByNumber(String orderNumber, String userId) {
        return orderRepository.findByOrderNumber(orderNumber)
                .filter(order -> order.getUserId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    }

    public Order updateOrderStatus(String orderNumber, String status) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        order.setOrderStatus(status);

        return orderRepository.save(order);
    }
    private String generateTrackingNumber() {
        return "TRK-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();
    }

    public Order updatePaymentStatus(String orderNumber, String status) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        order.setPaymentStatus(status);
        return orderRepository.save(order);
    }

    private record OrderTotals(double subtotal, double tax, double shippingCharge, double total) {}




}



