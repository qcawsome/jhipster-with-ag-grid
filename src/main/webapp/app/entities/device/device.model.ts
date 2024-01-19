import dayjs from 'dayjs/esm';

export interface IDevice {
  id: number;
  group?: string | null;
  text?: string | null;
  date?: dayjs.Dayjs | null;
  dateTime?: dayjs.Dayjs | null;
  check?: boolean | null;
  description?: string | null;
}

export type NewDevice = Omit<IDevice, 'id'> & { id: null };
