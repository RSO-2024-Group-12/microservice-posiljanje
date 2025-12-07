package si.nakupify.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.NotFoundException;
import si.nakupify.entity.ShipmentEntity;

@ApplicationScoped
public class ShipmentRepository implements PanacheRepositoryBase<ShipmentEntity, Long> {
    public ShipmentEntity findByIdOrThrow(Long id) throws NotFoundException {
        ShipmentEntity entity = findById(id);
        if (entity == null) {
            throw new NotFoundException("Shipment not found: id=" + id);
        }
        return entity;
    }
}
