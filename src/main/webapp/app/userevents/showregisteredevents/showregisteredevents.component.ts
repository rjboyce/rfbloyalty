import { Component, OnInit } from '@angular/core';
import { AppEvent, IAppEvent } from 'app/entities/event/event.model';
import { ApplicationUser } from 'app/entities/application-user/application-user.model';
import { ITEMS_PER_PAGE } from 'app/config/pagination.constants';
import { EventAttendanceService } from 'app/entities/event-attendance/service/event-attendance.service';
import { EventService } from 'app/entities/event/service/event.service';
import { ApplicationUserService } from 'app/entities/application-user/service/application-user.service';
import { LocationService } from 'app/entities/location/service/location.service';
import { ActivatedRoute, Router } from '@angular/router';
import { EventAttendance, IEventAttendance } from 'app/entities/event-attendance/event-attendance.model';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { combineLatest, forkJoin, of } from 'rxjs';
import { mergeMap, switchMap } from 'rxjs/operators';

@Component({
  selector: 'jhi-showregisteredevents',
  templateUrl: './showregisteredevents.component.html',
  styleUrls: ['./showregisteredevents.component.scss'],
})
export class ShowregisteredeventsComponent implements OnInit {
  attendances?: EventAttendance[];
  appEvents: AppEvent[] = [];
  locName?: string;
  user?: ApplicationUser;
  signed = false;

  isLoading = false;
  totalItems = 0;
  itemsPerPage = ITEMS_PER_PAGE;
  page?: number;
  predicate!: string;
  ascending!: boolean;
  ngbPaginationPage = 1;

  constructor(
    protected eventAttendanceService: EventAttendanceService,
    protected eventService: EventService,
    protected applicationUserService: ApplicationUserService,
    protected locationService: LocationService,
    protected activatedRoute: ActivatedRoute,
    protected router: Router
  ) {}

  ngOnInit(): void {
    this.handleNavigation();
  }

  returnEventName(eventAttendances: IEventAttendance): string {
    let dis = 'Event Unavailable';

    this.appEvents?.forEach(e => {
      if (e.id === eventAttendances?.eventId) {
        dis = e.eventName as string;
      }
    });
    return dis;
  }

  retrieveLocation(): void {
    this.locationService.find(this.user!.homeLocationId as number).subscribe(locs => (this.locName = locs.body?.locationName));
  }

  loadPage(page?: number, dontNavigate?: boolean): void {
    this.isLoading = true;
    const pageToLoad: number = page ?? this.page ?? 1;
    let response: HttpResponse<IEventAttendance[]>;

    this.eventAttendanceService
      .query({
        page: pageToLoad - 1,
        size: this.itemsPerPage,
        sort: this.sort(),
        user: this.user?.id,
      })
      .pipe(
        switchMap((res: HttpResponse<IEventAttendance[]>) => {
          response = res;
          return res.body as EventAttendance[];
        })
      )
      .pipe(mergeMap(ea => this.eventService.retrieveEvent(ea.eventId as number)))
      .subscribe(
        e => {
          this.appEvents.push(e);
        },
        () => {
          this.isLoading = false;
          this.onError();
        },
        () => {
          this.isLoading = false;
          this.onSuccess(response.body, response.headers, pageToLoad, !dontNavigate);
        }
      );
  }

  protected sort(): string[] {
    const result = [this.predicate + ',' + (this.ascending ? 'asc' : 'desc')];
    if (this.predicate !== 'id') {
      result.push('id');
    }
    return result;
  }

  protected handleNavigation(): void {
    combineLatest([this.activatedRoute.data, this.activatedRoute.queryParamMap])
      .pipe(
        mergeMap(([data, params]) =>
          forkJoin([this.applicationUserService.retrieveUser(params.get('user') as string), of(data), of(params)])
        )
      )
      .subscribe(([u, data, params]) => {
        // pull user
        this.user = u;

        if (params.get('sort') !== null || data['defaultSort'] !== undefined) {
          const sort = (params.get('sort') ?? data['defaultSort']).split(',');
          const page = params.get('page');
          const pageNumber = page !== null ? +page : 1;
          const predicate = sort[0];
          const ascending = sort[1] === 'asc';

          if (pageNumber !== this.page || predicate !== this.predicate || ascending !== this.ascending) {
            this.predicate = predicate;
            this.ascending = ascending;
            this.loadPage(pageNumber, true);
          }
        } else {
          this.predicate = 'id';
          this.ascending = true;

          this.loadPage(1, false);
        }
      });
  }

  protected onSuccess(data: IAppEvent[] | null, headers: HttpHeaders, page: number, navigate: boolean): void {
    this.totalItems = Number(headers.get('X-Total-Count'));
    this.page = page;
    if (navigate) {
      this.router.navigate(['/userevents', 'showregisteredevents'], {
        queryParams: {
          page: this.page,
          size: this.itemsPerPage,
          sort: this.predicate + ',' + (this.ascending ? 'asc' : 'desc'),
          user: this.user?.id,
        },
      });
    }
    this.attendances = data ?? [];

    this.ngbPaginationPage = this.page;
  }

  protected onError(): void {
    this.ngbPaginationPage = this.page ?? 1;
  }
}
