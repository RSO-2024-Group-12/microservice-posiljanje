package si.nakupify.entity;

/**
 * Status values for a shipment lifecycle.
 */
public enum ShipmentStatus {
    CREATED,
    LABEL_CREATED,
    IN_TRANSIT,
    OUT_FOR_DELIVERY,
    DELIVERED,
    CANCELLED
}
