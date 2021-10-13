import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IEventAttendance } from '../event-attendance.model';
import { EventAttendanceService } from '../service/event-attendance.service';

@Component({
  templateUrl: './event-attendance-delete-dialog.component.html',
})
export class EventAttendanceDeleteDialogComponent {
  eventAttendance?: IEventAttendance;

  constructor(protected eventAttendanceService: EventAttendanceService, public activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.eventAttendanceService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
