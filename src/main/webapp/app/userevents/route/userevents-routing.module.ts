import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { SuggestedeventsComponent } from 'app/userevents/suggestedevents/suggestedevents.component';
import { ShowregisteredeventsComponent } from 'app/userevents/showregisteredevents/showregisteredevents.component';

const eventSignInRoute: Routes = [
  {
    path: 'suggestevents',
    component: SuggestedeventsComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'showregisteredevents',
    component: ShowregisteredeventsComponent,
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(eventSignInRoute)],
  exports: [RouterModule],
})
export class UsereventsRoutingModule {}
