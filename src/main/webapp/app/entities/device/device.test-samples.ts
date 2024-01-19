import dayjs from 'dayjs/esm';

import { IDevice, NewDevice } from './device.model';

export const sampleWithRequiredData: IDevice = {
  id: 67542,
};

export const sampleWithPartialData: IDevice = {
  id: 50372,
  text: 'AGP',
  date: dayjs('2024-01-18'),
  dateTime: dayjs('2024-01-18T15:59'),
  description: 'Grocery',
};

export const sampleWithFullData: IDevice = {
  id: 58208,
  group: 'Toys disintermediate',
  text: 'Frozen AI',
  date: dayjs('2024-01-18'),
  dateTime: dayjs('2024-01-19T09:29'),
  check: true,
  description: 'Ergonomic',
};

export const sampleWithNewData: NewDevice = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
