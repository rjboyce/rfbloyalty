import { Component, OnInit } from '@angular/core';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { combineLatest, from } from 'rxjs';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { IAppEvent } from '../event.model';
import { ITEMS_PER_PAGE } from 'app/config/pagination.constants';
import { EventService } from '../service/event.service';
import { EventDeleteDialogComponent } from '../delete/event-delete-dialog.component';
import { MyLocation } from 'app/entities/location/location.model';
import { mergeMap } from 'rxjs/operators';
import { LocationService } from 'app/entities/location/service/location.service';

@Component({
  selector: 'jhi-event',
  templateUrl: './event.component.html',
})
export class EventComponent implements OnInit {
  events?: IAppEvent[];
  locations: MyLocation[] = [];
  isLoading = false;
  totalItems = 0;
  itemsPerPage = ITEMS_PER_PAGE;
  page?: number;
  predicate!: string;
  ascending!: boolean;
  ngbPaginationPage = 1;

  constructor(
    protected eventService: EventService,
    protected activatedRoute: ActivatedRoute,
    protected router: Router,
    protected modalService: NgbModal,
    protected locationService: LocationService
  ) {}

  loadPage(page?: number, dontNavigate?: boolean): void {
    this.isLoading = true;
    const pageToLoad: number = page ?? this.page ?? 1;

    this.eventService
      .query({
        page: pageToLoad - 1,
        size: this.itemsPerPage,
        sort: this.sort(),
      })
      .subscribe(
        (res: HttpResponse<IAppEvent[]>) => {
          this.isLoading = false;
          this.onSuccess(res.body, res.headers, pageToLoad, !dontNavigate);
          this.retrieveLocations();
        },
        () => {
          this.isLoading = false;
          this.onError();
        }
      );
  }

  ngOnInit(): void {
    this.handleNavigation();
  }

  returnName(event: IAppEvent): string {
    let dis = 'No Location';

    if (event.locationId !== null) {
      this.locations?.forEach((l: MyLocation) => {
        if (l.id === event.locationId) {
          dis = l?.locationName ?? 'Error Loading';
        }
      });
    }

    return dis;
  }

  retrieveLocations(): void {
    const ids: number[] = [];

    this.events?.forEach(a => {
      if (a.locationId !== null && a.locationId !== undefined) {
        if (!ids.includes(a.locationId)) {
          ids.push(a.locationId);
        }
      }
    });

    from(ids)
      .pipe(mergeMap(id => this.locationService.find(id)))
      .subscribe(locs => this.locations.push(locs.body as MyLocation));
  }

  trackId(index: number, item: IAppEvent): number {
    return item.id!;
  }

  delete(event: IAppEvent): void {
    const modalRef = this.modalService.open(EventDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.event = event;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadPage();
      }
    });
  }

  protected sort(): string[] {
    const result = [this.predicate + ',' + (this.ascending ? 'asc' : 'desc')];
    if (this.predicate !== 'id') {
      result.push('id');
    }
    return result;
  }

  protected handleNavigation(): void {
    combineLatest([this.activatedRoute.data, this.activatedRoute.queryParamMap]).subscribe(([data, params]) => {
      const page = params.get('page');
      const pageNumber = page !== null ? +page : 1;
      const sort = (params.get('sort') ?? data['defaultSort']).split(',');
      const predicate = sort[0];
      const ascending = sort[1] === 'asc';
      if (pageNumber !== this.page || predicate !== this.predicate || ascending !== this.ascending) {
        this.predicate = predicate;
        this.ascending = ascending;
        this.loadPage(pageNumber, true);
      }
    });
  }

  protected onSuccess(data: IAppEvent[] | null, headers: HttpHeaders, page: number, navigate: boolean): void {
    this.totalItems = Number(headers.get('X-Total-Count'));
    this.page = page;
    if (navigate) {
      this.router.navigate(['/event'], {
        queryParams: {
          page: this.page,
          size: this.itemsPerPage,
          sort: this.predicate + ',' + (this.ascending ? 'asc' : 'desc'),
        },
      });
    }
    this.events = data ?? [];
    this.ngbPaginationPage = this.page;
  }

  protected onError(): void {
    this.ngbPaginationPage = this.page ?? 1;
  }
}
