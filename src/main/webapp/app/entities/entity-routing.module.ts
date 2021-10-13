import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
      {
        path: 'location',
        data: { pageTitle: 'volunteerKindApp.location.home.title' },
        loadChildren: () => import('./location/location.module').then(m => m.LocationModule),
      },
      {
        path: 'event',
        data: { pageTitle: 'volunteerKindApp.event.home.title' },
        loadChildren: () => import('./event/event.module').then(m => m.EventModule),
      },
      {
        path: 'event-attendance',
        data: { pageTitle: 'volunteerKindApp.eventAttendance.home.title' },
        loadChildren: () => import('./event-attendance/event-attendance.module').then(m => m.EventAttendanceModule),
      },
      {
        path: 'application-user',
        data: { pageTitle: 'volunteerKindApp.applicationUser.home.title' },
        loadChildren: () => import('./application-user/application-user.module').then(m => m.ApplicationUserModule),
      },
    ]),
  ],
})
export class EntityRoutingModule {}
