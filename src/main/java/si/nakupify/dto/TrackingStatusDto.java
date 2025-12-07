package si.nakupify.dto;

import java.time.Instant;

import si.nakupify.entity.ShipmentStatus;
import si.nakupify.service.MockShippingProviderService;

/**
 * Public DTO for exposing tracking information to users.
 */
public record TrackingStatusDto(
        String trackingNumber,
        String externalStatus,
        Instant lastUpdated,
        Integer etaMinutes,
        String message,
        ShipmentStatus shipmentStatus
) {
    public static TrackingStatusDto from(MockShippingProviderService.ProviderStatusDTO provider,
                                         ShipmentStatus shipmentStatus) {
        return new TrackingStatusDto(
                provider.trackingNumber(),
                provider.externalStatus().name(),
                provider.lastUpdated(),
                provider.etaMinutes(),
                provider.message(),
                shipmentStatus
        );
    }
}
