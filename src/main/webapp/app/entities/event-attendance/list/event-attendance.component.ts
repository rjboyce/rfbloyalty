import { Component, OnInit } from '@angular/core';
import { HttpHeaders } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { EventAttendance, IEventAttendance } from '../event-attendance.model';

import { ITEMS_PER_PAGE } from 'app/config/pagination.constants';
import { EventAttendanceService } from '../service/event-attendance.service';
import { EventAttendanceDeleteDialogComponent } from '../delete/event-attendance-delete-dialog.component';
import { ParseLinks } from 'app/core/util/parse-links.service';
import { ApplicationUser } from 'app/entities/application-user/application-user.model';
import { forkJoin } from 'rxjs';
import { mergeMap, switchMap } from 'rxjs/operators';
import { ApplicationUserService } from 'app/entities/application-user/service/application-user.service';
import { AppEvent } from 'app/entities/event/event.model';
import { EventService } from 'app/entities/event/service/event.service';

@Component({
  selector: 'jhi-event-attendance',
  templateUrl: './event-attendance.component.html',
})
export class EventAttendanceComponent implements OnInit {
  eventAttendances: IEventAttendance[];
  appUsers: ApplicationUser[] = [];
  appEvents: AppEvent[] = [];
  isLoading = false;
  itemsPerPage: number;
  links: { [key: string]: number };
  page: number;
  predicate: string;
  ascending: boolean;

  constructor(
    protected eventAttendanceService: EventAttendanceService,
    protected modalService: NgbModal,
    protected parseLinks: ParseLinks,
    protected applicationUserService: ApplicationUserService,
    protected eventService: EventService
  ) {
    this.eventAttendances = [];
    this.itemsPerPage = ITEMS_PER_PAGE;
    this.page = 0;
    this.links = {
      last: 0,
    };
    this.predicate = 'id';
    this.ascending = true;
  }

  loadAll(): void {
    this.isLoading = true;

    this.eventAttendanceService
      .query({
        page: this.page,
        size: this.itemsPerPage,
        sort: this.sort(),
      })

      .pipe(
        switchMap(f => {
          this.isLoading = false;
          this.paginateEventAttendances(f.body, f.headers);
          return f.body as EventAttendance[];
        })
      )

      .pipe(
        mergeMap(m => forkJoin([this.applicationUserService.find(m.volunteerId as string), this.eventService.find(m.eventId as number)]))
      )

      .subscribe(
        ([myuser, myevent]) => {
          if (!this.appUsers.includes(<ApplicationUser>this.appUsers.find(x => x.id === myuser.body?.id))) {
            this.appUsers.push(myuser.body as ApplicationUser);
          }

          if (!this.appEvents.includes(<AppEvent>this.appEvents.find(x => x.id === myevent.body?.id))) {
            this.appEvents.push(myevent.body as AppEvent);
          }
        },
        () => {
          this.isLoading = false;
        }
      );
  }

  returnVolunteerName(eventAttendances: IEventAttendance): string {
    let dis = 'Name Unavailable';

    this.appUsers?.forEach((l: ApplicationUser) => {
      if (l.id === eventAttendances?.volunteerId) {
        dis = (l?.firstName ?? '') + ' ' + (l?.lastName ?? '');
      }
    });

    return dis;
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

  /* retrieveUsers(): void {
    const ids: string[] = [];

    this.eventAttendances?.forEach(a => {
      if (a.volunteerId !== null && a.volunteerId !== undefined) {
        if (!ids.includes(a.volunteerId)) {
          ids.push(a.volunteerId);
        }
      }
    });

    from(ids)
      .pipe(mergeMap(id => this.applicationUserService.find(id)))
      .subscribe(users => this.appUsers.push(users.body as ApplicationUser));
  }*/

  reset(): void {
    this.page = 0;
    this.eventAttendances = [];
    this.loadAll();
  }

  loadPage(page: number): void {
    this.page = page;
    this.loadAll();
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: IEventAttendance): number {
    return item.id!;
  }

  delete(eventAttendance: IEventAttendance): void {
    const modalRef = this.modalService.open(EventAttendanceDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.eventAttendance = eventAttendance;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.reset();
      }
    });
  }

  protected sort(): string[] {
    const result = [this.predicate + ',' + (this.ascending ? 'asc' : 'desc')];
    if (this.predicate !== 'id') {
      result.push('id');
    }
    return result;
  }

  protected paginateEventAttendances(data: IEventAttendance[] | null, headers: HttpHeaders): void {
    this.links = this.parseLinks.parse(headers.get('link') ?? '');
    if (data) {
      for (const d of data) {
        this.eventAttendances.push(d);
      }
    }
  }
}
