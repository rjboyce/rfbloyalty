import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IEventAttendance, getEventAttendanceIdentifier } from '../event-attendance.model';
import { map, take } from 'rxjs/operators';

export type EntityResponseType = HttpResponse<IEventAttendance>;
export type EntityArrayResponseType = HttpResponse<IEventAttendance[]>;

@Injectable({ providedIn: 'root' })
export class EventAttendanceService {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/event-attendances');

  constructor(protected http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  create(eventAttendance: IEventAttendance): Observable<EntityResponseType> {
    return this.http.post<IEventAttendance>(this.resourceUrl, eventAttendance, { observe: 'response' });
  }

  update(eventAttendance: IEventAttendance): Observable<EntityResponseType> {
    return this.http.put<IEventAttendance>(
      `${this.resourceUrl}/${getEventAttendanceIdentifier(eventAttendance) as number}`,
      eventAttendance,
      {
        observe: 'response',
      }
    );
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IEventAttendance>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IEventAttendance[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  singlequery(req?: any): Observable<EntityResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IEventAttendance>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  findAttendanceCountByEventAndUsercode(event: number): Observable<number> {
    let params = new HttpParams();
    params = params.append('event', String(event));
    return this.http.get<number>(this.resourceUrl, { params, observe: 'response' }).pipe(
      take(1),
      map(x => x.body as number)
    );
  }

  addAppEventAttendanceToCollectionIfMissing(
    appEventAttendanceCollection: IEventAttendance[],
    ...appEventAttendancesToCheck: (IEventAttendance | null | undefined)[]
  ): IEventAttendance[] {
    const appEventAttendances: IEventAttendance[] = appEventAttendancesToCheck.filter(isPresent);
    if (appEventAttendances.length > 0) {
      const appEventAttendanceCollectionIdentifiers = appEventAttendanceCollection.map(
        appEventAttendanceItem => getEventAttendanceIdentifier(appEventAttendanceItem)!
      );
      const appEventAttendancesToAdd = appEventAttendances.filter(appEventAttendanceItem => {
        const appEventAttendanceIdentifier = getEventAttendanceIdentifier(appEventAttendanceItem);
        if (appEventAttendanceIdentifier == null || appEventAttendanceCollectionIdentifiers.includes(appEventAttendanceIdentifier)) {
          return false;
        }
        appEventAttendanceCollectionIdentifiers.push(appEventAttendanceIdentifier);
        return true;
      });
      return [...appEventAttendancesToAdd, ...appEventAttendanceCollection];
    }
    return appEventAttendanceCollection;
  }
}
