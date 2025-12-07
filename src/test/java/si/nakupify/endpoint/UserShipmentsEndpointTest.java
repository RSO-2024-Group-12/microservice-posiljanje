package si.nakupify.endpoint;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.InjectMock;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import si.nakupify.dto.ShipmentDto;
import si.nakupify.dto.TrackingStatusDto;
import si.nakupify.entity.ShipmentStatus;
import si.nakupify.service.ShipmentService;

import java.time.Instant;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

@QuarkusTest
class UserShipmentsEndpointTest {

    @InjectMock
    ShipmentService service;

    private static ShipmentDto dto(long id, long orderId) {
        return new ShipmentDto(id, orderId, "TRK-XYZ", "LOCAL", ShipmentStatus.CREATED,
                "John","St","1","City","1000","SI", 100L, Instant.now(), Instant.now());
    }

    @Test
    void list_returnsDtos() {
        when(service.listDtos(0, 50, 5L)).thenReturn(List.of(dto(1,5)));

        given()
            .accept(ContentType.JSON)
            .queryParam("orderId", 5)
        .when()
            .get("/api/shipments")
        .then()
            .statusCode(200)
            .body("size()", is(1))
            .body("[0].orderId", equalTo(5));

        verify(service).listDtos(0, 50, 5L);
    }

    @Test
    void get_returnsDto() {
        when(service.getByIdDto(7L)).thenReturn(dto(7, 3));

        given()
            .accept(ContentType.JSON)
        .when()
            .get("/api/shipments/7")
        .then()
            .statusCode(200)
            .body("id", equalTo(7));
    }

    @Test
    void trackById_returnsTrackingDto() {
        var t = new TrackingStatusDto("TRK-XYZ", "IN_TRANSIT", Instant.now(), 60, "Package in transit", ShipmentStatus.IN_TRANSIT);
        when(service.trackByIdDto(9L)).thenReturn(t);

        given()
            .accept(ContentType.JSON)
        .when()
            .get("/api/shipments/9/tracking")
        .then()
            .statusCode(200)
            .body("trackingNumber", equalTo("TRK-XYZ"));
    }

    @Test
    void trackByTrackingNumber_returnsTrackingDto() {
        var t = new TrackingStatusDto("TRK-ABC", "PENDING", Instant.now(), 120, "Label created", ShipmentStatus.CREATED);
        when(service.trackByTrackingNumberDto("TRK-ABC")).thenReturn(t);

        given()
            .accept(ContentType.JSON)
        .when()
            .get("/api/shipments/track/TRK-ABC")
        .then()
            .statusCode(200)
            .body("trackingNumber", equalTo("TRK-ABC"));
    }
}
