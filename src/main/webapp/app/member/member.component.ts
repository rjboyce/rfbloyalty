import { Component, OnInit } from '@angular/core';
import { LoginService } from 'app/login/login.service';
import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/auth/account.model';
import { ApplicationUserService } from 'app/entities/application-user/service/application-user.service';
import { concatMap } from 'rxjs/operators';
import { forkJoin, of } from 'rxjs';
import { ApplicationUser } from 'app/entities/application-user/application-user.model';
import { Authority } from 'app/entities/application-user/authority.model';

@Component({
  selector: 'jhi-member',
  templateUrl: './member.component.html',
  styleUrls: ['./member.component.scss'],
})
export class MemberComponent implements OnInit {
  account: Account | null = null;
  user: ApplicationUser | null = null;

  constructor(
    private accountService: AccountService,
    private loginService: LoginService,
    protected applicationUserService: ApplicationUserService
  ) {}

  ngOnInit(): void {
    this.accountService
      .identity()
      .pipe(
        concatMap(a => {
          if (a?.login) {
            return forkJoin([this.applicationUserService.findByLogin(a?.login), of(a)]);
          } else {
            return forkJoin([of(new ApplicationUser()), of(a)]);
          }
        })
      )
      .subscribe(([u, account]) => {
        this.account = account;
        this.user = u;
      });
  }

  isAuthenticated(): boolean {
    return this.accountService.isAuthenticated();
  }

  login(): void {
    this.loginService.login();
  }

  validRole(roles: string[]): boolean {
    let validRole = false;

    roles.forEach(r => {
      if (this.user?.authorities?.includes(<Authority>this.user?.authorities?.find(x => x?.name === r))) {
        validRole = true;
        return;
      }
    });

    return validRole;
  }
}
