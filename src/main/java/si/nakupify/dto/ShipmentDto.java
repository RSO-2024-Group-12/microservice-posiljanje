package si.nakupify.dto;

import si.nakupify.entity.ShipmentEntity;
import si.nakupify.entity.ShipmentStatus;

import java.time.Instant;

public record ShipmentDto(
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
}
