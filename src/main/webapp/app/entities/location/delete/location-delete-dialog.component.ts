import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IMyLocation } from '../location.model';
import { LocationService } from '../service/location.service';

@Component({
  templateUrl: './location-delete-dialog.component.html',
})
export class LocationDeleteDialogComponent {
  location?: IMyLocation;

  constructor(protected locationService: LocationService, public activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.locationService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
