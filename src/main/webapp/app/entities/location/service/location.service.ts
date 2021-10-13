import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse, HttpParams } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IMyLocation, getLocationIdentifier, MyLocation } from '../location.model';
import { map, take } from 'rxjs/operators';
import { Valid } from 'app/entities/valid.model';
export type EntityResponseType = HttpResponse<IMyLocation>;
export type EntityArrayResponseType = HttpResponse<IMyLocation[]>;

@Injectable({ providedIn: 'root' })
export class LocationService {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/locations');

  constructor(protected http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  create(location: IMyLocation): Observable<EntityResponseType> {
    return this.http.post<IMyLocation>(this.resourceUrl, location, { observe: 'response' });
  }

  update(location: IMyLocation): Observable<EntityResponseType> {
    return this.http.put<IMyLocation>(`${this.resourceUrl}/${getLocationIdentifier(location) as number}`, location, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IMyLocation>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  findNameLike(match: string): Observable<EntityArrayResponseType> {
    let params = new HttpParams();
    params = params.append('match', match);
    return this.http.get<IMyLocation[]>(this.resourceUrl, { params, observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IMyLocation[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  returnLocation(id: number | null | undefined): Observable<MyLocation> {
    if (id !== null && id !== undefined) {
      return this.find(id).pipe(
        take(1),
        map(l => l.body as MyLocation)
      );
    }

    return of();
  }

  searchEntry(match: string, locationInvalid: Valid, currentLocation: MyLocation | null): Observable<MyLocation[]> {
    // passed by ref
    locationInvalid.value = true;

    if (match !== undefined && match !== null) {
      if (match.length > 0) {
        return this.findNameLike(match).pipe(
          take(1),
          map((s: HttpResponse<IMyLocation[]>) => {
            // formulate new match list and check if there is a complete match
            // users.length = 0;
            const locations = s.body ?? [];

            const id = this.checkValidLocation(match, locations, currentLocation);

            if (id !== undefined) {
              // pass value back to component as false if complete match found (passed by ref)
              locationInvalid.value = false;
            }

            // return new match list back as observable since the entire object needs to be reassigned(can't pass by ref)
            return locations;
          })
        );
      }
    }

    // empty list to be passed back as observable if problem with search criteria
    return of([]);
  }

  checkValidLocation(content: string | undefined, locations: MyLocation[], currentLocation: MyLocation | null): number | undefined {
    // ensure validity of event name and convert back to id
    let id = undefined;

    content = content !== undefined && content !== null ? content.trim() : undefined;

    if (content !== undefined && currentLocation !== null) {
      // if value matches event search item, assign that id
      const findlocation = locations.find(l => l.locationName === content);
      if (findlocation !== undefined) {
        id = findlocation.id;
        if (currentLocation !== null) {
          currentLocation.id = findlocation.id;
          currentLocation.locationName = findlocation.locationName;
        }
      }
    }

    return id;
  }

  addLocationToCollectionIfMissing(locationCollection: number[], ...locationsToCheck: (number | null | undefined)[]): number[] {
    const locations: number[] = locationsToCheck.filter(isPresent);
    if (locations.length > 0) {
      const locationsToAdd = locations.filter(locationItem => {
        if (locationItem == null || locationCollection.includes(locationItem)) {
          return false;
        }
        locationCollection.push(locationItem);
        return true;
      });
      return [...locationsToAdd, ...locationCollection];
    }
    return locationCollection;
  }
}
