package com.qc.aggrid.web.rest;

import static com.qc.aggrid.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.qc.aggrid.IntegrationTest;
import com.qc.aggrid.domain.Device;
import com.qc.aggrid.repository.DeviceRepository;
import com.qc.aggrid.service.criteria.DeviceCriteria;
import com.qc.aggrid.service.dto.DeviceDTO;
import com.qc.aggrid.service.mapper.DeviceMapper;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link DeviceResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class DeviceResourceIT {

    private static final String DEFAULT_GROUP = "AAAAAAAAAA";
    private static final String UPDATED_GROUP = "BBBBBBBBBB";

    private static final String DEFAULT_TEXT = "AAAAAAAAAA";
    private static final String UPDATED_TEXT = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_DATE = LocalDate.ofEpochDay(-1L);

    private static final ZonedDateTime DEFAULT_DATE_TIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_DATE_TIME = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_DATE_TIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final Boolean DEFAULT_CHECK = false;
    private static final Boolean UPDATED_CHECK = true;

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/devices";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private DeviceMapper deviceMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDeviceMockMvc;

    private Device device;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Device createEntity(EntityManager em) {
        Device device = new Device()
            .group(DEFAULT_GROUP)
            .text(DEFAULT_TEXT)
            .date(DEFAULT_DATE)
            .dateTime(DEFAULT_DATE_TIME)
            .check(DEFAULT_CHECK)
            .description(DEFAULT_DESCRIPTION);
        return device;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Device createUpdatedEntity(EntityManager em) {
        Device device = new Device()
            .group(UPDATED_GROUP)
            .text(UPDATED_TEXT)
            .date(UPDATED_DATE)
            .dateTime(UPDATED_DATE_TIME)
            .check(UPDATED_CHECK)
            .description(UPDATED_DESCRIPTION);
        return device;
    }

    @BeforeEach
    public void initTest() {
        device = createEntity(em);
    }

    @Test
    @Transactional
    void createDevice() throws Exception {
        int databaseSizeBeforeCreate = deviceRepository.findAll().size();
        // Create the Device
        DeviceDTO deviceDTO = deviceMapper.toDto(device);
        restDeviceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(deviceDTO)))
            .andExpect(status().isCreated());

        // Validate the Device in the database
        List<Device> deviceList = deviceRepository.findAll();
        assertThat(deviceList).hasSize(databaseSizeBeforeCreate + 1);
        Device testDevice = deviceList.get(deviceList.size() - 1);
        assertThat(testDevice.getGroup()).isEqualTo(DEFAULT_GROUP);
        assertThat(testDevice.getText()).isEqualTo(DEFAULT_TEXT);
        assertThat(testDevice.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testDevice.getDateTime()).isEqualTo(DEFAULT_DATE_TIME);
        assertThat(testDevice.getCheck()).isEqualTo(DEFAULT_CHECK);
        assertThat(testDevice.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void createDeviceWithExistingId() throws Exception {
        // Create the Device with an existing ID
        device.setId(1L);
        DeviceDTO deviceDTO = deviceMapper.toDto(device);

        int databaseSizeBeforeCreate = deviceRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDeviceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(deviceDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Device in the database
        List<Device> deviceList = deviceRepository.findAll();
        assertThat(deviceList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllDevices() throws Exception {
        // Initialize the database
        deviceRepository.saveAndFlush(device);

        // Get all the deviceList
        restDeviceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(device.getId().intValue())))
            .andExpect(jsonPath("$.[*].group").value(hasItem(DEFAULT_GROUP)))
            .andExpect(jsonPath("$.[*].text").value(hasItem(DEFAULT_TEXT)))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].dateTime").value(hasItem(sameInstant(DEFAULT_DATE_TIME))))
            .andExpect(jsonPath("$.[*].check").value(hasItem(DEFAULT_CHECK.booleanValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getDevice() throws Exception {
        // Initialize the database
        deviceRepository.saveAndFlush(device);

        // Get the device
        restDeviceMockMvc
            .perform(get(ENTITY_API_URL_ID, device.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(device.getId().intValue()))
            .andExpect(jsonPath("$.group").value(DEFAULT_GROUP))
            .andExpect(jsonPath("$.text").value(DEFAULT_TEXT))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.dateTime").value(sameInstant(DEFAULT_DATE_TIME)))
            .andExpect(jsonPath("$.check").value(DEFAULT_CHECK.booleanValue()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getDevicesByIdFiltering() throws Exception {
        // Initialize the database
        deviceRepository.saveAndFlush(device);

        Long id = device.getId();

        defaultDeviceShouldBeFound("id.equals=" + id);
        defaultDeviceShouldNotBeFound("id.notEquals=" + id);

        defaultDeviceShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultDeviceShouldNotBeFound("id.greaterThan=" + id);

        defaultDeviceShouldBeFound("id.lessThanOrEqual=" + id);
        defaultDeviceShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllDevicesByGroupIsEqualToSomething() throws Exception {
        // Initialize the database
        deviceRepository.saveAndFlush(device);

        // Get all the deviceList where group equals to DEFAULT_GROUP
        defaultDeviceShouldBeFound("group.equals=" + DEFAULT_GROUP);

        // Get all the deviceList where group equals to UPDATED_GROUP
        defaultDeviceShouldNotBeFound("group.equals=" + UPDATED_GROUP);
    }

    @Test
    @Transactional
    void getAllDevicesByGroupIsInShouldWork() throws Exception {
        // Initialize the database
        deviceRepository.saveAndFlush(device);

        // Get all the deviceList where group in DEFAULT_GROUP or UPDATED_GROUP
        defaultDeviceShouldBeFound("group.in=" + DEFAULT_GROUP + "," + UPDATED_GROUP);

        // Get all the deviceList where group equals to UPDATED_GROUP
        defaultDeviceShouldNotBeFound("group.in=" + UPDATED_GROUP);
    }

    @Test
    @Transactional
    void getAllDevicesByGroupIsNullOrNotNull() throws Exception {
        // Initialize the database
        deviceRepository.saveAndFlush(device);

        // Get all the deviceList where group is not null
        defaultDeviceShouldBeFound("group.specified=true");

        // Get all the deviceList where group is null
        defaultDeviceShouldNotBeFound("group.specified=false");
    }

    @Test
    @Transactional
    void getAllDevicesByGroupContainsSomething() throws Exception {
        // Initialize the database
        deviceRepository.saveAndFlush(device);

        // Get all the deviceList where group contains DEFAULT_GROUP
        defaultDeviceShouldBeFound("group.contains=" + DEFAULT_GROUP);

        // Get all the deviceList where group contains UPDATED_GROUP
        defaultDeviceShouldNotBeFound("group.contains=" + UPDATED_GROUP);
    }

    @Test
    @Transactional
    void getAllDevicesByGroupNotContainsSomething() throws Exception {
        // Initialize the database
        deviceRepository.saveAndFlush(device);

        // Get all the deviceList where group does not contain DEFAULT_GROUP
        defaultDeviceShouldNotBeFound("group.doesNotContain=" + DEFAULT_GROUP);

        // Get all the deviceList where group does not contain UPDATED_GROUP
        defaultDeviceShouldBeFound("group.doesNotContain=" + UPDATED_GROUP);
    }

    @Test
    @Transactional
    void getAllDevicesByTextIsEqualToSomething() throws Exception {
        // Initialize the database
        deviceRepository.saveAndFlush(device);

        // Get all the deviceList where text equals to DEFAULT_TEXT
        defaultDeviceShouldBeFound("text.equals=" + DEFAULT_TEXT);

        // Get all the deviceList where text equals to UPDATED_TEXT
        defaultDeviceShouldNotBeFound("text.equals=" + UPDATED_TEXT);
    }

    @Test
    @Transactional
    void getAllDevicesByTextIsInShouldWork() throws Exception {
        // Initialize the database
        deviceRepository.saveAndFlush(device);

        // Get all the deviceList where text in DEFAULT_TEXT or UPDATED_TEXT
        defaultDeviceShouldBeFound("text.in=" + DEFAULT_TEXT + "," + UPDATED_TEXT);

        // Get all the deviceList where text equals to UPDATED_TEXT
        defaultDeviceShouldNotBeFound("text.in=" + UPDATED_TEXT);
    }

    @Test
    @Transactional
    void getAllDevicesByTextIsNullOrNotNull() throws Exception {
        // Initialize the database
        deviceRepository.saveAndFlush(device);

        // Get all the deviceList where text is not null
        defaultDeviceShouldBeFound("text.specified=true");

        // Get all the deviceList where text is null
        defaultDeviceShouldNotBeFound("text.specified=false");
    }

    @Test
    @Transactional
    void getAllDevicesByTextContainsSomething() throws Exception {
        // Initialize the database
        deviceRepository.saveAndFlush(device);

        // Get all the deviceList where text contains DEFAULT_TEXT
        defaultDeviceShouldBeFound("text.contains=" + DEFAULT_TEXT);

        // Get all the deviceList where text contains UPDATED_TEXT
        defaultDeviceShouldNotBeFound("text.contains=" + UPDATED_TEXT);
    }

    @Test
    @Transactional
    void getAllDevicesByTextNotContainsSomething() throws Exception {
        // Initialize the database
        deviceRepository.saveAndFlush(device);

        // Get all the deviceList where text does not contain DEFAULT_TEXT
        defaultDeviceShouldNotBeFound("text.doesNotContain=" + DEFAULT_TEXT);

        // Get all the deviceList where text does not contain UPDATED_TEXT
        defaultDeviceShouldBeFound("text.doesNotContain=" + UPDATED_TEXT);
    }

    @Test
    @Transactional
    void getAllDevicesByDateIsEqualToSomething() throws Exception {
        // Initialize the database
        deviceRepository.saveAndFlush(device);

        // Get all the deviceList where date equals to DEFAULT_DATE
        defaultDeviceShouldBeFound("date.equals=" + DEFAULT_DATE);

        // Get all the deviceList where date equals to UPDATED_DATE
        defaultDeviceShouldNotBeFound("date.equals=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllDevicesByDateIsInShouldWork() throws Exception {
        // Initialize the database
        deviceRepository.saveAndFlush(device);

        // Get all the deviceList where date in DEFAULT_DATE or UPDATED_DATE
        defaultDeviceShouldBeFound("date.in=" + DEFAULT_DATE + "," + UPDATED_DATE);

        // Get all the deviceList where date equals to UPDATED_DATE
        defaultDeviceShouldNotBeFound("date.in=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllDevicesByDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        deviceRepository.saveAndFlush(device);

        // Get all the deviceList where date is not null
        defaultDeviceShouldBeFound("date.specified=true");

        // Get all the deviceList where date is null
        defaultDeviceShouldNotBeFound("date.specified=false");
    }

    @Test
    @Transactional
    void getAllDevicesByDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        deviceRepository.saveAndFlush(device);

        // Get all the deviceList where date is greater than or equal to DEFAULT_DATE
        defaultDeviceShouldBeFound("date.greaterThanOrEqual=" + DEFAULT_DATE);

        // Get all the deviceList where date is greater than or equal to UPDATED_DATE
        defaultDeviceShouldNotBeFound("date.greaterThanOrEqual=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllDevicesByDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        deviceRepository.saveAndFlush(device);

        // Get all the deviceList where date is less than or equal to DEFAULT_DATE
        defaultDeviceShouldBeFound("date.lessThanOrEqual=" + DEFAULT_DATE);

        // Get all the deviceList where date is less than or equal to SMALLER_DATE
        defaultDeviceShouldNotBeFound("date.lessThanOrEqual=" + SMALLER_DATE);
    }

    @Test
    @Transactional
    void getAllDevicesByDateIsLessThanSomething() throws Exception {
        // Initialize the database
        deviceRepository.saveAndFlush(device);

        // Get all the deviceList where date is less than DEFAULT_DATE
        defaultDeviceShouldNotBeFound("date.lessThan=" + DEFAULT_DATE);

        // Get all the deviceList where date is less than UPDATED_DATE
        defaultDeviceShouldBeFound("date.lessThan=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllDevicesByDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        deviceRepository.saveAndFlush(device);

        // Get all the deviceList where date is greater than DEFAULT_DATE
        defaultDeviceShouldNotBeFound("date.greaterThan=" + DEFAULT_DATE);

        // Get all the deviceList where date is greater than SMALLER_DATE
        defaultDeviceShouldBeFound("date.greaterThan=" + SMALLER_DATE);
    }

    @Test
    @Transactional
    void getAllDevicesByDateTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        deviceRepository.saveAndFlush(device);

        // Get all the deviceList where dateTime equals to DEFAULT_DATE_TIME
        defaultDeviceShouldBeFound("dateTime.equals=" + DEFAULT_DATE_TIME);

        // Get all the deviceList where dateTime equals to UPDATED_DATE_TIME
        defaultDeviceShouldNotBeFound("dateTime.equals=" + UPDATED_DATE_TIME);
    }

    @Test
    @Transactional
    void getAllDevicesByDateTimeIsInShouldWork() throws Exception {
        // Initialize the database
        deviceRepository.saveAndFlush(device);

        // Get all the deviceList where dateTime in DEFAULT_DATE_TIME or UPDATED_DATE_TIME
        defaultDeviceShouldBeFound("dateTime.in=" + DEFAULT_DATE_TIME + "," + UPDATED_DATE_TIME);

        // Get all the deviceList where dateTime equals to UPDATED_DATE_TIME
        defaultDeviceShouldNotBeFound("dateTime.in=" + UPDATED_DATE_TIME);
    }

    @Test
    @Transactional
    void getAllDevicesByDateTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        deviceRepository.saveAndFlush(device);

        // Get all the deviceList where dateTime is not null
        defaultDeviceShouldBeFound("dateTime.specified=true");

        // Get all the deviceList where dateTime is null
        defaultDeviceShouldNotBeFound("dateTime.specified=false");
    }

    @Test
    @Transactional
    void getAllDevicesByDateTimeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        deviceRepository.saveAndFlush(device);

        // Get all the deviceList where dateTime is greater than or equal to DEFAULT_DATE_TIME
        defaultDeviceShouldBeFound("dateTime.greaterThanOrEqual=" + DEFAULT_DATE_TIME);

        // Get all the deviceList where dateTime is greater than or equal to UPDATED_DATE_TIME
        defaultDeviceShouldNotBeFound("dateTime.greaterThanOrEqual=" + UPDATED_DATE_TIME);
    }

    @Test
    @Transactional
    void getAllDevicesByDateTimeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        deviceRepository.saveAndFlush(device);

        // Get all the deviceList where dateTime is less than or equal to DEFAULT_DATE_TIME
        defaultDeviceShouldBeFound("dateTime.lessThanOrEqual=" + DEFAULT_DATE_TIME);

        // Get all the deviceList where dateTime is less than or equal to SMALLER_DATE_TIME
        defaultDeviceShouldNotBeFound("dateTime.lessThanOrEqual=" + SMALLER_DATE_TIME);
    }

    @Test
    @Transactional
    void getAllDevicesByDateTimeIsLessThanSomething() throws Exception {
        // Initialize the database
        deviceRepository.saveAndFlush(device);

        // Get all the deviceList where dateTime is less than DEFAULT_DATE_TIME
        defaultDeviceShouldNotBeFound("dateTime.lessThan=" + DEFAULT_DATE_TIME);

        // Get all the deviceList where dateTime is less than UPDATED_DATE_TIME
        defaultDeviceShouldBeFound("dateTime.lessThan=" + UPDATED_DATE_TIME);
    }

    @Test
    @Transactional
    void getAllDevicesByDateTimeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        deviceRepository.saveAndFlush(device);

        // Get all the deviceList where dateTime is greater than DEFAULT_DATE_TIME
        defaultDeviceShouldNotBeFound("dateTime.greaterThan=" + DEFAULT_DATE_TIME);

        // Get all the deviceList where dateTime is greater than SMALLER_DATE_TIME
        defaultDeviceShouldBeFound("dateTime.greaterThan=" + SMALLER_DATE_TIME);
    }

    @Test
    @Transactional
    void getAllDevicesByCheckIsEqualToSomething() throws Exception {
        // Initialize the database
        deviceRepository.saveAndFlush(device);

        // Get all the deviceList where check equals to DEFAULT_CHECK
        defaultDeviceShouldBeFound("check.equals=" + DEFAULT_CHECK);

        // Get all the deviceList where check equals to UPDATED_CHECK
        defaultDeviceShouldNotBeFound("check.equals=" + UPDATED_CHECK);
    }

    @Test
    @Transactional
    void getAllDevicesByCheckIsInShouldWork() throws Exception {
        // Initialize the database
        deviceRepository.saveAndFlush(device);

        // Get all the deviceList where check in DEFAULT_CHECK or UPDATED_CHECK
        defaultDeviceShouldBeFound("check.in=" + DEFAULT_CHECK + "," + UPDATED_CHECK);

        // Get all the deviceList where check equals to UPDATED_CHECK
        defaultDeviceShouldNotBeFound("check.in=" + UPDATED_CHECK);
    }

    @Test
    @Transactional
    void getAllDevicesByCheckIsNullOrNotNull() throws Exception {
        // Initialize the database
        deviceRepository.saveAndFlush(device);

        // Get all the deviceList where check is not null
        defaultDeviceShouldBeFound("check.specified=true");

        // Get all the deviceList where check is null
        defaultDeviceShouldNotBeFound("check.specified=false");
    }

    @Test
    @Transactional
    void getAllDevicesByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        deviceRepository.saveAndFlush(device);

        // Get all the deviceList where description equals to DEFAULT_DESCRIPTION
        defaultDeviceShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the deviceList where description equals to UPDATED_DESCRIPTION
        defaultDeviceShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllDevicesByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        deviceRepository.saveAndFlush(device);

        // Get all the deviceList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultDeviceShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the deviceList where description equals to UPDATED_DESCRIPTION
        defaultDeviceShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllDevicesByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        deviceRepository.saveAndFlush(device);

        // Get all the deviceList where description is not null
        defaultDeviceShouldBeFound("description.specified=true");

        // Get all the deviceList where description is null
        defaultDeviceShouldNotBeFound("description.specified=false");
    }

    @Test
    @Transactional
    void getAllDevicesByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        deviceRepository.saveAndFlush(device);

        // Get all the deviceList where description contains DEFAULT_DESCRIPTION
        defaultDeviceShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the deviceList where description contains UPDATED_DESCRIPTION
        defaultDeviceShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllDevicesByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        deviceRepository.saveAndFlush(device);

        // Get all the deviceList where description does not contain DEFAULT_DESCRIPTION
        defaultDeviceShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the deviceList where description does not contain UPDATED_DESCRIPTION
        defaultDeviceShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultDeviceShouldBeFound(String filter) throws Exception {
        restDeviceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(device.getId().intValue())))
            .andExpect(jsonPath("$.[*].group").value(hasItem(DEFAULT_GROUP)))
            .andExpect(jsonPath("$.[*].text").value(hasItem(DEFAULT_TEXT)))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].dateTime").value(hasItem(sameInstant(DEFAULT_DATE_TIME))))
            .andExpect(jsonPath("$.[*].check").value(hasItem(DEFAULT_CHECK.booleanValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));

        // Check, that the count call also returns 1
        restDeviceMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultDeviceShouldNotBeFound(String filter) throws Exception {
        restDeviceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restDeviceMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingDevice() throws Exception {
        // Get the device
        restDeviceMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingDevice() throws Exception {
        // Initialize the database
        deviceRepository.saveAndFlush(device);

        int databaseSizeBeforeUpdate = deviceRepository.findAll().size();

        // Update the device
        Device updatedDevice = deviceRepository.findById(device.getId()).get();
        // Disconnect from session so that the updates on updatedDevice are not directly saved in db
        em.detach(updatedDevice);
        updatedDevice
            .group(UPDATED_GROUP)
            .text(UPDATED_TEXT)
            .date(UPDATED_DATE)
            .dateTime(UPDATED_DATE_TIME)
            .check(UPDATED_CHECK)
            .description(UPDATED_DESCRIPTION);
        DeviceDTO deviceDTO = deviceMapper.toDto(updatedDevice);

        restDeviceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, deviceDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(deviceDTO))
            )
            .andExpect(status().isOk());

        // Validate the Device in the database
        List<Device> deviceList = deviceRepository.findAll();
        assertThat(deviceList).hasSize(databaseSizeBeforeUpdate);
        Device testDevice = deviceList.get(deviceList.size() - 1);
        assertThat(testDevice.getGroup()).isEqualTo(UPDATED_GROUP);
        assertThat(testDevice.getText()).isEqualTo(UPDATED_TEXT);
        assertThat(testDevice.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testDevice.getDateTime()).isEqualTo(UPDATED_DATE_TIME);
        assertThat(testDevice.getCheck()).isEqualTo(UPDATED_CHECK);
        assertThat(testDevice.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void putNonExistingDevice() throws Exception {
        int databaseSizeBeforeUpdate = deviceRepository.findAll().size();
        device.setId(count.incrementAndGet());

        // Create the Device
        DeviceDTO deviceDTO = deviceMapper.toDto(device);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDeviceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, deviceDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(deviceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Device in the database
        List<Device> deviceList = deviceRepository.findAll();
        assertThat(deviceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchDevice() throws Exception {
        int databaseSizeBeforeUpdate = deviceRepository.findAll().size();
        device.setId(count.incrementAndGet());

        // Create the Device
        DeviceDTO deviceDTO = deviceMapper.toDto(device);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDeviceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(deviceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Device in the database
        List<Device> deviceList = deviceRepository.findAll();
        assertThat(deviceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDevice() throws Exception {
        int databaseSizeBeforeUpdate = deviceRepository.findAll().size();
        device.setId(count.incrementAndGet());

        // Create the Device
        DeviceDTO deviceDTO = deviceMapper.toDto(device);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDeviceMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(deviceDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Device in the database
        List<Device> deviceList = deviceRepository.findAll();
        assertThat(deviceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateDeviceWithPatch() throws Exception {
        // Initialize the database
        deviceRepository.saveAndFlush(device);

        int databaseSizeBeforeUpdate = deviceRepository.findAll().size();

        // Update the device using partial update
        Device partialUpdatedDevice = new Device();
        partialUpdatedDevice.setId(device.getId());

        partialUpdatedDevice.dateTime(UPDATED_DATE_TIME).check(UPDATED_CHECK).description(UPDATED_DESCRIPTION);

        restDeviceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDevice.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDevice))
            )
            .andExpect(status().isOk());

        // Validate the Device in the database
        List<Device> deviceList = deviceRepository.findAll();
        assertThat(deviceList).hasSize(databaseSizeBeforeUpdate);
        Device testDevice = deviceList.get(deviceList.size() - 1);
        assertThat(testDevice.getGroup()).isEqualTo(DEFAULT_GROUP);
        assertThat(testDevice.getText()).isEqualTo(DEFAULT_TEXT);
        assertThat(testDevice.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testDevice.getDateTime()).isEqualTo(UPDATED_DATE_TIME);
        assertThat(testDevice.getCheck()).isEqualTo(UPDATED_CHECK);
        assertThat(testDevice.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void fullUpdateDeviceWithPatch() throws Exception {
        // Initialize the database
        deviceRepository.saveAndFlush(device);

        int databaseSizeBeforeUpdate = deviceRepository.findAll().size();

        // Update the device using partial update
        Device partialUpdatedDevice = new Device();
        partialUpdatedDevice.setId(device.getId());

        partialUpdatedDevice
            .group(UPDATED_GROUP)
            .text(UPDATED_TEXT)
            .date(UPDATED_DATE)
            .dateTime(UPDATED_DATE_TIME)
            .check(UPDATED_CHECK)
            .description(UPDATED_DESCRIPTION);

        restDeviceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDevice.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDevice))
            )
            .andExpect(status().isOk());

        // Validate the Device in the database
        List<Device> deviceList = deviceRepository.findAll();
        assertThat(deviceList).hasSize(databaseSizeBeforeUpdate);
        Device testDevice = deviceList.get(deviceList.size() - 1);
        assertThat(testDevice.getGroup()).isEqualTo(UPDATED_GROUP);
        assertThat(testDevice.getText()).isEqualTo(UPDATED_TEXT);
        assertThat(testDevice.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testDevice.getDateTime()).isEqualTo(UPDATED_DATE_TIME);
        assertThat(testDevice.getCheck()).isEqualTo(UPDATED_CHECK);
        assertThat(testDevice.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void patchNonExistingDevice() throws Exception {
        int databaseSizeBeforeUpdate = deviceRepository.findAll().size();
        device.setId(count.incrementAndGet());

        // Create the Device
        DeviceDTO deviceDTO = deviceMapper.toDto(device);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDeviceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, deviceDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(deviceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Device in the database
        List<Device> deviceList = deviceRepository.findAll();
        assertThat(deviceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDevice() throws Exception {
        int databaseSizeBeforeUpdate = deviceRepository.findAll().size();
        device.setId(count.incrementAndGet());

        // Create the Device
        DeviceDTO deviceDTO = deviceMapper.toDto(device);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDeviceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(deviceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Device in the database
        List<Device> deviceList = deviceRepository.findAll();
        assertThat(deviceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDevice() throws Exception {
        int databaseSizeBeforeUpdate = deviceRepository.findAll().size();
        device.setId(count.incrementAndGet());

        // Create the Device
        DeviceDTO deviceDTO = deviceMapper.toDto(device);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDeviceMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(deviceDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Device in the database
        List<Device> deviceList = deviceRepository.findAll();
        assertThat(deviceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteDevice() throws Exception {
        // Initialize the database
        deviceRepository.saveAndFlush(device);

        int databaseSizeBeforeDelete = deviceRepository.findAll().size();

        // Delete the device
        restDeviceMockMvc
            .perform(delete(ENTITY_API_URL_ID, device.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Device> deviceList = deviceRepository.findAll();
        assertThat(deviceList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
