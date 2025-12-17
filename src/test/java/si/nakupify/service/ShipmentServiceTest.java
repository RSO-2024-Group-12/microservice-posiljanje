package si.nakupify.service;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import si.nakupify.dto.ShipmentDto;
import si.nakupify.dto.TrackingStatusDto;
import si.nakupify.entity.ShipmentEntity;
import si.nakupify.entity.ShipmentStatus;
import si.nakupify.mapper.ShipmentMapper;
import si.nakupify.repository.ShipmentRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@QuarkusTest
class ShipmentServiceTest {

    @Inject
    ShipmentService service;

    @InjectMock
    ShipmentRepository repository;

    @InjectMock
    MockShippingProviderService providerService;

    @InjectMock
    ShipmentMapper mapper;

    private static ShipmentEntity entity(long id, String tracking) {
        ShipmentEntity s = new ShipmentEntity();
        s.id = id;
        s.orderId = 5L;
        s.trackingNumber = tracking;
        s.status = ShipmentStatus.CREATED;
        s.createdAt = Instant.now();
        s.updatedAt = s.createdAt;
        return s;
    }

    @Test
    void getByIdDto_returnsMappedDto() {
        var e = entity(1, "TRK-1");
        when(repository.findByIdOrThrow(1L)).thenReturn(e);
        ShipmentDto dto = new ShipmentDto(1L, 5L, "TRK-1", "LOCAL", ShipmentStatus.CREATED,
                "John","St","1","City","1000","SI", 100L, Instant.now(), Instant.now());
        when(mapper.toDto(e)).thenReturn(dto);

        var result = service.getByIdDto(1L);
        assertEquals(1L, result.id());
        verify(repository).findByIdOrThrow(1L);
        verify(mapper).toDto(e);
    }

    @Test
    void updateStatusDto_updatesAndReturnsDto() {
        var e = entity(3, "TRK-3");
        when(repository.findByIdOrThrow(3L)).thenReturn(e);
        ShipmentDto dto = new ShipmentDto(3L, 5L, "TRK-3", "LOCAL", ShipmentStatus.DELIVERED,
                "John","St","1","City","1000","SI", 100L, Instant.now(), Instant.now());
        when(mapper.toDto(e)).thenReturn(dto);

        var out = service.updateStatusDto(3L, ShipmentStatus.DELIVERED);
        assertEquals(ShipmentStatus.DELIVERED, out.status());
    }

    @Test
    void trackByTrackingNumberDto_returnsProviderAndMaybeShipmentStatus() {
        var provider = new MockShippingProviderService.ProviderStatusDTO("TRK-ABC",
                MockShippingProviderService.ExternalStatus.EN_ROUTE, Instant.now(), 60, "Package in transit");
        when(providerService.getStatus("TRK-ABC")).thenReturn(provider);
        when(repository.find("trackingNumber", "TRK-ABC")).thenReturn(mock(PanacheQuery.class));
        when(repository.find("trackingNumber", "TRK-ABC").firstResultOptional()).thenReturn(Optional.of(entity(9, "TRK-ABC")));

        TrackingStatusDto result = service.trackByTrackingNumberDto("TRK-ABC");
        assertEquals("TRK-ABC", result.trackingNumber());
        assertNotNull(result.shipmentStatus());
    }
}
