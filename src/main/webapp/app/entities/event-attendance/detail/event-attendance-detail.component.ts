import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IEventAttendance } from '../event-attendance.model';
import { ApplicationUserService } from 'app/entities/application-user/service/application-user.service';

@Component({
  selector: 'jhi-event-attendance-detail',
  templateUrl: './event-attendance-detail.component.html',
})
export class EventAttendanceDetailComponent implements OnInit {
  eventAttendance: IEventAttendance | null = null;
  data = '';

  constructor(protected activatedRoute: ActivatedRoute, protected applicationUserService: ApplicationUserService) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ eventAttendance }) => {
      this.eventAttendance = eventAttendance;
      this.retrieveLocObject(this.eventAttendance?.volunteerId);
    });
  }

  retrieveLocObject(id: string | null | undefined): void {
    this.data = 'No Location';

    if (id !== null && id !== undefined) {
      this.applicationUserService.find(id).subscribe(l => {
        if (l !== null && l !== undefined) {
          this.data = (l.body?.firstName ?? '') + ' ' + (l.body?.lastName ?? '');
        }
      });
    }
  }

  previousState(): void {
    window.history.back();
  }
}
