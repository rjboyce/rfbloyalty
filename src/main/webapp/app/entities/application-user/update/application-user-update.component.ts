import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';
import { IApplicationUser, ApplicationUser } from '../application-user.model';
import { ApplicationUserService } from '../service/application-user.service';
import { MyLocation } from 'app/entities/location/location.model';
import { LocationService } from 'app/entities/location/service/location.service';
import { Authority } from 'app/entities/application-user/authority.model';
import { DaysOfWeek } from 'app/entities/application-user/daysofweek.model';
import { Checked, IChecked } from 'app/entities/application-user/checked.model';
import { Valid } from 'app/entities/valid.model';

@Component({
  selector: 'jhi-application-user-update',
  templateUrl: './application-user-update.component.html',
  styleUrls: ['./application-user-update.component.scss'],
})
export class ApplicationUserUpdateComponent implements OnInit {
  isSaving = false;
  locInvalid = new Valid(false);

  activated = false;
  auths: Authority[] = [];

  // Search locations to be displayed in databind control
  locations: MyLocation[] = [];

  // Originating user location
  userLocation: MyLocation | undefined;

  // set the checkboxes
  checkedAvailabilities: IChecked[] = [];

  editForm = this.fb.group({
    id: [],
    login: [],
    firstName: [],
    lastName: [],
    email: [],
    phoneNumber: [],
    langKey: [],
    imageUrl: [],
    homeLocation: ['', Validators.required],
  });

  constructor(
    protected applicationUserService: ApplicationUserService,
    protected locationService: LocationService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ applicationUser }) => {
      this.populateAvailability(applicationUser?.availability);
      this.activated = (applicationUser as ApplicationUser)?.activated ?? false;
      this.auths = (applicationUser as ApplicationUser)?.authorities ?? [];
      this.retrieveLocation(applicationUser);
    });
  }

  populateAvailability(days: string): void {
    // make sure incoming string isn't null
    if (days === null) {
      days = '';
    }

    // iterate through enum keys (days of the week)
    Object.values(DaysOfWeek)
      .filter(f => typeof f === 'number')
      .forEach(s => {
        // preset checkbox as we display it regardless of checked value
        const day: Checked = new Checked();
        day.id = DaysOfWeek[s as number];
        day.name = DaysOfWeek[s as number];
        day.checked = false;

        // check if day is set to user and set checkbox to checked (true)
        if (days.includes(s.toString())) {
          day.checked = true;
        }

        // add checkbox to collection to be displayed in template
        this.checkedAvailabilities.push(day);
      });
  }

  onDayChange(event: Event, day: IChecked): void {
    const isChecked = (<HTMLInputElement>event.target).checked;

    this.checkedAvailabilities.filter(s => {
      if (s === day) {
        if (isChecked) {
          s.checked = true;
        } else {
          s.checked = false;
        }
      }
    });
  }

  saveAvailability(): string {
    let mynum = '';

    this.checkedAvailabilities.forEach(s => {
      if (s.checked) {
        const myval = Object.keys(DaysOfWeek)
          .filter(f => f === (s.name as string))
          .toString();

        // can only use literals for enums
        switch (myval) {
          case 'Monday':
            mynum += DaysOfWeek['Monday'].toString();
            break;
          case 'Tuesday':
            mynum += DaysOfWeek['Tuesday'].toString();
            break;
          case 'Wednesday':
            mynum += DaysOfWeek['Wednesday'].toString();
            break;
          case 'Thursday':
            mynum += DaysOfWeek['Thursday'].toString();
            break;
          case 'Friday':
            mynum += DaysOfWeek['Friday'].toString();
            break;
          case 'Saturday':
            mynum += DaysOfWeek['Saturday'].toString();
            break;
          case 'Sunday':
            mynum += DaysOfWeek['Sunday'].toString();
            break;
          default:
            break; //  should never happen as all boxes are properly extracted
        }
      }
    });

    return mynum.trim();
  }

  retrieveLocation(user: ApplicationUser): void {
    if (user.homeLocationId !== null && user.homeLocationId !== undefined) {
      this.locationService.returnLocation(user.homeLocationId).subscribe(l => {
        this.userLocation = l;
        this.updateForm(user);
      });
    } else {
      this.userLocation = new MyLocation();
      this.updateForm(user);
    }
  }

  searchLocations(event: Event): void {
    const match: string = (<HTMLInputElement>event.target).value.trim();

    if (match.length > 0) {
      this.locationService.searchEntry(match, this.locInvalid, this.userLocation as MyLocation).subscribe(x => {
        this.locations = x;
      });
    }
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const applicationUser = this.createFromForm();

    if (applicationUser.id !== undefined) {
      this.subscribeToSaveResponse(this.applicationUserService.update(applicationUser));
    } else {
      // TODO: this can never happen in this context and should be removed in the future
      this.subscribeToSaveResponse(this.applicationUserService.create(applicationUser));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IApplicationUser>>): void {
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

  protected updateForm(applicationUser: IApplicationUser): void {
    this.editForm.patchValue({
      id: applicationUser.id,
      login: applicationUser.login,
      firstName: applicationUser.firstName,
      lastName: applicationUser.lastName,
      email: applicationUser.email,
      phoneNumber: applicationUser.phoneNumber,
      langKey: applicationUser.langKey,
      imageUrl: applicationUser.imageUrl,
      homeLocation: this.userLocation?.locationName,
    });
  }

  protected createFromForm(): IApplicationUser {
    return {
      ...new ApplicationUser(),
      id: this.editForm.get(['id'])!.value,
      login: this.editForm.get(['login'])!.value,
      firstName: this.editForm.get(['firstName'])!.value,
      lastName: this.editForm.get(['lastName'])!.value,
      email: this.editForm.get(['email'])!.value,
      phoneNumber: this.editForm.get(['phoneNumber'])!.value,
      langKey: this.editForm.get(['langKey'])!.value,
      imageUrl: this.editForm.get(['imageUrl'])!.value,
      homeLocationId:
        this.locationService.checkValidLocation(this.editForm.get(['homeLocation'])!.value, this.locations, null) ?? this.userLocation?.id,
      authorities: this.auths,
      activated: this.activated,
      availability: this.saveAvailability(),
    };
  }
}
