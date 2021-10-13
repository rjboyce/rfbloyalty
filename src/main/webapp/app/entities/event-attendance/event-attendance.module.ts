import { NgModule } from '@angular/core';

import { SharedModule } from 'app/shared/shared.module';
import { EventAttendanceComponent } from './list/event-attendance.component';
import { EventAttendanceDetailComponent } from './detail/event-attendance-detail.component';
import { EventAttendanceUpdateComponent } from './update/event-attendance-update.component';
import { EventAttendanceDeleteDialogComponent } from './delete/event-attendance-delete-dialog.component';
import { EventAttendanceRoutingModule } from './route/event-attendance-routing.module';
import { NgxMaterialTimepickerModule } from 'ngx-material-timepicker';

@NgModule({
  imports: [SharedModule, EventAttendanceRoutingModule, NgxMaterialTimepickerModule],
  declarations: [
    EventAttendanceComponent,
    EventAttendanceDetailComponent,
    EventAttendanceUpdateComponent,
    EventAttendanceDeleteDialogComponent,
  ],
  entryComponents: [EventAttendanceDeleteDialogComponent],
})
export class EventAttendanceModule {}
