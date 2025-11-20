package com.recnaile.accountService.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recnaile.accountService.exception.ResourceNotFoundException;
import com.recnaile.accountService.model.*;
import com.recnaile.accountService.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final RestTemplate restTemplate;
    private final AddressService addressService;
    private final ObjectMapper objectMapper;

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
        ProductDTO product = fetchProductDetailsSafe(item.getUniqueProductName());

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

    /**
     * Safe method that manually handles the response to avoid HttpMessageConverter issues
     */
    private ProductDTO fetchProductDetailsSafe(String uniqueProductName) {
        String url = null;
        try {
            // Validate and encode the product name
            if (uniqueProductName == null || uniqueProductName.trim().isEmpty()) {
                throw new IllegalArgumentException("Product name cannot be null or empty");
            }
            
            String encodedProductName = URLEncoder.encode(uniqueProductName.trim(), StandardCharsets.UTF_8);
            url = PRODUCT_SERVICE_URL + encodedProductName;
            
            log.info("🔍 Fetching product from: {}", url);
            
            // Create request with explicit JSON acceptance
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            headers.set("User-Agent", "OrderService/1.0");
            
            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
            
            // MANUAL RESPONSE HANDLING - get raw response as String
            ResponseEntity<String> response = restTemplate.exchange(
                URI.create(url),
                HttpMethod.GET,
                requestEntity,
                String.class
            );
            
            log.info("📥 Response Status: {}", response.getStatusCode());
            log.info("📥 Content Type: {}", response.getHeaders().getContentType());
            
            // Check if we got HTML (error page)
            if (response.getHeaders().getContentType() != null && 
                response.getHeaders().getContentType().includes(MediaType.TEXT_HTML)) {
                
                log.error("❌ Received HTML error page instead of JSON");
                log.error("📄 HTML Preview: {}", 
                    response.getBody() != null ? 
                    response.getBody().substring(0, Math.min(300, response.getBody().length())) : "Empty body");
                
                throw new ResourceNotFoundException("Product service returned HTML error page. Product '" + uniqueProductName + "' may not exist.");
            }
            
            // Check if response is successful and has body
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new ResourceNotFoundException("Product service returned error: " + response.getStatusCode());
            }
            
            if (response.getBody() == null || response.getBody().trim().isEmpty()) {
                throw new ResourceNotFoundException("Empty response from product service");
            }
            
            // Log the raw response for debugging
            log.debug("📄 Raw response body: {}", response.getBody());
            
            // Manually parse JSON to avoid HttpMessageConverter issues
            log.info("🔄 Parsing JSON response...");
            ProductDTO product = objectMapper.readValue(response.getBody(), ProductDTO.class);
            
            if (product == null) {
                throw new ResourceNotFoundException("Failed to parse product data");
            }
            
            log.info("✅ Successfully fetched product: {} (ID: {})", 
                product.getProductName(), product.get_id());
            
            return product;
            
        } catch (HttpClientErrorException.NotFound e) {
            log.error("❌ Product not found (404): {}", uniqueProductName);
            throw new ResourceNotFoundException("Product not found: " + uniqueProductName);
        } catch (HttpClientErrorException e) {
            log.error("❌ HTTP Client Error {} for product '{}': {}", 
                e.getStatusCode(), uniqueProductName, e.getMessage());
            throw new ResourceNotFoundException("Product service error: " + e.getStatusCode());
        } catch (Exception e) {
            log.error("❌ Unexpected error fetching product '{}' from URL {}: {}", 
                uniqueProductName, url, e.getMessage(), e);
            throw new ResourceNotFoundException("Error fetching product '" + uniqueProductName + "': " + e.getMessage());
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

    /**
     * DELETE THIS OLD METHOD IF IT EXISTS IN YOUR CODE
     * This is the method causing the HttpMessageConverter error
     */
    /*
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
    */
}
