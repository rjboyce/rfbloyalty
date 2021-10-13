import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { forkJoin, Observable, of } from 'rxjs';
import { concatMap, finalize } from 'rxjs/operators';
import { IAppEvent, AppEvent } from '../event.model';
import { EventService } from '../service/event.service';
import { IMyLocation, MyLocation } from 'app/entities/location/location.model';
import { LocationService } from 'app/entities/location/service/location.service';
import { Valid } from 'app/entities/valid.model';

@Component({
  selector: 'jhi-event-update',
  templateUrl: './event-update.component.html',
})
export class EventUpdateComponent implements OnInit {
  isSaving = false;
  locInvalid = new Valid(false);

  // Search locations to be displayed in databind control
  locations: MyLocation[] = [];

  // Originating event location
  eventLocation: MyLocation | undefined;

  editForm = this.fb.group({
    id: [],
    eventName: [],
    eventDate: ['', Validators.required],
    startTime: ['', Validators.required],
    venue: [],
    projection: [],
    homeLocation: ['', Validators.required],
  });

  constructor(
    protected eventService: EventService,
    protected locationService: LocationService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data
      .pipe(
        concatMap(({ event }) => {
          if (event?.locationId) {
            return forkJoin([of({ event }), this.locationService.returnLocation(event.locationId)]);
          } else {
            return forkJoin([of({ event }), of(new MyLocation())]);
          }
        })
      )
      .subscribe(([{ event }, l]) => {
        this.eventLocation = l;
        this.updateForm(event);
      });
  }

  searchLocations(event: Event): void {
    const match: string = (<HTMLInputElement>event.target).value.trim();

    if (match.length > 0) {
      this.locationService.searchEntry(match, this.locInvalid, this.eventLocation as MyLocation).subscribe(x => {
        this.locations = x;
      });
    }
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const appEvent = this.createFromForm();
    if (appEvent?.id) {
      this.subscribeToSaveResponse(this.eventService.update(appEvent));
    } else {
      this.subscribeToSaveResponse(this.eventService.create(appEvent));
    }
  }

  trackMyLocationById(index: number, item: IMyLocation): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IAppEvent>>): void {
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

  protected updateForm(event: IAppEvent): void {
    this.editForm.patchValue({
      id: event.id,
      eventName: event.eventName,
      eventDate: event.eventDate,
      startTime: event.startTime,
      venue: event.venue,
      projection: event.projection,
      homeLocation: this.eventLocation?.locationName,
    });
  }

  protected createFromForm(): IAppEvent {
    return {
      ...new AppEvent(),
      id: this.editForm.get(['id'])!.value,
      eventName: this.editForm.get(['eventName'])!.value,
      eventDate: this.editForm.get(['eventDate'])!.value,
      startTime: this.editForm.get(['startTime'])!.value,
      venue: this.editForm.get(['venue'])!.value,
      projection: this.editForm.get(['projection'])!.value,
      locationId:
        this.locationService.checkValidLocation(this.editForm.get(['homeLocation'])!.value, this.locations, null) ?? this.eventLocation?.id,
    };
  }
}
