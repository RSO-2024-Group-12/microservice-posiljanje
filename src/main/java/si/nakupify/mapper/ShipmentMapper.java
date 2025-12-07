package si.nakupify.mapper;

import org.mapstruct.Mapper;
import si.nakupify.dto.ShipmentDto;
import si.nakupify.entity.ShipmentEntity;

@Mapper(componentModel = "cdi")
public interface ShipmentMapper {
    ShipmentDto toDto(ShipmentEntity entity);
}
