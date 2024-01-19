import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IDevice, NewDevice } from '../device.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IDevice for edit and NewDeviceFormGroupInput for create.
 */
type DeviceFormGroupInput = IDevice | PartialWithRequiredKeyOf<NewDevice>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IDevice | NewDevice> = Omit<T, 'dateTime'> & {
  dateTime?: string | null;
};

type DeviceFormRawValue = FormValueOf<IDevice>;

type NewDeviceFormRawValue = FormValueOf<NewDevice>;

type DeviceFormDefaults = Pick<NewDevice, 'id' | 'dateTime' | 'check'>;

type DeviceFormGroupContent = {
  id: FormControl<DeviceFormRawValue['id'] | NewDevice['id']>;
  group: FormControl<DeviceFormRawValue['group']>;
  text: FormControl<DeviceFormRawValue['text']>;
  date: FormControl<DeviceFormRawValue['date']>;
  dateTime: FormControl<DeviceFormRawValue['dateTime']>;
  check: FormControl<DeviceFormRawValue['check']>;
  description: FormControl<DeviceFormRawValue['description']>;
};

export type DeviceFormGroup = FormGroup<DeviceFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class DeviceFormService {
  createDeviceFormGroup(device: DeviceFormGroupInput = { id: null }): DeviceFormGroup {
    const deviceRawValue = this.convertDeviceToDeviceRawValue({
      ...this.getFormDefaults(),
      ...device,
    });
    return new FormGroup<DeviceFormGroupContent>({
      id: new FormControl(
        { value: deviceRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      group: new FormControl(deviceRawValue.group),
      text: new FormControl(deviceRawValue.text),
      date: new FormControl(deviceRawValue.date),
      dateTime: new FormControl(deviceRawValue.dateTime),
      check: new FormControl(deviceRawValue.check),
      description: new FormControl(deviceRawValue.description),
    });
  }

  getDevice(form: DeviceFormGroup): IDevice | NewDevice {
    return this.convertDeviceRawValueToDevice(form.getRawValue() as DeviceFormRawValue | NewDeviceFormRawValue);
  }

  resetForm(form: DeviceFormGroup, device: DeviceFormGroupInput): void {
    const deviceRawValue = this.convertDeviceToDeviceRawValue({ ...this.getFormDefaults(), ...device });
    form.reset(
      {
        ...deviceRawValue,
        id: { value: deviceRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): DeviceFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      dateTime: currentTime,
      check: false,
    };
  }

  private convertDeviceRawValueToDevice(rawDevice: DeviceFormRawValue | NewDeviceFormRawValue): IDevice | NewDevice {
    return {
      ...rawDevice,
      dateTime: dayjs(rawDevice.dateTime, DATE_TIME_FORMAT),
    };
  }

  private convertDeviceToDeviceRawValue(
    device: IDevice | (Partial<NewDevice> & DeviceFormDefaults)
  ): DeviceFormRawValue | PartialWithRequiredKeyOf<NewDeviceFormRawValue> {
    return {
      ...device,
      dateTime: device.dateTime ? device.dateTime.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
