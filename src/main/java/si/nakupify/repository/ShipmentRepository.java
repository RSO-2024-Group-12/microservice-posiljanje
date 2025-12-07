package si.nakupify.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import si.nakupify.entity.ShipmentEntity;

@ApplicationScoped
public class ShipmentRepository implements PanacheRepositoryBase<ShipmentEntity, Long> {
}
