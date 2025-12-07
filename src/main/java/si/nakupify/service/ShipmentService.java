package si.nakupify.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import si.nakupify.entity.ShipmentEntity;
import si.nakupify.entity.ShipmentStatus;
import si.nakupify.repository.ShipmentRepository;
import si.nakupify.mapper.ShipmentMapper;
import si.nakupify.dto.ShipmentDTO;
import si.nakupify.dto.TrackingStatusDTO;
import si.nakupify.dto.request.CreateShipmentRequest;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.core.Response;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;

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

    public Optional<ShipmentEntity> findById(Long id) {
        return Optional.ofNullable(repository.findById(id));
    }

    public List<ShipmentEntity> listAll(int page, int size) {
        return repository.findAll().page(page, size).list();
    }

    public Optional<ShipmentEntity> findByOrderId(Long orderId) {
        return repository.find("orderId", orderId).firstResultOptional();
    }

    public Optional<ShipmentEntity> findByTrackingNumber(String trackingNumber) {
        if (trackingNumber == null) return Optional.empty();
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
        if (messaging != null) messaging.emitShipmentCreated(s);
        return s;
    }

    @Transactional
    public ShipmentEntity create(CreateShipmentRequest req) {
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

    @Transactional
    public Optional<ShipmentEntity> updateStatus(Long id, ShipmentStatus newStatus) {
        ShipmentEntity s = repository.findById(id);
        if (s == null) return Optional.empty();
        s.status = newStatus;
        s.updatedAt = Instant.now();
        if (messaging != null) messaging.emitShipmentUpdated(s);
        return Optional.of(s);
    }

    private String generateTracking() {
        byte[] buf = new byte[8];
        RANDOM.nextBytes(buf);
        return "TRK-" + HEX.formatHex(buf).toUpperCase();
    }

    // Convenience DTO/Response helpers for endpoints
    public List<ShipmentDTO> listDtos(int page, int size, Long orderId) {
        if (size < 1) size = 50;
        if (orderId != null) {
            return findByOrderId(orderId).map(mapper::toDto).stream().toList();
        }
        return listAll(page, size).stream().map(mapper::toDto).toList();
    }

    public Response getByIdResponse(Long id) {
        var opt = findById(id);
        return opt.<Response>map(s -> Response.ok(mapper.toDto(s)).build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
    }

    public Response trackByIdResponse(Long id) {
        var sOpt = findById(id);
        if (sOpt.isEmpty()) return Response.status(Response.Status.NOT_FOUND).build();
        var s = sOpt.get();
        var provider = providerService.getStatus(s.trackingNumber);
        return Response.ok(TrackingStatusDTO.from(provider, s.status)).build();
    }

    public Response trackByTrackingNumberResponse(String trackingNumber) {
        if (trackingNumber == null || trackingNumber.isBlank()) {
            throw new BadRequestException("trackingNumber is required");
        }
        var provider = providerService.getStatus(trackingNumber);
        var shipmentOpt = findByTrackingNumber(trackingNumber);
        return Response.ok(TrackingStatusDTO.from(provider, shipmentOpt.map(s -> s.status).orElse(null))).build();
    }

    public Response createResponse(CreateShipmentRequest req) {
        var s = create(req);
        var dto = mapper.toDto(s);
        return Response.created(java.net.URI.create("/internal/shipments/" + s.id)).entity(dto).build();
    }

    public Response updateStatusResponse(Long id, ShipmentStatus status) {
        var s = updateStatus(id, status);
        return s.<Response>map(value -> Response.ok(mapper.toDto(value)).build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
    }
}
