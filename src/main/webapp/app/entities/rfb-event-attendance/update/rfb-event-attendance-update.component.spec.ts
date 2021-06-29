jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { RfbEventAttendanceService } from '../service/rfb-event-attendance.service';
import { IRfbEventAttendance, RfbEventAttendance } from '../rfb-event-attendance.model';
import { IRfbEvent } from 'app/entities/rfb-event/rfb-event.model';
import { RfbEventService } from 'app/entities/rfb-event/service/rfb-event.service';
import { IApplicationUser } from 'app/entities/application-user/application-user.model';
import { ApplicationUserService } from 'app/entities/application-user/service/application-user.service';

import { RfbEventAttendanceUpdateComponent } from './rfb-event-attendance-update.component';

describe('Component Tests', () => {
  describe('RfbEventAttendance Management Update Component', () => {
    let comp: RfbEventAttendanceUpdateComponent;
    let fixture: ComponentFixture<RfbEventAttendanceUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let rfbEventAttendanceService: RfbEventAttendanceService;
    let rfbEventService: RfbEventService;
    let applicationUserService: ApplicationUserService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [RfbEventAttendanceUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(RfbEventAttendanceUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(RfbEventAttendanceUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      rfbEventAttendanceService = TestBed.inject(RfbEventAttendanceService);
      rfbEventService = TestBed.inject(RfbEventService);
      applicationUserService = TestBed.inject(ApplicationUserService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call RfbEvent query and add missing value', () => {
        const rfbEventAttendance: IRfbEventAttendance = { id: 456 };
        const rfbEvent: IRfbEvent = { id: 84547 };
        rfbEventAttendance.rfbEvent = rfbEvent;

        const rfbEventCollection: IRfbEvent[] = [{ id: 85861 }];
        spyOn(rfbEventService, 'query').and.returnValue(of(new HttpResponse({ body: rfbEventCollection })));
        const additionalRfbEvents = [rfbEvent];
        const expectedCollection: IRfbEvent[] = [...additionalRfbEvents, ...rfbEventCollection];
        spyOn(rfbEventService, 'addRfbEventToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ rfbEventAttendance });
        comp.ngOnInit();

        expect(rfbEventService.query).toHaveBeenCalled();
        expect(rfbEventService.addRfbEventToCollectionIfMissing).toHaveBeenCalledWith(rfbEventCollection, ...additionalRfbEvents);
        expect(comp.rfbEventsSharedCollection).toEqual(expectedCollection);
      });

      it('Should call ApplicationUser query and add missing value', () => {
        const rfbEventAttendance: IRfbEventAttendance = { id: 456 };
        const applicationUser: IApplicationUser = { id: 33811 };
        rfbEventAttendance.applicationUser = applicationUser;

        const applicationUserCollection: IApplicationUser[] = [{ id: 20797 }];
        spyOn(applicationUserService, 'query').and.returnValue(of(new HttpResponse({ body: applicationUserCollection })));
        const additionalApplicationUsers = [applicationUser];
        const expectedCollection: IApplicationUser[] = [...additionalApplicationUsers, ...applicationUserCollection];
        spyOn(applicationUserService, 'addApplicationUserToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ rfbEventAttendance });
        comp.ngOnInit();

        expect(applicationUserService.query).toHaveBeenCalled();
        expect(applicationUserService.addApplicationUserToCollectionIfMissing).toHaveBeenCalledWith(
          applicationUserCollection,
          ...additionalApplicationUsers
        );
        expect(comp.applicationUsersSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const rfbEventAttendance: IRfbEventAttendance = { id: 456 };
        const rfbEvent: IRfbEvent = { id: 133 };
        rfbEventAttendance.rfbEvent = rfbEvent;
        const applicationUser: IApplicationUser = { id: 58002 };
        rfbEventAttendance.applicationUser = applicationUser;

        activatedRoute.data = of({ rfbEventAttendance });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(rfbEventAttendance));
        expect(comp.rfbEventsSharedCollection).toContain(rfbEvent);
        expect(comp.applicationUsersSharedCollection).toContain(applicationUser);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const rfbEventAttendance = { id: 123 };
        spyOn(rfbEventAttendanceService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ rfbEventAttendance });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: rfbEventAttendance }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(rfbEventAttendanceService.update).toHaveBeenCalledWith(rfbEventAttendance);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const rfbEventAttendance = new RfbEventAttendance();
        spyOn(rfbEventAttendanceService, 'create').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ rfbEventAttendance });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: rfbEventAttendance }));
        saveSubject.complete();

        // THEN
        expect(rfbEventAttendanceService.create).toHaveBeenCalledWith(rfbEventAttendance);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject();
        const rfbEventAttendance = { id: 123 };
        spyOn(rfbEventAttendanceService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ rfbEventAttendance });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(rfbEventAttendanceService.update).toHaveBeenCalledWith(rfbEventAttendance);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });

    describe('Tracking relationships identifiers', () => {
      describe('trackRfbEventById', () => {
        it('Should return tracked RfbEvent primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackRfbEventById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });

      describe('trackApplicationUserById', () => {
        it('Should return tracked ApplicationUser primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackApplicationUserById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });
    });
  });
});
