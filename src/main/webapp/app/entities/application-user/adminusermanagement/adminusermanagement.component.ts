import { Component, OnInit } from '@angular/core';
import { MyLocation } from 'app/entities/location/location.model';
import { ApplicationUserService } from 'app/entities/application-user/service/application-user.service';
import { LocationService } from 'app/entities/location/service/location.service';
import { ActivatedRoute } from '@angular/router';
import { FormBuilder, Validators } from '@angular/forms';
import { forkJoin, Observable, of } from 'rxjs';
import { HttpResponse } from '@angular/common/http';
import { ApplicationUser, IApplicationUser } from 'app/entities/application-user/application-user.model';
import { concatMap, finalize } from 'rxjs/operators';
import { Authority, IAuthority } from 'app/entities/application-user/authority.model';
import { Checked, IChecked } from 'app/entities/application-user/checked.model';
import { DaysOfWeek } from 'app/entities/application-user/daysofweek.model';
import { Valid } from 'app/entities/valid.model';

@Component({
  selector: 'jhi-adminusermanagement',
  templateUrl: './adminusermanagement.component.html',
  styleUrls: ['./adminusermanagement.component.scss'],
})
export class AdminusermanagementComponent implements OnInit {
  isSaving = false;
  locInvalid = new Valid(false);

  // Track the status of user activation control to ensure proper saving
  status = false;

  // homeLocationsCollection: number[] = [];

  // Search locations to be displayed in databind control
  locations: MyLocation[] = [];

  // Originating user location(if exists)
  userLocation: MyLocation | undefined;

  // Authorities assigned to user
  containedAuthorities: IAuthority[] = [];

  // All authorities in app are looped and represented as checkboxes on form (IRole represents the needed properties of
  // a checkbox and is iterated in template), roles will be checked based on existence of user authorities
  // (containedAuthorities)
  roles: IChecked[] = [];

  // set the checkboxes
  checkedAvailabilities: IChecked[] = [];

  editForm = this.fb.group({
    id: [],
    login: ['', Validators.required],
    firstName: [],
    lastName: [],
    email: [],
    phoneNumber: [],
    langKey: [],
    imageUrl: [],
    homeLocation: ['', Validators.required],
    activated: [],
  });

  constructor(
    protected applicationUserService: ApplicationUserService,
    protected locationService: LocationService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data
      .pipe(
        concatMap(({ applicationUser }) => {
          if (applicationUser?.homeLocationId) {
            return forkJoin([of({ applicationUser }), this.locationService.returnLocation(applicationUser.homeLocationId)]);
          } else {
            return forkJoin([of({ applicationUser }), of(new MyLocation())]);
          }
        })
      )
      .pipe(
        concatMap(([{ applicationUser }, l]) => {
          this.userLocation = l;
          this.updateForm(applicationUser);
          this.containedAuthorities = applicationUser.authorities;
          this.populateAvailability(applicationUser?.availability);
          return this.applicationUserService.getAuthorities();
        })
      )
      .subscribe(x => {
        if (x.body !== null) {
          this.populateRoles(x.body);
        }
      });
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

    if (applicationUser?.id) {
      this.subscribeToSaveResponse(this.applicationUserService.update(applicationUser));
    } else {
      this.subscribeToSaveResponse(this.applicationUserService.create(applicationUser));
    }
  }

  populateRoles(auths: IAuthority[]): void {
    auths.forEach(a => {
      const role: Checked = new Checked();
      role.name = a.name;
      role.id = a.name;
      role.checked = false;

      let index = -1;

      if (this.containedAuthorities !== undefined) {
        index = this.containedAuthorities.findIndex(x => a.name === x.name) ?? -1;
      } else {
        this.containedAuthorities = [] as Authority[];
      }

      if (index > -1) {
        role.checked = true;
      }
      this.roles.push(role);
    });
  }

  populateAvailability(days: string): void {
    // make sure incoming string isn't null
    if (days === null || days === undefined) {
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

        // can only use literals with typescript enums
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
            break; // should never happen as all boxes are properly extracted
        }
      }
    });

    return mynum.trim();
  }

  isActivated(event: Event): void {
    if ((<HTMLInputElement>event.target).checked) {
      this.status = true;
    } else {
      this.status = false;
    }
  }

  onAuthChange(event: Event, role: IChecked): void {
    const isChecked = (<HTMLInputElement>event.target).checked;
    const auth = new Authority();
    auth.name = role.name;

    if (isChecked) {
      this.containedAuthorities.push(auth);
    } else {
      const index: number = this.containedAuthorities.findIndex(x => x.name === auth.name);
      if (index > -1) {
        this.containedAuthorities.splice(index, 1);
      }
    }
  }

  checkLanguage(textbox: string | null | undefined): string {
    let lang = 'en';

    if (textbox !== null && textbox !== undefined) {
      if (textbox.trim() !== '') {
        lang = textbox.trim();
      }
    }
    return lang;
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
      activated: applicationUser.activated,
    });

    this.status = applicationUser.activated ?? false;
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
      langKey: this.checkLanguage(this.editForm.get(['langKey'])!.value),
      imageUrl: this.editForm.get(['imageUrl'])!.value,
      homeLocationId:
        this.locationService.checkValidLocation(this.editForm.get(['homeLocation'])!.value, this.locations, null) ?? this.userLocation?.id,
      activated: this.editForm.get(['activated'])!.value,
      authorities: this.containedAuthorities,
      availability: this.saveAvailability(),
    };
  }
}
