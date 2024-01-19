import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IDevice, NewDevice } from '../device.model';

export type PartialUpdateDevice = Partial<IDevice> & Pick<IDevice, 'id'>;

type RestOf<T extends IDevice | NewDevice> = Omit<T, 'date' | 'dateTime'> & {
  date?: string | null;
  dateTime?: string | null;
};

export type RestDevice = RestOf<IDevice>;

export type NewRestDevice = RestOf<NewDevice>;

export type PartialUpdateRestDevice = RestOf<PartialUpdateDevice>;

export type EntityResponseType = HttpResponse<IDevice>;
export type EntityArrayResponseType = HttpResponse<IDevice[]>;

@Injectable({ providedIn: 'root' })
export class DeviceService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/devices');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(device: NewDevice): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(device);
    return this.http
      .post<RestDevice>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(device: IDevice): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(device);
    return this.http
      .put<RestDevice>(`${this.resourceUrl}/${this.getDeviceIdentifier(device)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(device: PartialUpdateDevice): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(device);
    return this.http
      .patch<RestDevice>(`${this.resourceUrl}/${this.getDeviceIdentifier(device)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestDevice>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestDevice[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getDeviceIdentifier(device: Pick<IDevice, 'id'>): number {
    return device.id;
  }

  compareDevice(o1: Pick<IDevice, 'id'> | null, o2: Pick<IDevice, 'id'> | null): boolean {
    return o1 && o2 ? this.getDeviceIdentifier(o1) === this.getDeviceIdentifier(o2) : o1 === o2;
  }

  addDeviceToCollectionIfMissing<Type extends Pick<IDevice, 'id'>>(
    deviceCollection: Type[],
    ...devicesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const devices: Type[] = devicesToCheck.filter(isPresent);
    if (devices.length > 0) {
      const deviceCollectionIdentifiers = deviceCollection.map(deviceItem => this.getDeviceIdentifier(deviceItem)!);
      const devicesToAdd = devices.filter(deviceItem => {
        const deviceIdentifier = this.getDeviceIdentifier(deviceItem);
        if (deviceCollectionIdentifiers.includes(deviceIdentifier)) {
          return false;
        }
        deviceCollectionIdentifiers.push(deviceIdentifier);
        return true;
      });
      return [...devicesToAdd, ...deviceCollection];
    }
    return deviceCollection;
  }

  protected convertDateFromClient<T extends IDevice | NewDevice | PartialUpdateDevice>(device: T): RestOf<T> {
    return {
      ...device,
      date: device.date?.format(DATE_FORMAT) ?? null,
      dateTime: device.dateTime?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restDevice: RestDevice): IDevice {
    return {
      ...restDevice,
      date: restDevice.date ? dayjs(restDevice.date) : undefined,
      dateTime: restDevice.dateTime ? dayjs(restDevice.dateTime) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestDevice>): HttpResponse<IDevice> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestDevice[]>): HttpResponse<IDevice[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
