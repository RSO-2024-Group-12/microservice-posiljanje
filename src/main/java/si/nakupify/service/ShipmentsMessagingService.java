package si.nakupify.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
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

    public void emitShipmentCreated(ShipmentEntity s) {
        try {
            var node = mapper.createObjectNode();
            node.put("type", "SHIPMENT_CREATED");
            node.put("shipmentId", s.id);
            node.put("orderId", s.orderId);
            node.put("trackingNumber", s.trackingNumber);
            node.put("status", s.status.name());
            shipmentsEmitter.send(mapper.writeValueAsString(node));
        } catch (Exception ignored) {
        }
    }

    public void emitShipmentUpdated(ShipmentEntity s) {
        try {
            var node = mapper.createObjectNode();
            node.put("type", "SHIPMENT_UPDATED");
            node.put("shipmentId", s.id);
            node.put("status", s.status.name());
            shipmentsEmitter.send(mapper.writeValueAsString(node));
        } catch (Exception ignored) {
        }
    }

    @Incoming("nakup-in")
    public void onPurchaseMessage(String message) {
        System.out.println("[posiljanje] Received from nakup: " + message);
    }
}
