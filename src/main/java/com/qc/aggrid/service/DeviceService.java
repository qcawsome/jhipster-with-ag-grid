package com.qc.aggrid.service;

import com.qc.aggrid.domain.Device;
import com.qc.aggrid.repository.DeviceRepository;
import com.qc.aggrid.service.dto.DeviceDTO;
import com.qc.aggrid.service.mapper.DeviceMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Device}.
 */
@Service
@Transactional
public class DeviceService {

    private final Logger log = LoggerFactory.getLogger(DeviceService.class);

    private final DeviceRepository deviceRepository;

    private final DeviceMapper deviceMapper;

    public DeviceService(DeviceRepository deviceRepository, DeviceMapper deviceMapper) {
        this.deviceRepository = deviceRepository;
        this.deviceMapper = deviceMapper;
    }

    /**
     * Save a device.
     *
     * @param deviceDTO the entity to save.
     * @return the persisted entity.
     */
    public DeviceDTO save(DeviceDTO deviceDTO) {
        log.debug("Request to save Device : {}", deviceDTO);
        Device device = deviceMapper.toEntity(deviceDTO);
        device = deviceRepository.save(device);
        return deviceMapper.toDto(device);
    }

    /**
     * Update a device.
     *
     * @param deviceDTO the entity to save.
     * @return the persisted entity.
     */
    public DeviceDTO update(DeviceDTO deviceDTO) {
        log.debug("Request to update Device : {}", deviceDTO);
        Device device = deviceMapper.toEntity(deviceDTO);
        device = deviceRepository.save(device);
        return deviceMapper.toDto(device);
    }

    /**
     * Partially update a device.
     *
     * @param deviceDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<DeviceDTO> partialUpdate(DeviceDTO deviceDTO) {
        log.debug("Request to partially update Device : {}", deviceDTO);

        return deviceRepository
            .findById(deviceDTO.getId())
            .map(existingDevice -> {
                deviceMapper.partialUpdate(existingDevice, deviceDTO);

                return existingDevice;
            })
            .map(deviceRepository::save)
            .map(deviceMapper::toDto);
    }

    /**
     * Get all the devices.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<DeviceDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Devices");
        return deviceRepository.findAll(pageable).map(deviceMapper::toDto);
    }

    /**
     * Get one device by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<DeviceDTO> findOne(Long id) {
        log.debug("Request to get Device : {}", id);
        return deviceRepository.findById(id).map(deviceMapper::toDto);
    }

    /**
     * Delete the device by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Device : {}", id);
        deviceRepository.deleteById(id);
    }
}
