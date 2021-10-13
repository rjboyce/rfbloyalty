import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IAppEvent } from '../event.model';
import { LocationService } from 'app/entities/location/service/location.service';

@Component({
  selector: 'jhi-event-detail',
  templateUrl: './event-detail.component.html',
})
export class EventDetailComponent implements OnInit {
  event: IAppEvent | null = null;
  data = '';

  constructor(protected activatedRoute: ActivatedRoute, protected locationService: LocationService) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ event }) => {
      this.event = event;
      this.retrieveLocObject(this.event?.locationId);
    });
  }

  retrieveLocObject(id: number | null | undefined): void {
    this.data = 'No Location';

    if (id !== null && id !== undefined) {
      this.locationService.find(id).subscribe(l => {
        if (l !== null && l !== undefined) {
          this.data = l.body?.locationName as string;
        }
      });
    }
  }

  previousState(): void {
    window.history.back();
  }
}
