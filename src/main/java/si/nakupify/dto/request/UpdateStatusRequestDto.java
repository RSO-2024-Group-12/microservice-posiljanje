package si.nakupify.dto.request;

import jakarta.validation.constraints.NotNull;
import si.nakupify.entity.ShipmentStatus;

public record UpdateStatusRequestDto(@NotNull ShipmentStatus status) {
}
