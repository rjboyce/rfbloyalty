import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { forkJoin, Observable, of } from 'rxjs';
import { concatMap, finalize, mergeMap, switchMap } from 'rxjs/operators';
import { IEventAttendance, EventAttendance } from '../event-attendance.model';
import { EventAttendanceService } from '../service/event-attendance.service';
import { AppEvent, IAppEvent } from 'app/entities/event/event.model';
import { EventService } from 'app/entities/event/service/event.service';
import { ApplicationUser, IApplicationUser } from 'app/entities/application-user/application-user.model';
import { ApplicationUserService } from 'app/entities/application-user/service/application-user.service';
import { Valid } from 'app/entities/valid.model';
import { LocationService } from 'app/entities/location/service/location.service';
import { MyLocation } from 'app/entities/location/location.model';
import { v4 as uuid } from 'uuid';
// npm i --save-dev @types/uuid
// npm i --save uuid

@Component({
  selector: 'jhi-event-attendance-update',
  templateUrl: './event-attendance-update.component.html',
})
export class EventAttendanceUpdateComponent implements OnInit {
  isSaving = false;
  userInvalid = new Valid(false);
  eventInvalid = new Valid(false);

  theCode = '';

  locs: MyLocation[] = [];

  users: ApplicationUser[] = [];
  events: AppEvent[] = [];

  currentUser: ApplicationUser | undefined;
  currentEvent: AppEvent | undefined;
  volunteerCount = 0;

  editForm = this.fb.group({
    id: [],
    userCode: [],
    signIn: [],
    signOut: [],
    event: ['', Validators.required],
    volunteer: ['', Validators.required],
    quota: [],
  });

  constructor(
    protected eventAttendanceService: EventAttendanceService,
    protected eventService: EventService,
    protected locationService: LocationService,
    protected applicationUserService: ApplicationUserService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data
      .pipe(
        mergeMap(({ eventAttendance: eventAttendance }) =>
          forkJoin([
            this.applicationUserService.retrieveUser(eventAttendance?.volunteerId),
            this.eventService.retrieveEvent(eventAttendance?.eventId as number),
            of({ eventAttendance }),
          ])
        )
      )
      .pipe(
        mergeMap(([u, e, { eventAttendance: eventAttendance }]) => {
          this.currentUser = u;
          this.currentEvent = e;
          return forkJoin([
            this.locationService.returnLocation(e.locationId),
            this.eventAttendanceService.findAttendanceCountByEventAndUsercode(eventAttendance.eventId),
            of({ eventAttendance }),
          ]);
        })
      )
      .subscribe(([l, n, { eventAttendance: eventAttendance }]) => {
        this.locs.push(l);
        this.eventInvalid.value = false;
        this.userInvalid.value = false;
        this.updateForm(eventAttendance);
        this.volunteerCount = n;
      });
  }

  refreshCount(): void {
    if (this.currentEvent!.id !== undefined) {
      this.eventAttendanceService.findAttendanceCountByEventAndUsercode(this.currentEvent?.id as number).subscribe(x => {
        this.volunteerCount = x;
      });
    }
  }

  generateCode(): void {
    // query event attendances where event id matches and code !== null

    const gen = uuid().toString();
    const pt1 = gen.substring(0, 3);
    const pt2 = gen.substring(9, 12);
    const pt3 = gen.substring(gen.length - 3);
    // const fromIndex = ((this.currentUser?.login as string).length <= 3) ? 0 : (this.currentUser?.login as string).length - 3;
    // (this.currentUser?.login as string).substring(fromIndex).toString() // former part 2 of code
    const theCode = String(pt1) + '-' + String(pt2) + '-' + String(pt3);
    this.editForm.get('userCode')!.setValue(theCode);

    this.refreshCount();
  }

  searchUsers(event: Event): void {
    const match: string = (<HTMLInputElement>event.target).value.trim();

    this.applicationUserService.searchEntry(match, this.userInvalid).subscribe(s => (this.users = s));
  }

  searchEvents(event: Event): void {
    const match: string = (<HTMLInputElement>event.target).value.trim();
    this.locs.length = 0;

    if (match.length > 0) {
      this.eventService
        .searchEntry(match, this.eventInvalid, this.currentEvent as AppEvent)
        .pipe(
          switchMap(ee => {
            this.events = ee;
            this.refreshCount();
            return ee;
          })
        )
        .pipe(concatMap(e => this.locationService.returnLocation(e.locationId)))
        // get and pass associated location with the event
        .subscribe(l => {
          // we need to make sure location does not already exist in a collected list before storing

          // objects with identical values are not equal so we must use type assertion with a find
          // THIS IS IMPORTANT AS IT PREVENTS OUR GLOBAL VARIABLE FROM EXPONENTIALLY GROWING!!!
          if (!this.locs.includes(<MyLocation>this.locs.find(x => x.locationName === l.locationName))) {
            this.locs.push(l);
          }
        });
    } else {
      this.currentEvent!.projection = undefined;
      this.volunteerCount = 0;
    }
  }

  getLocationName(locationId: number | undefined | null): string {
    let dis = 'No Location';

    if (locationId === null || locationId === undefined) {
      return dis;
    }

    this.locs.forEach(l => {
      if (locationId === l.id) {
        dis = l.locationName as string;
      }
    });

    return dis;
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const eventAttendance = this.createFromForm();
    if (eventAttendance.id !== undefined && eventAttendance.id !== null) {
      this.subscribeToSaveResponse(this.eventAttendanceService.update(eventAttendance));
    } else {
      this.subscribeToSaveResponse(this.eventAttendanceService.create(eventAttendance));
    }
  }

  trackAppEventById(index: number, item: IAppEvent): number {
    return item.id!;
  }

  trackApplicationUserById(index: number, item: IApplicationUser): string {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IEventAttendance>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(eventAttendance: IEventAttendance): void {
    this.editForm.patchValue({
      id: eventAttendance.id,
      userCode: eventAttendance.userCode,
      event: this.currentEvent?.eventName,
      signIn: eventAttendance?.signIn,
      signOut: eventAttendance?.signOut,
      volunteer: this.currentUser?.login,
      quota: this.volunteerCount,
    });
  }

  protected createFromForm(): IEventAttendance {
    return {
      ...new EventAttendance(),
      id: this.editForm.get(['id'])!.value,
      userCode: this.editForm.get(['userCode'])!.value,
      eventId: this.eventService.checkValidEvent(this.editForm.get(['event'])!.value, this.events, null) ?? this.currentEvent?.id,
      signIn: this.editForm.get(['signIn'])!.value,
      signOut: this.editForm.get(['signOut'])!.value,
      volunteerId:
        this.applicationUserService.checkValidUser(
          this.editForm.get(['volunteer'])!.value,
          this.users,
          this.currentUser as ApplicationUser
        ) ?? this.currentUser?.id,
    };
  }
}
