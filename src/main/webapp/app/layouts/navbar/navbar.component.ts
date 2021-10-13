import { Component, OnInit } from '@angular/core';
import { Router, NavigationStart, Event as NavigationEvent } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { SessionStorageService } from 'ngx-webstorage';
import { VERSION } from 'app/app.constants';
import { LANGUAGES } from 'app/config/language.constants';
import { AccountService } from 'app/core/auth/account.service';
import { LoginService } from 'app/login/login.service';
import { ProfileService } from 'app/layouts/profiles/profile.service';
import { ApplicationUser } from 'app/entities/application-user/application-user.model';
import { filter, mergeMap } from 'rxjs/operators';
import { ApplicationUserService } from 'app/entities/application-user/service/application-user.service';
import { Account } from 'app/core/auth/account.model';
import { Authority } from '../../entities/application-user/authority.model';

@Component({
  selector: 'jhi-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss'],
})
export class NavbarComponent implements OnInit {
  inProduction?: boolean;
  isNavbarCollapsed = true;
  languages = LANGUAGES;
  openAPIEnabled?: boolean;
  theUser?: ApplicationUser;
  userId?: string;
  version = '';
  account: Account | null = null;

  constructor(
    private loginService: LoginService,
    private translateService: TranslateService,
    private sessionStorage: SessionStorageService,
    private accountService: AccountService,
    private profileService: ProfileService,
    private router: Router,
    private applicationUserService: ApplicationUserService
  ) {
    if (VERSION) {
      this.version = VERSION.toLowerCase().startsWith('v') ? VERSION : 'v' + VERSION;
    }

    // create an event listener for updating navbar
    this.router.events.pipe(filter(event => event instanceof NavigationStart)).subscribe((event: NavigationEvent) => {
      console.log('nav event: ', event);
      // TODO: enhance this functionality for better performance and find better use for NavigationEvent
      this.applicationUserService.retrieveUser(this.theUser?.id as string).subscribe(x => (this.theUser = x));
    });
  }

  ngOnInit(): void {
    this.accountService
      .identity()
      .pipe(
        mergeMap(account => {
          this.account = account;
          return this.profileService.getProfileInfo();
        })
      )
      .subscribe(profileInfo => {
        this.inProduction = profileInfo.inProduction;
        this.openAPIEnabled = profileInfo.openAPIEnabled;

        if (this.isAuthenticated()) {
          this.loadAccountInfo();
        }
      });
  }

  validRole(roles: string[]): boolean {
    let validRole = false;

    roles.forEach(r => {
      if (this.theUser?.authorities?.includes(<Authority>this.theUser?.authorities?.find(x => x?.name === r))) {
        validRole = true;
        return;
      }
    });

    return validRole;
  }

  loadAccountInfo(): void {
    this.applicationUserService.findByLogin(this.account!.login).subscribe(x => {
      this.theUser = x;
    });
  }

  changeLanguage(languageKey: string): void {
    this.sessionStorage.store('locale', languageKey);
    this.translateService.use(languageKey);
  }

  collapseNavbar(): void {
    this.isNavbarCollapsed = true;
  }

  isAuthenticated(): boolean {
    return this.accountService.isAuthenticated();
  }

  login(): void {
    this.loginService.login();
  }

  logout(): void {
    this.collapseNavbar();
    this.loginService.logout();
  }

  toggleNavbar(): void {
    this.isNavbarCollapsed = !this.isNavbarCollapsed;
  }

  getImageUrl(): string {
    return this.isAuthenticated() ? this.accountService.getImageUrl() : '';
  }
}
