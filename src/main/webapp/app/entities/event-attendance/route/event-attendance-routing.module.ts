import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { EventAttendanceComponent } from '../list/event-attendance.component';
import { EventAttendanceDetailComponent } from '../detail/event-attendance-detail.component';
import { EventAttendanceUpdateComponent } from '../update/event-attendance-update.component';
import { EventAttendanceRoutingResolveService } from './event-attendance-routing-resolve.service';

const eventAttendanceRoute: Routes = [
  {
    path: '',
    component: EventAttendanceComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: EventAttendanceDetailComponent,
    resolve: {
      eventAttendance: EventAttendanceRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: EventAttendanceUpdateComponent,
    resolve: {
      eventAttendance: EventAttendanceRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: EventAttendanceUpdateComponent,
    resolve: {
      eventAttendance: EventAttendanceRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(eventAttendanceRoute)],
  exports: [RouterModule],
})
export class EventAttendanceRoutingModule {}
