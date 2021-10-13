import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { ShowDayEventsComponent } from '../show-day-events/show-day-events.component';
import { VolunteerSigninComponent } from '../volunteer-signin/volunteer-signin.component';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

const eventSignInRoute: Routes = [
  {
    path: 'showevents',
    component: ShowDayEventsComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'sign',
    component: VolunteerSigninComponent,
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(eventSignInRoute)],
  exports: [RouterModule],
})
export class EventSignInRoutingModule {}
