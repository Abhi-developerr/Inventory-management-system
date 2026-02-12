package com.example.demo.enums;

public enum PurchaseOrderStatus {
    DRAFT("Draft"),
    PENDING_APPROVAL("Pending Approval"),
    APPROVED("Approved"),
    ORDERED("Ordered"),
    RECEIVED("Received"),
    CANCELLED("Cancelled");

    private final String displayName;

    PurchaseOrderStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
