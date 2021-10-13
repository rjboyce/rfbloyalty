import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { IApplicationUser } from '../application-user.model';
import { LocationService } from 'app/entities/location/service/location.service';

@Component({
  selector: 'jhi-application-user-detail',
  templateUrl: './application-user-detail.component.html',
})
export class ApplicationUserDetailComponent implements OnInit {
  applicationUser: IApplicationUser | null = null;
  data = '';

  constructor(protected activatedRoute: ActivatedRoute, protected locationService: LocationService) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ applicationUser }) => {
      this.applicationUser = applicationUser;
      this.retrieveLocObject(this.applicationUser?.homeLocationId);
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
