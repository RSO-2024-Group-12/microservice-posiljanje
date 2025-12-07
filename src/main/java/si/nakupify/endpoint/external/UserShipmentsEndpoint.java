package si.nakupify.endpoint.external;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import si.nakupify.dto.ShipmentDto;
import si.nakupify.dto.TrackingStatusDto;
import si.nakupify.service.ShipmentService;

import java.util.List;

@Path("/api/shipments")
@Transactional
public class UserShipmentsEndpoint {

    @Inject
    ShipmentService service;

    @GET
    public List<ShipmentDto> list(@QueryParam("page") @DefaultValue("0") int page,
                                  @QueryParam("size") @DefaultValue("50") int size,
                                  @QueryParam("orderId") Long orderId) {
        return service.listDtos(page, size, orderId);
    }

    @GET
    @Path("/{id}")
    public ShipmentDto get(@PathParam("id") Long id) {
        return service.getByIdDto(id);
    }

    @GET
    @Path("/{id}/tracking")
    public TrackingStatusDto trackById(@PathParam("id") Long id) {
        return service.trackByIdDto(id);
    }

    @GET
    @Path("/track/{trackingNumber}")
    public TrackingStatusDto trackByTrackingNumber(@PathParam("trackingNumber") String trackingNumber) {
        return service.trackByTrackingNumberDto(trackingNumber);
    }
}
