package com.qc.aggrid.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.qc.aggrid.domain.Device} entity. This class is used
 * in {@link com.qc.aggrid.web.rest.DeviceResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /devices?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DeviceCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter group;

    private StringFilter text;

    private LocalDateFilter date;

    private ZonedDateTimeFilter dateTime;

    private BooleanFilter check;

    private StringFilter description;

    private Boolean distinct;

    public DeviceCriteria() {}

    public DeviceCriteria(DeviceCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.group = other.group == null ? null : other.group.copy();
        this.text = other.text == null ? null : other.text.copy();
        this.date = other.date == null ? null : other.date.copy();
        this.dateTime = other.dateTime == null ? null : other.dateTime.copy();
        this.check = other.check == null ? null : other.check.copy();
        this.description = other.description == null ? null : other.description.copy();
        this.distinct = other.distinct;
    }

    @Override
    public DeviceCriteria copy() {
        return new DeviceCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getGroup() {
        return group;
    }

    public StringFilter group() {
        if (group == null) {
            group = new StringFilter();
        }
        return group;
    }

    public void setGroup(StringFilter group) {
        this.group = group;
    }

    public StringFilter getText() {
        return text;
    }

    public StringFilter text() {
        if (text == null) {
            text = new StringFilter();
        }
        return text;
    }

    public void setText(StringFilter text) {
        this.text = text;
    }

    public LocalDateFilter getDate() {
        return date;
    }

    public LocalDateFilter date() {
        if (date == null) {
            date = new LocalDateFilter();
        }
        return date;
    }

    public void setDate(LocalDateFilter date) {
        this.date = date;
    }

    public ZonedDateTimeFilter getDateTime() {
        return dateTime;
    }

    public ZonedDateTimeFilter dateTime() {
        if (dateTime == null) {
            dateTime = new ZonedDateTimeFilter();
        }
        return dateTime;
    }

    public void setDateTime(ZonedDateTimeFilter dateTime) {
        this.dateTime = dateTime;
    }

    public BooleanFilter getCheck() {
        return check;
    }

    public BooleanFilter check() {
        if (check == null) {
            check = new BooleanFilter();
        }
        return check;
    }

    public void setCheck(BooleanFilter check) {
        this.check = check;
    }

    public StringFilter getDescription() {
        return description;
    }

    public StringFilter description() {
        if (description == null) {
            description = new StringFilter();
        }
        return description;
    }

    public void setDescription(StringFilter description) {
        this.description = description;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final DeviceCriteria that = (DeviceCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(group, that.group) &&
            Objects.equals(text, that.text) &&
            Objects.equals(date, that.date) &&
            Objects.equals(dateTime, that.dateTime) &&
            Objects.equals(check, that.check) &&
            Objects.equals(description, that.description) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, group, text, date, dateTime, check, description, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DeviceCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (group != null ? "group=" + group + ", " : "") +
            (text != null ? "text=" + text + ", " : "") +
            (date != null ? "date=" + date + ", " : "") +
            (dateTime != null ? "dateTime=" + dateTime + ", " : "") +
            (check != null ? "check=" + check + ", " : "") +
            (description != null ? "description=" + description + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
