import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { DeviceComponent } from './list/device.component';
import { DeviceDetailComponent } from './detail/device-detail.component';
import { DeviceUpdateComponent } from './update/device-update.component';
import { DeviceDeleteDialogComponent } from './delete/device-delete-dialog.component';
import { DeviceRoutingModule } from './route/device-routing.module';

import { AgGridAngular, AgGridModule } from 'ag-grid-angular';

@NgModule({
  imports: [SharedModule, DeviceRoutingModule, AgGridAngular, AgGridModule],
  declarations: [DeviceComponent, DeviceDetailComponent, DeviceUpdateComponent, DeviceDeleteDialogComponent],
})
export class DeviceModule {}
