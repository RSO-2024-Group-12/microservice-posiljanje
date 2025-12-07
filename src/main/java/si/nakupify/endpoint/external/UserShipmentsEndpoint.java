package si.nakupify.endpoint.external;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import si.nakupify.dto.ShipmentDTO;
import si.nakupify.service.ShipmentService;

import java.util.List;

/**
 * Public/user-facing read-only endpoints for shipments.
 */
@Path("/api/shipments")
public class UserShipmentsEndpoint {

    @Inject
    ShipmentService service;

    // Only delegates to service

    @GET
    public List<ShipmentDTO> list(@QueryParam("page") @DefaultValue("0") int page,
                                  @QueryParam("size") @DefaultValue("50") int size,
                                  @QueryParam("orderId") Long orderId) {
        return service.listDtos(page, size, orderId);
    }

    @GET
    @Path("/{id}")
    public Response get(@PathParam("id") Long id) {
        return service.getByIdResponse(id);
    }

    @GET
    @Path("/{id}/tracking")
    public Response trackById(@PathParam("id") Long id) {
        return service.trackByIdResponse(id);
    }

    @GET
    @Path("/track/{trackingNumber}")
    public Response trackByTrackingNumber(@PathParam("trackingNumber") String trackingNumber) {
        return service.trackByTrackingNumberResponse(trackingNumber);
    }
}
