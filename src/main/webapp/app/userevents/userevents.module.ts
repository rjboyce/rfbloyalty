import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { UsereventsRoutingModule } from 'app/userevents/route/userevents-routing.module';
import { SuggestedeventsComponent } from 'app/userevents/suggestedevents/suggestedevents.component';
import { ShowregisteredeventsComponent } from 'app/userevents/showregisteredevents/showregisteredevents.component';

@NgModule({
  imports: [SharedModule, UsereventsRoutingModule],
  declarations: [SuggestedeventsComponent, ShowregisteredeventsComponent],
})
export class UserEventsModule {}
