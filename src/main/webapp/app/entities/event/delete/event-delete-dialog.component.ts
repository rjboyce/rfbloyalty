import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IAppEvent } from '../event.model';
import { EventService } from '../service/event.service';

@Component({
  templateUrl: './event-delete-dialog.component.html',
})
export class EventDeleteDialogComponent {
  event?: IAppEvent;

  constructor(protected eventService: EventService, public activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.eventService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
