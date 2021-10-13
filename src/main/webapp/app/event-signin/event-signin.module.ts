import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { EventSignInRoutingModule } from 'app/event-signin/route/event-signin-routing.module';
import { ShowDayEventsComponent } from 'app/event-signin/show-day-events/show-day-events.component';
import { VolunteerSigninComponent } from 'app/event-signin/volunteer-signin/volunteer-signin.component';

@NgModule({
  imports: [SharedModule, EventSignInRoutingModule],
  declarations: [ShowDayEventsComponent, VolunteerSigninComponent],
})
export class EventSignInModule {}
