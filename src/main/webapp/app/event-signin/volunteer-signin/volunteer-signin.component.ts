import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { concatMap } from 'rxjs/operators';
import { ActivatedRoute } from '@angular/router';
import { AppEvent } from 'app/entities/event/event.model';
import { EventService } from 'app/entities/event/service/event.service';
import { EventAttendanceService } from 'app/entities/event-attendance/service/event-attendance.service';
import { DatePipe } from '@angular/common';
import { forkJoin, of } from 'rxjs';
import { HttpResponse } from '@angular/common/http';
import { IEventAttendance } from 'app/entities/event-attendance/event-attendance.model';
import { ApplicationUserService } from 'app/entities/application-user/service/application-user.service';
import { ApplicationUser } from 'app/entities/application-user/application-user.model';

@Component({
  selector: 'jhi-volunteer-signin',
  templateUrl: './volunteer-signin.component.html',
  styleUrls: ['./volunteer-signin.component.scss'],
})
export class VolunteerSigninComponent implements OnInit, OnDestroy {
  currentEvent?: AppEvent;
  message?: string;

  editForm = this.fb.group({
    codeOne: ['', Validators.required],
    codeTwo: ['', Validators.required],
    codeThree: ['', Validators.required],
  });

  constructor(
    protected fb: FormBuilder,
    protected activatedRoute: ActivatedRoute,
    protected eventService: EventService,
    protected eventAttendanceService: EventAttendanceService,
    protected applicationUserService: ApplicationUserService
  ) {}

  ngOnInit(): void {
    this.activatedRoute.queryParamMap.pipe(concatMap(e => this.eventService.retrieveEvent(+(e.get('event') as string)))).subscribe(e => {
      this.currentEvent = e;
      this.message = undefined;
    });
  }

  validate(): void {
    const userCode =
      String(this.editForm.get('codeOne')!.value).trim() +
      '-' +
      String(this.editForm.get('codeTwo')!.value).trim() +
      '-' +
      String(this.editForm.get('codeThree')!.value).trim();

    const datepipe: DatePipe = new DatePipe('en-US');
    const currentTime = datepipe.transform(new Date(), 'h:mm a');

    this.editForm.reset();

    this.eventAttendanceService
      .singlequery({ event: this.currentEvent!.id, usercode: userCode })
      .pipe(
        concatMap(ea => {
          if (ea.body?.id) {
            return forkJoin([of(ea), this.applicationUserService.retrieveUser(ea.body.volunteerId as string)]);
          } else {
            return forkJoin([of(ea), of(new ApplicationUser())]);
          }
        })
      )
      .pipe(
        concatMap(([ea, u]) => {
          if (ea.body?.id) {
            const attendance = ea.body;
            const username = String(u.firstName);

            if (!attendance?.signIn) {
              attendance.signIn = currentTime as string;
              return forkJoin([
                of('Welcome, ' + username + '. You have successfully signed in...'),
                this.eventAttendanceService.update(attendance),
              ]);
            } else if (attendance?.signIn && !attendance?.signOut) {
              attendance.signOut = currentTime as string;
              return forkJoin([
                of('Goodbye, ' + username + '. You have successfully signed out...'),
                this.eventAttendanceService.update(attendance),
              ]);
            } else {
              return forkJoin([of('You have already signed out!'), of(new HttpResponse<IEventAttendance>())]);
            }
          } else {
            return forkJoin([
              of('Invalid code. Please check you have entered the correct code.'),
              of(new HttpResponse<IEventAttendance>()),
            ]);
          }
        })
      )
      .subscribe(([m, h]) => {
        this.message = m;
        console.log('log in/out status: ', h.status);
        window.alert(this.message);
      });
  }

  ngOnDestroy(): void {
    console.log(this.activatedRoute.url);
  }
}
