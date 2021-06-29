import { IRfbLocation } from 'app/entities/rfb-location/rfb-location.model';
import { IRfbEventAttendance } from 'app/entities/rfb-event-attendance/rfb-event-attendance.model';

export interface IApplicationUser {
  id?: number;
  login?: string | null;
  firstName?: string | null;
  lastName?: string | null;
  email?: string | null;
  langKey?: string | null;
  imageUrl?: string | null;
  homeLocation?: IRfbLocation | null;
  rfbEventAttendances?: IRfbEventAttendance[] | null;
}

export class ApplicationUser implements IApplicationUser {
  constructor(
    public id?: number,
    public login?: string | null,
    public firstName?: string | null,
    public lastName?: string | null,
    public email?: string | null,
    public langKey?: string | null,
    public imageUrl?: string | null,
    public homeLocation?: IRfbLocation | null,
    public rfbEventAttendances?: IRfbEventAttendance[] | null
  ) {}
}

export function getApplicationUserIdentifier(applicationUser: IApplicationUser): number | undefined {
  return applicationUser.id;
}
