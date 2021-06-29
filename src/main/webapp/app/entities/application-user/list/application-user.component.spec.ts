import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { ApplicationUserService } from '../service/application-user.service';

import { ApplicationUserComponent } from './application-user.component';

describe('Component Tests', () => {
  describe('ApplicationUser Management Component', () => {
    let comp: ApplicationUserComponent;
    let fixture: ComponentFixture<ApplicationUserComponent>;
    let service: ApplicationUserService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [ApplicationUserComponent],
      })
        .overrideTemplate(ApplicationUserComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(ApplicationUserComponent);
      comp = fixture.componentInstance;
      service = TestBed.inject(ApplicationUserService);

      const headers = new HttpHeaders().append('link', 'link;link');
      spyOn(service, 'query').and.returnValue(
        of(
          new HttpResponse({
            body: [{ id: 123 }],
            headers,
          })
        )
      );
    });

    it('Should call load all on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.applicationUsers?.[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
