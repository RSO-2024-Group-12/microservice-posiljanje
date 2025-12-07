package si.nakupify.dto;

import si.nakupify.entity.ShipmentEntity;
import si.nakupify.entity.ShipmentStatus;

import java.time.Instant;

public record ShipmentDTO(
        Long id,
        Long orderId,
        String trackingNumber,
        String carrier,
        ShipmentStatus status,
        String recipientName,
        String street,
        String houseNumber,
        String city,
        String postalCode,
        String country,
        Long shippingCostCents,
        Instant createdAt,
        Instant updatedAt
) {
    public static ShipmentDTO from(ShipmentEntity s) {
        if (s == null) return null;
        return new ShipmentDTO(
                s.id,
                s.orderId,
                s.trackingNumber,
                s.carrier,
                s.status,
                s.recipientName,
                s.street,
                s.houseNumber,
                s.city,
                s.postalCode,
                s.country,
                s.shippingCostCents,
                s.createdAt,
                s.updatedAt
        );
    }
}
