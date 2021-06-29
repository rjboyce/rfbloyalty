import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IApplicationUser, ApplicationUser } from '../application-user.model';
import { ApplicationUserService } from '../service/application-user.service';
import { IRfbLocation } from 'app/entities/rfb-location/rfb-location.model';
import { RfbLocationService } from 'app/entities/rfb-location/service/rfb-location.service';

@Component({
  selector: 'jhi-application-user-update',
  templateUrl: './application-user-update.component.html',
})
export class ApplicationUserUpdateComponent implements OnInit {
  isSaving = false;

  homeLocationsCollection: IRfbLocation[] = [];

  editForm = this.fb.group({
    id: [],
    login: [],
    firstName: [],
    lastName: [],
    email: [],
    langKey: [],
    imageUrl: [],
    homeLocation: [],
  });

  constructor(
    protected applicationUserService: ApplicationUserService,
    protected rfbLocationService: RfbLocationService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ applicationUser }) => {
      this.updateForm(applicationUser);

      this.loadRelationshipsOptions();
    });
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
      this.subscribeToSaveResponse(this.applicationUserService.create(applicationUser));
    }
  }

  trackRfbLocationById(index: number, item: IRfbLocation): number {
    return item.id!;
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
      langKey: applicationUser.langKey,
      imageUrl: applicationUser.imageUrl,
      homeLocation: applicationUser.homeLocation,
    });

    this.homeLocationsCollection = this.rfbLocationService.addRfbLocationToCollectionIfMissing(
      this.homeLocationsCollection,
      applicationUser.homeLocation
    );
  }

  protected loadRelationshipsOptions(): void {
    this.rfbLocationService
      .query({ filter: 'applicationuser-is-null' })
      .pipe(map((res: HttpResponse<IRfbLocation[]>) => res.body ?? []))
      .pipe(
        map((rfbLocations: IRfbLocation[]) =>
          this.rfbLocationService.addRfbLocationToCollectionIfMissing(rfbLocations, this.editForm.get('homeLocation')!.value)
        )
      )
      .subscribe((rfbLocations: IRfbLocation[]) => (this.homeLocationsCollection = rfbLocations));
  }

  protected createFromForm(): IApplicationUser {
    return {
      ...new ApplicationUser(),
      id: this.editForm.get(['id'])!.value,
      login: this.editForm.get(['login'])!.value,
      firstName: this.editForm.get(['firstName'])!.value,
      lastName: this.editForm.get(['lastName'])!.value,
      email: this.editForm.get(['email'])!.value,
      langKey: this.editForm.get(['langKey'])!.value,
      imageUrl: this.editForm.get(['imageUrl'])!.value,
      homeLocation: this.editForm.get(['homeLocation'])!.value,
    };
  }
}
