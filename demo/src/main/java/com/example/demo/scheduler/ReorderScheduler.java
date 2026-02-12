package com.example.demo.scheduler;

import com.example.demo.service.PurchaseOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * ReorderScheduler - Automated Purchase Order generation
 * 
 * Runs daily at 2 AM to evaluate low-stock products and auto-generate reorder POs
 * Helps maintain stock levels automatically without manual intervention
 */
@Component
public class ReorderScheduler {

    private static final Logger log = LoggerFactory.getLogger(ReorderScheduler.class);

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @Scheduled(cron = "0 0 2 * * *")
    public void generateAutoReorderPurchaseOrders() {
        log.info("Starting scheduled auto-reorder PO generation");
        
        try {
            purchaseOrderService.generateAutoReorderPurchaseOrders();
            log.info("Completed scheduled auto-reorder PO generation");
        } catch (Exception ex) {
            log.error("Error during scheduled auto-reorder PO generation", ex);
        }
    }
}
