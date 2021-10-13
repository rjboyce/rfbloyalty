import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IMyLocation, MyLocation } from '../location.model';
import { LocationService } from '../service/location.service';

@Injectable({ providedIn: 'root' })
export class LocationRoutingResolveService implements Resolve<IMyLocation> {
  constructor(protected service: LocationService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IMyLocation> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((location: HttpResponse<MyLocation>) => {
          if (location.body) {
            return of(location.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new MyLocation());
  }
}
