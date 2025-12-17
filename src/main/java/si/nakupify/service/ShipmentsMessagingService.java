package si.nakupify.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import si.nakupify.dto.vo.ShipmentCreatedVO;
import si.nakupify.dto.vo.ShipmentRequestedVO;
import si.nakupify.entity.ShipmentEntity;

/**
 * Kafka producer/consumer for microservice-posiljanje.
 * Produces shipment events and consumes messages from microservice-nakup.
 */
@ApplicationScoped
public class ShipmentsMessagingService {

    @Inject
    @Channel("shipments-out")
    Emitter<String> shipmentsEmitter;

    @Inject
    ObjectMapper mapper;

    @Inject
    ShipmentService shipmentService;

    public void emitShipmentCreated(ShipmentEntity s) {
        try {
            var vo = new ShipmentCreatedVO(
                    s.id,
                    s.orderId,
                    s.trackingNumber,
                    s.status.name()
            );
            shipmentsEmitter.send(mapper.writeValueAsString(vo));
        } catch (Exception ignored) {
        }
    }

    @Incoming("nakup-in")
    public void onPurchaseMessage(String message) {
        System.out.println("[posiljanje] Received from nakup: " + message);
    }

    @Incoming("shipment-requests-in")
    public void onShipmentRequested(String message) {
        try {
            var vo = mapper.readValue(message, ShipmentRequestedVO.class);
            System.out.println("[posiljanje] Creating shipment for order " + vo.orderId());
            shipmentService.create(
                    vo.orderId(),
                    vo.carrier(),
                    vo.shippingCostCents(),
                    vo.recipientName(),
                    vo.street(),
                    vo.houseNumber(),
                    vo.city(),
                    vo.postalCode(),
                    vo.country()
            );
        } catch (Exception e) {
            System.err.println("[posiljanje] Failed to process SHIPMENT_REQUESTED: " + e.getMessage());
        }
    }
}
