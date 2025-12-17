package si.nakupify.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import si.nakupify.entity.ShipmentEntity;
import si.nakupify.entity.ShipmentStatus;
import si.nakupify.repository.ShipmentRepository;
import si.nakupify.mapper.ShipmentMapper;
import si.nakupify.dto.ShipmentDto;
import si.nakupify.dto.TrackingStatusDto;
import si.nakupify.dto.request.ShipmentRequestDto;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.HexFormat;
import java.util.List;

@ApplicationScoped
public class ShipmentService {

    @Inject
    ShipmentRepository repository;

    @Inject
    ShipmentsMessagingService messaging;

    @Inject
    MockShippingProviderService providerService;

    @Inject
    ShipmentMapper mapper;

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final HexFormat HEX = HexFormat.of();

    public List<ShipmentEntity> listAll(int page, int size) {
        return repository.findAll().page(page, size).list();
    }

    public java.util.Optional<ShipmentEntity> findByOrderId(Long orderId) {
        return repository.find("orderId", orderId).firstResultOptional();
    }

    public java.util.Optional<ShipmentEntity> findByTrackingNumber(String trackingNumber) {
        if (trackingNumber == null) return java.util.Optional.empty();
        return repository.find("trackingNumber", trackingNumber).firstResultOptional();
    }

    @Transactional
    public ShipmentEntity create(Long orderId, String carrier, Long costCents,
                                 String recipientName, String street, String houseNumber,
                                 String city, String postalCode, String country) {
        ShipmentEntity s = new ShipmentEntity();
        s.orderId = orderId;
        s.carrier = carrier == null ? "LOCAL" : carrier;
        s.shippingCostCents = costCents;
        s.trackingNumber = generateTracking();
        s.status = ShipmentStatus.CREATED;
        s.recipientName = recipientName;
        s.street = street;
        s.houseNumber = houseNumber;
        s.city = city;
        s.postalCode = postalCode;
        s.country = country;
        s.createdAt = Instant.now();
        s.updatedAt = s.createdAt;

        repository.persist(s);
        messaging.emitShipmentCreated(s);
        return s;
    }

    public ShipmentEntity create(ShipmentRequestDto req) {
        return create(
                req.orderId(),
                req.carrier(),
                req.shippingCostCents(),
                req.recipientName(),
                req.street(),
                req.houseNumber(),
                req.city(),
                req.postalCode(),
                req.country()
        );
    }

    public ShipmentEntity updateStatus(Long id, ShipmentStatus newStatus) throws NotFoundException {
        ShipmentEntity s = repository.findByIdOrThrow(id);
        s.status = newStatus;
        s.updatedAt = Instant.now();
        return s;
    }

    private String generateTracking() {
        byte[] buf = new byte[8];
        RANDOM.nextBytes(buf);
        return "TRK-" + HEX.formatHex(buf).toUpperCase();
    }

    // DTO helpers for endpoints
    public List<ShipmentDto> listDtos(int page, int size, Long orderId) {
        if (size < 1) size = 50;
        if (orderId != null) {
            return findByOrderId(orderId).map(mapper::toDto).stream().toList();
        }
        return listAll(page, size).stream().map(mapper::toDto).toList();
    }

    public ShipmentDto getByIdDto(Long id) throws NotFoundException {
        var s = repository.findByIdOrThrow(id);
        return mapper.toDto(s);
    }

    public TrackingStatusDto trackByIdDto(Long id) throws NotFoundException {
        var s = repository.findByIdOrThrow(id);
        var provider = providerService.getStatus(s.trackingNumber);
        return TrackingStatusDto.from(provider, s.status);
    }

    public TrackingStatusDto trackByTrackingNumberDto(String trackingNumber) {
        if (trackingNumber == null || trackingNumber.isBlank()) {
            throw new BadRequestException("trackingNumber is required");
        }
        var provider = providerService.getStatus(trackingNumber);
        var shipmentOpt = findByTrackingNumber(trackingNumber);
        return TrackingStatusDto.from(provider, shipmentOpt.map(s -> s.status).orElse(null));
    }

    public ShipmentDto createReturningDto(ShipmentRequestDto req) {
        var s = create(req);
        return mapper.toDto(s);
    }

    public ShipmentDto updateStatusDto(Long id, ShipmentStatus status) throws NotFoundException {
        var s = updateStatus(id, status);
        return mapper.toDto(s);
    }
}
