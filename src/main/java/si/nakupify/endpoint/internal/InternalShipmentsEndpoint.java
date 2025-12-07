package si.nakupify.endpoint.internal;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import si.nakupify.entity.ShipmentStatus;
import si.nakupify.service.ShipmentService;
import si.nakupify.dto.request.CreateShipmentRequest;

import java.net.URI;
import java.util.Optional;

@Path("/internal/shipments")
public class InternalShipmentsEndpoint {

    @Inject
    ShipmentService service;

    // Endpoint delegates to service only

    public record UpdateStatusRequest(ShipmentStatus status) {}

    // Internal endpoint exposes only create and status updates.

    @POST
    @Transactional
    public Response create(@Valid CreateShipmentRequest req) {
        return service.createResponse(req);
    }

    @PATCH
    @Path("/{id}/status")
    @Transactional
    public Response updateStatus(@PathParam("id") Long id, UpdateStatusRequest req) {
        if (req == null || req.status == null) {
            throw new BadRequestException("status is required");
        }
        return service.updateStatusResponse(id, req.status);
    }
}
