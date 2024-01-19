package com.qc.aggrid.service.mapper;

import com.qc.aggrid.domain.Device;
import com.qc.aggrid.service.dto.DeviceDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Device} and its DTO {@link DeviceDTO}.
 */
@Mapper(componentModel = "spring")
public interface DeviceMapper extends EntityMapper<DeviceDTO, Device> {}
