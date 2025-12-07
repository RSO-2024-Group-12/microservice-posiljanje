package si.nakupify.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateShipmentRequest(
        @NotNull Long orderId,
        @NotBlank String carrier,
        @NotNull @Min(0) Long shippingCostCents,
        @NotBlank String recipientName,
        @NotBlank String street,
        @NotBlank String houseNumber,
        @NotBlank String city,
        @NotBlank String postalCode,
        @NotBlank String country
) {}
