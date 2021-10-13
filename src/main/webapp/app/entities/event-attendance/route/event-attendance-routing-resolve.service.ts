import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IEventAttendance, EventAttendance } from '../event-attendance.model';
import { EventAttendanceService } from '../service/event-attendance.service';

@Injectable({ providedIn: 'root' })
export class EventAttendanceRoutingResolveService implements Resolve<IEventAttendance> {
  constructor(protected service: EventAttendanceService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IEventAttendance> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((eventAttendance: HttpResponse<EventAttendance>) => {
          if (eventAttendance.body) {
            return of(eventAttendance.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new EventAttendance());
  }
}
