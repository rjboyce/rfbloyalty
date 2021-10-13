import { TestBed } from '@angular/core/testing';

import { ApplicationUserService } from './application-user.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('ApplicationUserService', () => {
  beforeEach(() => TestBed.configureTestingModule({ imports: [HttpClientTestingModule], providers: [ApplicationUserService] }));

  it('should be created', () => {
    const service: ApplicationUserService = TestBed.inject(ApplicationUserService);
    expect(service).toBeTruthy();
  });
});
