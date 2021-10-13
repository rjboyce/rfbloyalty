import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { IApplicationUser } from '../application-user.model';
import { ApplicationUserService } from '../service/application-user.service';
import { ApplicationUserDeleteDialogComponent } from '../delete/application-user-delete-dialog.component';
import { LocationService } from 'app/entities/location/service/location.service';
import { MyLocation } from 'app/entities/location/location.model';
import { mergeMap } from 'rxjs/operators';
import { from } from 'rxjs';

@Component({
  selector: 'jhi-application-user',
  templateUrl: './application-user.component.html',
})
export class ApplicationUserComponent implements OnInit {
  applicationUsers?: IApplicationUser[];
  isLoading = false;
  locations: MyLocation[] = [];

  constructor(
    protected applicationUserService: ApplicationUserService,
    protected modalService: NgbModal,
    protected locationService: LocationService
  ) {}

  loadAll(): void {
    this.isLoading = true;

    this.applicationUserService.query().subscribe(
      (res: HttpResponse<IApplicationUser[]>) => {
        this.isLoading = false;
        this.applicationUsers = res.body ?? [];
        this.retrieveLocations();
      },
      () => {
        this.isLoading = false;
      }
    );
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: IApplicationUser): string {
    return item.id!;
  }

  returnName(applicationUser: IApplicationUser): string {
    let dis = 'No Location';

    if (applicationUser?.homeLocationId !== null) {
      this.locations?.forEach((l: MyLocation) => {
        if (l.id === applicationUser?.homeLocationId) {
          dis = l?.locationName ?? 'Error Loading';
        }
      });
    }

    return dis;
  }

  retrieveLocations(): void {
    const ids: number[] = [];

    this.applicationUsers?.forEach(a => {
      if (a.homeLocationId !== null && a.homeLocationId !== undefined) {
        if (!ids.includes(a.homeLocationId)) {
          ids.push(a.homeLocationId);
        }
      }
    });

    from(ids)
      .pipe(mergeMap(id => this.locationService.find(id)))
      .subscribe(locs => this.locations.push(locs.body as MyLocation));
  }

  delete(applicationUser: IApplicationUser): void {
    const modalRef = this.modalService.open(ApplicationUserDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.applicationUser = applicationUser;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }

  getActivatedLabel(applicationUser: IApplicationUser): string {
    let act = applicationUser?.activated;
    if (act === null) {
      act = false;
    }
    return act ? 'badge-success' : 'badge-primary';
  }

  getAuthorities(applicationUser: IApplicationUser): string {
    const auths = applicationUser?.authorities ?? null;
    let dis = '';
    if (auths !== null) {
      auths.forEach(a => {
        const name = a?.name ?? '';
        if (name !== '') {
          dis = dis + '[' + name + '] ';
        }
      });
      if (dis === '') {
        dis = 'No Roles';
      }
      return dis;
    }
    return 'No Roles';
  }

  getStatus(applicationUser: IApplicationUser): string {
    let act = applicationUser?.activated;
    if (act === null) {
      act = false;
    }
    if (act === true) {
      return 'Activated';
    } else {
      return 'Deactivated';
    }
  }
}
