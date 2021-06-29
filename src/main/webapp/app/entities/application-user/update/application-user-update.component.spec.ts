jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { ApplicationUserService } from '../service/application-user.service';
import { IApplicationUser, ApplicationUser } from '../application-user.model';
import { IRfbLocation } from 'app/entities/rfb-location/rfb-location.model';
import { RfbLocationService } from 'app/entities/rfb-location/service/rfb-location.service';

import { ApplicationUserUpdateComponent } from './application-user-update.component';

describe('Component Tests', () => {
  describe('ApplicationUser Management Update Component', () => {
    let comp: ApplicationUserUpdateComponent;
    let fixture: ComponentFixture<ApplicationUserUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let applicationUserService: ApplicationUserService;
    let rfbLocationService: RfbLocationService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [ApplicationUserUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(ApplicationUserUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(ApplicationUserUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      applicationUserService = TestBed.inject(ApplicationUserService);
      rfbLocationService = TestBed.inject(RfbLocationService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call homeLocation query and add missing value', () => {
        const applicationUser: IApplicationUser = { id: 456 };
        const homeLocation: IRfbLocation = { id: 35334 };
        applicationUser.homeLocation = homeLocation;

        const homeLocationCollection: IRfbLocation[] = [{ id: 84255 }];
        spyOn(rfbLocationService, 'query').and.returnValue(of(new HttpResponse({ body: homeLocationCollection })));
        const expectedCollection: IRfbLocation[] = [homeLocation, ...homeLocationCollection];
        spyOn(rfbLocationService, 'addRfbLocationToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ applicationUser });
        comp.ngOnInit();

        expect(rfbLocationService.query).toHaveBeenCalled();
        expect(rfbLocationService.addRfbLocationToCollectionIfMissing).toHaveBeenCalledWith(homeLocationCollection, homeLocation);
        expect(comp.homeLocationsCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const applicationUser: IApplicationUser = { id: 456 };
        const homeLocation: IRfbLocation = { id: 85465 };
        applicationUser.homeLocation = homeLocation;

        activatedRoute.data = of({ applicationUser });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(applicationUser));
        expect(comp.homeLocationsCollection).toContain(homeLocation);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const applicationUser = { id: 123 };
        spyOn(applicationUserService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ applicationUser });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: applicationUser }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(applicationUserService.update).toHaveBeenCalledWith(applicationUser);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const applicationUser = new ApplicationUser();
        spyOn(applicationUserService, 'create').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ applicationUser });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: applicationUser }));
        saveSubject.complete();

        // THEN
        expect(applicationUserService.create).toHaveBeenCalledWith(applicationUser);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject();
        const applicationUser = { id: 123 };
        spyOn(applicationUserService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ applicationUser });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(applicationUserService.update).toHaveBeenCalledWith(applicationUser);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });

    describe('Tracking relationships identifiers', () => {
      describe('trackRfbLocationById', () => {
        it('Should return tracked RfbLocation primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackRfbLocationById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });
    });
  });
});
