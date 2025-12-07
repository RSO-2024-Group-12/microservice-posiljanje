package si.nakupify.service;

import jakarta.enterprise.context.ApplicationScoped;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

/**
 * Internal mock of an external shipping provider. Not exposed via REST.
 * Provides deterministic responses based on the tracking number.
 */
@ApplicationScoped
public class MockShippingProviderService {

    public enum ExternalStatus { PENDING, EN_ROUTE, OUT_FOR_DELIVERY, DELIVERED, CANCELLED }

    public record ProviderStatusDTO(
            String trackingNumber,
            ExternalStatus externalStatus,
            Instant lastUpdated,
            Integer etaMinutes,
            String message
    ) {}

    /**
     * Returns a mocked status for a given tracking number.
     */
    public ProviderStatusDTO getStatus(String trackingNumber) {
        ExternalStatus status = pickStatus(trackingNumber);
        int eta = switch (status) {
            case PENDING -> 120;
            case EN_ROUTE -> 60;
            case OUT_FOR_DELIVERY -> 15;
            case DELIVERED -> 0;
            case CANCELLED -> -1;
        };
        String msg = Map.of(
                ExternalStatus.PENDING, "Label created; waiting for pickup",
                ExternalStatus.EN_ROUTE, "Package in transit",
                ExternalStatus.OUT_FOR_DELIVERY, "Courier is on the way",
                ExternalStatus.DELIVERED, "Package delivered",
                ExternalStatus.CANCELLED, "Shipment cancelled"
        ).get(status);

        Instant lastUpdated = Instant.now().minus(minutesOffset(trackingNumber), ChronoUnit.MINUTES);
        return new ProviderStatusDTO(trackingNumber, status, lastUpdated, eta, msg);
    }

    private ExternalStatus pickStatus(String trackingNumber) {
        if (trackingNumber == null || trackingNumber.isBlank()) return ExternalStatus.PENDING;
        int hash = Math.abs(trackingNumber.hashCode());
        int mod = hash % 10;
        return switch (mod) {
            case 0 -> ExternalStatus.CANCELLED;
            case 1, 2 -> ExternalStatus.PENDING;
            case 3, 4, 5 -> ExternalStatus.EN_ROUTE;
            case 6, 7, 8 -> ExternalStatus.OUT_FOR_DELIVERY;
            default -> ExternalStatus.DELIVERED;
        };
    }

    private long minutesOffset(String trackingNumber) {
        if (trackingNumber == null) return 5;
        return Math.abs(trackingNumber.hashCode() % 90);
    }
}
