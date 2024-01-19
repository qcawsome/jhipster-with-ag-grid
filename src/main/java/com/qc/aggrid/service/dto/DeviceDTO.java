package com.qc.aggrid.service.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A DTO for the {@link com.qc.aggrid.domain.Device} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DeviceDTO implements Serializable {

    private Long id;

    private String group;

    private String text;

    private LocalDate date;

    private ZonedDateTime dateTime;

    private Boolean check;

    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public ZonedDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(ZonedDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public Boolean getCheck() {
        return check;
    }

    public void setCheck(Boolean check) {
        this.check = check;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DeviceDTO)) {
            return false;
        }

        DeviceDTO deviceDTO = (DeviceDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, deviceDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DeviceDTO{" +
            "id=" + getId() +
            ", group='" + getGroup() + "'" +
            ", text='" + getText() + "'" +
            ", date='" + getDate() + "'" +
            ", dateTime='" + getDateTime() + "'" +
            ", check='" + getCheck() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
