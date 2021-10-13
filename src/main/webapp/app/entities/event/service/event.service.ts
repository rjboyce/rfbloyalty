import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpResponse } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map, take } from 'rxjs/operators';
import * as dayjs from 'dayjs';
import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IAppEvent, getEventIdentifier, AppEvent } from '../event.model';
import { Valid } from 'app/entities/valid.model';

export type EntityResponseType = HttpResponse<IAppEvent>;
export type EntityArrayResponseType = HttpResponse<IAppEvent[]>;

@Injectable({ providedIn: 'root' })
export class EventService {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/events');

  constructor(protected http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  create(event: IAppEvent): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(event);
    return this.http
      .post<IAppEvent>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(event: IAppEvent): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(event);
    return this.http
      .put<IAppEvent>(`${this.resourceUrl}/${getEventIdentifier(event) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IAppEvent>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IAppEvent[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  retrieveEvent(eventId: number): Observable<AppEvent> {
    if (eventId !== null && eventId !== undefined) {
      return this.find(eventId).pipe(
        take(1),
        map(m => m.body as AppEvent)
      );
    }

    return of(new AppEvent());
  }

  findEventNameLike(match: string): Observable<EntityArrayResponseType> {
    let params = new HttpParams();
    params = params.append('match', match);
    return this.http.get<IAppEvent[]>(this.resourceUrl, { params, observe: 'response' });
  }

  searchEntry(match: string, eventInvalid: Valid, currentEvent: AppEvent): Observable<AppEvent[]> {
    // passed by ref
    eventInvalid.value = true;

    if (match !== undefined && match !== null) {
      if (match.length > 0) {
        return this.findEventNameLike(match).pipe(
          take(1),
          map((s: HttpResponse<IAppEvent[]>) => {
            // formulate new match list and check if there is a complete match
            // users.length = 0;
            const events = s.body ?? [];

            const id = this.checkValidEvent(match, events, currentEvent);

            if (id !== undefined) {
              // pass value back to component as false if complete match found (passed by ref)
              eventInvalid.value = false;
            }

            // return new match list back as observable since the entire object needs to be reassigned(can't pass by ref)
            return events;
          })
        );
      }
    }

    // empty list to be passed back as observable if problem with search criteria
    return of([]);
  }

  checkValidEvent(content: string | undefined, events: AppEvent[], currentEvent: AppEvent | null): number | undefined {
    // ensure validity of event name and convert back to id
    let id = undefined;

    content = content !== undefined && content !== null ? content.trim() : undefined;

    if (content !== undefined && currentEvent !== null) {
      // if value matches event search item, assign that id
      const findevent = events.find(l => l.eventName === content);
      if (findevent !== undefined) {
        id = findevent.id;
        if (currentEvent !== null) {
          this.passObject(currentEvent, findevent);
        }
      }
    }

    return id;
  }

  passObject(eventOne: AppEvent, eventTwo: AppEvent): void {
    eventOne.eventDate = eventTwo.eventDate;
    eventOne.eventName = eventTwo.eventName;
    eventOne.id = eventTwo.id;
    eventOne.projection = eventTwo.projection;
    eventOne.locationId = eventTwo.locationId;
    eventOne.startTime = eventTwo.startTime;
    eventOne.venue = eventTwo.venue;
  }

  addEventToCollectionIfMissing(eventCollection: number[], ...eventsToCheck: (number | null | undefined)[]): number[] {
    const events: number[] = eventsToCheck.filter(isPresent);
    if (events.length > 0) {
      const eventsToAdd = events.filter(eventItem => {
        if (eventItem == null || eventCollection.includes(eventItem)) {
          return false;
        }
        eventCollection.push(eventItem);
        return true;
      });
      return [...eventsToAdd, ...eventCollection];
    }
    return eventCollection;
  }

  protected convertDateFromClient(event: IAppEvent): IAppEvent {
    return Object.assign({}, event, {
      eventDate: event.eventDate?.isValid() ? event.eventDate.format(DATE_FORMAT) : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.eventDate = res.body.eventDate ? dayjs(res.body.eventDate) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((event: IAppEvent) => {
        event.eventDate = event.eventDate ? dayjs(event.eventDate) : undefined;
      });
    }
    return res;
  }
}
