package com.qc.aggrid.domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Device.
 */
@Entity
@Table(name = "device")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Device implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "jhi_group")
    private String group;

    @Column(name = "text")
    private String text;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "date_time")
    private ZonedDateTime dateTime;

    @Column(name = "jhi_check")
    private Boolean check;

    @Column(name = "description")
    private String description;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Device id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGroup() {
        return this.group;
    }

    public Device group(String group) {
        this.setGroup(group);
        return this;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getText() {
        return this.text;
    }

    public Device text(String text) {
        this.setText(text);
        return this;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDate getDate() {
        return this.date;
    }

    public Device date(LocalDate date) {
        this.setDate(date);
        return this;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public ZonedDateTime getDateTime() {
        return this.dateTime;
    }

    public Device dateTime(ZonedDateTime dateTime) {
        this.setDateTime(dateTime);
        return this;
    }

    public void setDateTime(ZonedDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public Boolean getCheck() {
        return this.check;
    }

    public Device check(Boolean check) {
        this.setCheck(check);
        return this;
    }

    public void setCheck(Boolean check) {
        this.check = check;
    }

    public String getDescription() {
        return this.description;
    }

    public Device description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Device)) {
            return false;
        }
        return id != null && id.equals(((Device) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Device{" +
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
