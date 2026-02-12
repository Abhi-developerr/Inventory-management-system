package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Order;
import com.example.demo.entity.OrderItem;
import com.example.demo.entity.Product;
import com.example.demo.enums.OrderStatus;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;

/**
 * Analytics Service
 * Provides business intelligence and reporting
 */
@Service
public class AnalyticsService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    /**
     * Get dashboard analytics
     */
    @Cacheable(value = "dashboardAnalytics", key = "#organizationId")
    public Map<String, Object> getDashboardAnalytics(Long organizationId) {
        Map<String, Object> analytics = new HashMap<>();

        // Total statistics
        analytics.put("totalProducts", productRepository.countByOrganizationId(organizationId));
        analytics.put("totalOrders", orderRepository.countByOrganizationId(organizationId));
        analytics.put("lowStockProducts", productRepository.countLowStockProducts(organizationId));

        // Revenue statistics
        List<Order> allOrders = orderRepository.findAllByOrganizationId(organizationId);
        Double totalRevenue = allOrders.stream()
                .filter(order -> order.getStatus() != OrderStatus.CANCELLED)
                .mapToDouble(order -> order.getTotalAmount().doubleValue())
                .sum();
        analytics.put("totalRevenue", totalRevenue);

        // Current month revenue
        LocalDateTime monthStart = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        Double monthlyRevenue = allOrders.stream()
                .filter(order -> order.getCreatedAt().isAfter(monthStart))
                .filter(order -> order.getStatus() != OrderStatus.CANCELLED)
                .mapToDouble(order -> order.getTotalAmount().doubleValue())
                .sum();
        analytics.put("monthlyRevenue", monthlyRevenue);

        // Top selling products
        List<Map<String, Object>> topProducts = getTopSellingProducts(organizationId, 5);
        analytics.put("topProducts", topProducts);

        // Order status breakdown
        Map<String, Long> orderStatusBreakdown = getOrderStatusBreakdown(organizationId);
        analytics.put("orderStatusBreakdown", orderStatusBreakdown);

        // Inventory value
        List<Product> allProducts = productRepository.findAllByOrganizationId(organizationId);
        Double inventoryValue = allProducts.stream()
                .mapToDouble(p -> p.getPrice().doubleValue() * p.getStockQuantity())
                .sum();
        analytics.put("inventoryValue", inventoryValue);

        return analytics;
    }

    /**
     * Get top selling products
     */
    public List<Map<String, Object>> getTopSellingProducts(Long organizationId, int limit) {
        List<Order> orders = orderRepository.findAllByOrganizationId(organizationId).stream()
                .filter(order -> order.getStatus() != OrderStatus.CANCELLED)
                .collect(Collectors.toList());

        Map<Long, Integer> productSales = new HashMap<>();
        Map<Long, Double> productRevenue = new HashMap<>();
        Map<Long, String> productNames = new HashMap<>();

        for (Order order : orders) {
            for (OrderItem item : order.getOrderItems()) {
                Long productId = item.getProduct().getId();
                productSales.put(productId, productSales.getOrDefault(productId, 0) + item.getQuantity());
                productRevenue.put(productId, productRevenue.getOrDefault(productId, 0.0) + item.getSubtotal().doubleValue());
                productNames.put(productId, item.getProduct().getName());
            }
        }

        return productSales.entrySet().stream()
                .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                .limit(limit)
                .map(entry -> {
                    Map<String, Object> product = new HashMap<>();
                    product.put("productId", entry.getKey());
                    product.put("productName", productNames.get(entry.getKey()));
                    product.put("totalSold", entry.getValue());
                    product.put("revenue", productRevenue.get(entry.getKey()));
                    return product;
                })
                .collect(Collectors.toList());
    }

    /**
     * Get order status breakdown
     */
    public Map<String, Long> getOrderStatusBreakdown(Long organizationId) {
        List<Order> orders = orderRepository.findAllByOrganizationId(organizationId);
        
        Map<String, Long> breakdown = new HashMap<>();
        for (OrderStatus status : OrderStatus.values()) {
            long count = orders.stream()
                    .filter(order -> order.getStatus() == status)
                    .count();
            breakdown.put(status.name(), count);
        }
        
        return breakdown;
    }

    /**
     * Get sales trend (daily for last 30 days)
     */
    @Cacheable(value = "salesTrend", key = "#organizationId")
    public List<Map<String, Object>> getSalesTrend(Long organizationId, int days) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        
        List<Order> orders = orderRepository.findAllByOrganizationId(organizationId).stream()
                .filter(order -> order.getCreatedAt().isAfter(startDate))
                .filter(order -> order.getStatus() != OrderStatus.CANCELLED)
                .collect(Collectors.toList());

        Map<String, Double> dailySales = new HashMap<>();
        
        for (Order order : orders) {
            String date = order.getCreatedAt().toLocalDate().toString();
            dailySales.put(date, dailySales.getOrDefault(date, 0.0) + order.getTotalAmount().doubleValue());
        }

        return dailySales.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> {
                    Map<String, Object> dataPoint = new HashMap<>();
                    dataPoint.put("date", entry.getKey());
                    dataPoint.put("sales", entry.getValue());
                    return dataPoint;
                })
                .collect(Collectors.toList());
    }

    /**
     * Get inventory forecast (simple moving average)
     */
    public Map<String, Object> getInventoryForecast(Long productId, int daysToForecast) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Get historical order data
        List<Order> orders = orderRepository.findAllByOrganizationId(product.getOrganization().getId());
        
        int totalSold = 0;
        int orderCount = 0;
        
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        
        for (Order order : orders) {
            if (order.getCreatedAt().isAfter(thirtyDaysAgo) && order.getStatus() != OrderStatus.CANCELLED) {
                for (OrderItem item : order.getOrderItems()) {
                    if (item.getProduct().getId().equals(productId)) {
                        totalSold += item.getQuantity();
                        orderCount++;
                    }
                }
            }
        }

        double averageDailySales = orderCount > 0 ? (double) totalSold / 30 : 0;
        int forecastedDemand = (int) Math.ceil(averageDailySales * daysToForecast);
        int currentStock = product.getStockQuantity();
        int suggestedReorder = Math.max(0, forecastedDemand - currentStock);

        Map<String, Object> forecast = new HashMap<>();
        forecast.put("productId", productId);
        forecast.put("productName", product.getName());
        forecast.put("currentStock", currentStock);
        forecast.put("averageDailySales", averageDailySales);
        forecast.put("forecastedDemand", forecastedDemand);
        forecast.put("suggestedReorder", suggestedReorder);
        forecast.put("daysUntilStockout", averageDailySales > 0 ? currentStock / averageDailySales : -1);

        return forecast;
    }

    /**
     * Get low stock alerts
     */
    public List<Map<String, Object>> getLowStockAlerts(Long organizationId) {
        return productRepository.findLowStockProducts(organizationId).stream()
                .map(product -> {
                    Map<String, Object> alert = new HashMap<>();
                    alert.put("productId", product.getId());
                    alert.put("productName", product.getName());
                    alert.put("currentStock", product.getStockQuantity());
                    alert.put("minStockLevel", product.getLowStockThreshold());
                    alert.put("shortage", product.getLowStockThreshold() - product.getStockQuantity());
                    
                    // Add forecast
                    Map<String, Object> forecast = getInventoryForecast(product.getId(), 7);
                    alert.put("suggestedReorder", forecast.get("suggestedReorder"));
                    
                    return alert;
                })
                .collect(Collectors.toList());
    }
}
