package si.nakupify.endpoint;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.InjectMock;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import si.nakupify.dto.ShipmentDto;
import si.nakupify.entity.ShipmentStatus;
import si.nakupify.service.ShipmentService;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@QuarkusTest
class InternalShipmentsEndpointTest {

    @InjectMock
    ShipmentService service;

    private static ShipmentDto dto(long id, long orderId) {
        return new ShipmentDto(id, orderId, "TRK-NEW", "LOCAL", ShipmentStatus.CREATED,
                "John","St","1","City","1000","SI", 100L, Instant.now(), Instant.now());
    }

    @Test
    void create_returnsDto() {
        when(service.createReturningDto(any())).thenReturn(dto(100, 50));

        String body = "{" +
                "\"orderId\":50,\"carrier\":\"LOCAL\",\"shippingCostCents\":100,\"recipientName\":\"John\",\"street\":\"St\",\"houseNumber\":\"1\",\"city\":\"City\",\"postalCode\":\"1000\",\"country\":\"SI\"}";

        given()
            .contentType(ContentType.JSON)
            .body(body)
        .when()
            .post("/internal/shipments")
        .then()
            .statusCode(200)
            .body("id", equalTo(100));

        verify(service).createReturningDto(any());
    }

    @Test
    void updateStatus_returnsDto() {
        when(service.updateStatusDto(77L, ShipmentStatus.DELIVERED)).thenReturn(dto(77, 40));

        given()
            .contentType(ContentType.JSON)
            .body(Map.of("status", "DELIVERED"))
        .when()
            .patch("/internal/shipments/77/status")
        .then()
            .statusCode(200)
            .body("id", equalTo(77));

        verify(service).updateStatusDto(77L, ShipmentStatus.DELIVERED);
    }
}
