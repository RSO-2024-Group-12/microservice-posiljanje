package si.nakupify.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "shipments")
public class ShipmentEntity extends PanacheEntity {

    // Reference to order (narocilo)
    @Column(name = "order_id", nullable = false)
    public Long orderId;

    @Column(name = "tracking_number", unique = true, nullable = false, length = 64)
    public String trackingNumber;

    @Column(length = 64)
    public String carrier; // e.g., DHL, UPS (simulated)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    public ShipmentStatus status = ShipmentStatus.CREATED;

    // Very simplified address snapshot at shipment time
    @Column(name = "recipient_name", length = 128)
    public String recipientName;
    @Column(length = 128)
    public String street;
    @Column(length = 64)
    public String houseNumber;
    @Column(length = 64)
    public String city;
    @Column(length = 16)
    public String postalCode;
    @Column(length = 64)
    public String country;

    @Column(name = "shipping_cost_cents")
    public Long shippingCostCents; // store in smallest currency unit

    @Column(name = "created_at", nullable = false)
    public Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    public Instant updatedAt = Instant.now();
}
