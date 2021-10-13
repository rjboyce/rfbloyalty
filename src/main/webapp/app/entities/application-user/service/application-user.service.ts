import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpResponse } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IApplicationUser, getApplicationUserIdentifier, ApplicationUser } from '../application-user.model';
import { IAuthority } from 'app/entities/application-user/authority.model';
import { map, take } from 'rxjs/operators';
import { Valid } from 'app/entities/valid.model';

export type EntityResponseType = HttpResponse<IApplicationUser>;
export type EntityArrayResponseType = HttpResponse<IApplicationUser[]>;

@Injectable({ providedIn: 'root' })
export class ApplicationUserService {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/application-users');

  constructor(protected http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  create(applicationUser: IApplicationUser): Observable<EntityResponseType> {
    return this.http.post<IApplicationUser>(this.resourceUrl, applicationUser, { observe: 'response' });
  }

  update(applicationUser: IApplicationUser): Observable<EntityResponseType> {
    return this.http.put<IApplicationUser>(`${this.resourceUrl}/${getApplicationUserIdentifier(applicationUser)}`, applicationUser, {
      observe: 'response',
    });
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http.get<IApplicationUser>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  findLoginLike(match: string): Observable<EntityArrayResponseType> {
    let params = new HttpParams();
    params = params.append('match', match);
    return this.http.get<IApplicationUser[]>(this.resourceUrl, { params, observe: 'response' });
  }

  findByLogin(login: string): Observable<ApplicationUser> {
    let params = new HttpParams();
    params = params.append('login', login);
    return this.http.get<IApplicationUser>(this.resourceUrl, { params, observe: 'response' }).pipe(
      take(1),
      map(x => x.body as ApplicationUser)
    );
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IApplicationUser[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  getAuthorities(): Observable<HttpResponse<IAuthority[]>> {
    return this.http.get<IAuthority[]>('api/auths', { observe: 'response' });
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  retrieveUser(userId: string): Observable<ApplicationUser> {
    if (userId !== null && userId !== undefined) {
      return this.find(userId).pipe(
        take(1),
        map(m => m.body as ApplicationUser)
      );
    }

    return of(new ApplicationUser());
  }

  searchEntry(match: string, userInvalid: Valid): Observable<ApplicationUser[]> {
    // passed by ref
    userInvalid.value = true;

    if (match !== undefined && match !== null) {
      if (match.length > 0) {
        return this.findLoginLike(match).pipe(
          take(1),
          map((s: HttpResponse<IApplicationUser[]>) => {
            // formulate new match list and check if there is a complete match
            // users.length = 0;
            const users = s.body ?? [];

            if (this.checkValidUser(match, users, null) !== undefined) {
              // pass value back to component as false if complete match found (passed by ref)
              userInvalid.value = false;
            }

            // return new match list back as observable since the entire object needs to be reassigned(can't pass by ref)
            return users;
          })
        );
      }
    }

    // empty list to be passed back as observable if problem with search criteria
    return of([]);
  }

  checkValidUser(content: string | undefined, users: ApplicationUser[], currentUser: ApplicationUser | null): string | undefined {
    // ensure validity of user name and convert back to id
    let id = undefined;

    content = content !== undefined && content != null ? content.trim() : undefined;

    if (content !== undefined && content !== currentUser?.login) {
      if (content.length > 0) {
        users?.forEach(l => {
          // if value matches user search item, assign that id
          if (l.login === content) {
            id = l.id;
            return;
          }
        });
      }
    }

    return id;
  }

  addApplicationUserToCollectionIfMissing(
    applicationUserCollection: string[],
    ...applicationUsersToCheck: (string | null | undefined)[]
  ): string[] {
    const applicationUsers: string[] = applicationUsersToCheck.filter(isPresent);
    if (applicationUsers.length > 0) {
      const applicationUsersToAdd = applicationUsers.filter(applicationUserItem => {
        if (applicationUserItem == null || applicationUsers.includes(applicationUserItem)) {
          return false;
        }
        applicationUsers.push(applicationUserItem);
        return true;
      });
      return [...applicationUsersToAdd, ...applicationUserCollection];
    }
    return applicationUserCollection;
  }
}
