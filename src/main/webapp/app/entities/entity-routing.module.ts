import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'rfb-location',
        data: { pageTitle: 'rfbloyaltyApp.rfbLocation.home.title' },
        loadChildren: () => import('./rfb-location/rfb-location.module').then(m => m.RfbLocationModule),
      },
      {
        path: 'rfb-event',
        data: { pageTitle: 'rfbloyaltyApp.rfbEvent.home.title' },
        loadChildren: () => import('./rfb-event/rfb-event.module').then(m => m.RfbEventModule),
      },
      {
        path: 'rfb-event-attendance',
        data: { pageTitle: 'rfbloyaltyApp.rfbEventAttendance.home.title' },
        loadChildren: () => import('./rfb-event-attendance/rfb-event-attendance.module').then(m => m.RfbEventAttendanceModule),
      },
      {
        path: 'application-user',
        data: { pageTitle: 'rfbloyaltyApp.applicationUser.home.title' },
        loadChildren: () => import('./application-user/application-user.module').then(m => m.ApplicationUserModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
