package com.hotelManagement.system.entity;

public enum PaymentStatus {
	PENDING,        // Payment not completed yet
    Paid,           // Payment successful
    FAILED,         // Payment failed
    REFUNDED,       // Payment returned
    CANCELLED       // Payment cancelled
}


