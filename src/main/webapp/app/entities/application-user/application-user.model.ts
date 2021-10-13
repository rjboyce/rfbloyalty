import { IAuthority } from 'app/entities/application-user/authority.model';

export interface IApplicationUser {
  id?: string;
  login?: string;
  firstName?: string | null;
  lastName?: string | null;
  email?: string | null;
  phoneNumber?: string | null;
  langKey?: string | null;
  imageUrl?: string | null;
  homeLocationId?: number | null;
  activated?: boolean;
  authorities?: IAuthority[] | null;
  availability?: string | null;
}

export class ApplicationUser implements IApplicationUser {
  constructor(
    public id?: string,
    public login?: string,
    public firstName?: string | null,
    public lastName?: string | null,
    public email?: string | null,
    public phoneNumber?: string | null,
    public langKey?: string | null,
    public imageUrl?: string | null,
    public homeLocationId?: number | null,
    public activated?: boolean,
    public authorities?: IAuthority[] | null,
    public availability?: string | null
  ) {}
}

export function getApplicationUserIdentifier(applicationUser: IApplicationUser): string {
  return applicationUser.id ?? '';
}
