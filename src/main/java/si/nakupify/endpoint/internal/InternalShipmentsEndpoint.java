package si.nakupify.endpoint.internal;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import si.nakupify.dto.request.UpdateStatusRequestDto;
import si.nakupify.entity.ShipmentStatus;
import si.nakupify.service.ShipmentService;
import si.nakupify.dto.request.ShipmentRequestDto;
import si.nakupify.dto.ShipmentDto;

@Path("/internal/shipments")
@Transactional
public class InternalShipmentsEndpoint {

    @Inject
    ShipmentService service;

    @POST
    public ShipmentDto create(@NotNull @Valid ShipmentRequestDto req) {
        return service.createReturningDto(req);
    }

    @PATCH
    @Path("/{id}/status")
    public ShipmentDto updateStatus(@PathParam("id") Long id, @NotNull @Valid UpdateStatusRequestDto req) {
        return service.updateStatusDto(id, req.status());
    }
}
