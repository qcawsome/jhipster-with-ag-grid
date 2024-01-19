import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { DATE_FORMAT } from 'app/config/input.constants';
import { IDevice } from '../device.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../device.test-samples';

import { DeviceService, RestDevice } from './device.service';

const requireRestSample: RestDevice = {
  ...sampleWithRequiredData,
  date: sampleWithRequiredData.date?.format(DATE_FORMAT),
  dateTime: sampleWithRequiredData.dateTime?.toJSON(),
};

describe('Device Service', () => {
  let service: DeviceService;
  let httpMock: HttpTestingController;
  let expectedResult: IDevice | IDevice[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(DeviceService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a Device', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const device = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(device).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Device', () => {
      const device = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(device).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Device', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Device', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Device', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addDeviceToCollectionIfMissing', () => {
      it('should add a Device to an empty array', () => {
        const device: IDevice = sampleWithRequiredData;
        expectedResult = service.addDeviceToCollectionIfMissing([], device);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(device);
      });

      it('should not add a Device to an array that contains it', () => {
        const device: IDevice = sampleWithRequiredData;
        const deviceCollection: IDevice[] = [
          {
            ...device,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addDeviceToCollectionIfMissing(deviceCollection, device);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Device to an array that doesn't contain it", () => {
        const device: IDevice = sampleWithRequiredData;
        const deviceCollection: IDevice[] = [sampleWithPartialData];
        expectedResult = service.addDeviceToCollectionIfMissing(deviceCollection, device);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(device);
      });

      it('should add only unique Device to an array', () => {
        const deviceArray: IDevice[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const deviceCollection: IDevice[] = [sampleWithRequiredData];
        expectedResult = service.addDeviceToCollectionIfMissing(deviceCollection, ...deviceArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const device: IDevice = sampleWithRequiredData;
        const device2: IDevice = sampleWithPartialData;
        expectedResult = service.addDeviceToCollectionIfMissing([], device, device2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(device);
        expect(expectedResult).toContain(device2);
      });

      it('should accept null and undefined values', () => {
        const device: IDevice = sampleWithRequiredData;
        expectedResult = service.addDeviceToCollectionIfMissing([], null, device, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(device);
      });

      it('should return initial array if no Device is added', () => {
        const deviceCollection: IDevice[] = [sampleWithRequiredData];
        expectedResult = service.addDeviceToCollectionIfMissing(deviceCollection, undefined, null);
        expect(expectedResult).toEqual(deviceCollection);
      });
    });

    describe('compareDevice', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareDevice(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareDevice(entity1, entity2);
        const compareResult2 = service.compareDevice(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareDevice(entity1, entity2);
        const compareResult2 = service.compareDevice(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareDevice(entity1, entity2);
        const compareResult2 = service.compareDevice(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
