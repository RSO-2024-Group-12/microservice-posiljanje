package si.nakupify.mapper;

import org.mapstruct.Mapper;
import si.nakupify.dto.ShipmentDTO;
import si.nakupify.entity.ShipmentEntity;

@Mapper(componentModel = "cdi")
public interface ShipmentMapper {
    ShipmentDTO toDto(ShipmentEntity entity);
}
