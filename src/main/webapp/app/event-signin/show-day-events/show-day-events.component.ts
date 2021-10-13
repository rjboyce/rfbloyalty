import { Component, OnInit } from '@angular/core';
import { AppEvent, IAppEvent } from 'app/entities/event/event.model';
import { MyLocation } from 'app/entities/location/location.model';
import { ITEMS_PER_PAGE } from 'app/config/pagination.constants';
import { EventAttendanceService } from 'app/entities/event-attendance/service/event-attendance.service';
import { EventService } from 'app/entities/event/service/event.service';
import { ApplicationUserService } from 'app/entities/application-user/service/application-user.service';
import { LocationService } from 'app/entities/location/service/location.service';
import { ActivatedRoute, Router } from '@angular/router';
import { DatePipe } from '@angular/common';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { combineLatest, forkJoin, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

@Component({
  selector: 'jhi-show-day-events',
  templateUrl: './show-day-events.component.html',
  styleUrls: ['./show-day-events.component.scss'],
})
export class ShowDayEventsComponent implements OnInit {
  events?: AppEvent[];
  locations: MyLocation[] = [];
  locName?: string;
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

  getSignIn(event: AppEvent): void {
    this.router.navigate(['/event-signon', 'sign'], {
      queryParams: {
        event: event.id,
      },
    });
  }

  loadPage(page?: number, dontNavigate?: boolean): void {
    this.isLoading = true;
    const pageToLoad: number = page ?? this.page ?? 1;
    const datepipe: DatePipe = new DatePipe('en-US');
    const mydate = datepipe.transform(new Date(), 'YYYY-MM-dd');

    this.eventService
      .query({
        page: pageToLoad - 1,
        size: this.itemsPerPage,
        sort: this.sort(),
        date: mydate,
        // organizational query to be implemented here once completed
      })
      .subscribe(
        (res: HttpResponse<IAppEvent[]>) => {
          this.isLoading = false;

          // this.retrieveLocation();

          this.onSuccess(res.body, res.headers, pageToLoad, !dontNavigate);
        },
        () => {
          this.isLoading = false;
          this.onError();
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
      .pipe(mergeMap(([data, params]) => forkJoin([of(params.get('organization') as string), of(data), of(params)])))
      .subscribe(([o, data, params]) => {
        console.log('organization: ', o);
        // organizational code once implemented

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
      this.router.navigate(['/event-signon', 'showevents'], {
        queryParams: {
          page: this.page,
          size: this.itemsPerPage,
          sort: this.predicate + ',' + (this.ascending ? 'asc' : 'desc'),
          // organizational param to go here
        },
      });
    }
    this.events = data ?? [];

    this.ngbPaginationPage = this.page;
  }

  protected onError(): void {
    this.ngbPaginationPage = this.page ?? 1;
  }
}
