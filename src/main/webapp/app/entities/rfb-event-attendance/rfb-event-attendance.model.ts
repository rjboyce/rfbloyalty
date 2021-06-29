import * as dayjs from 'dayjs';
import { IRfbEvent } from 'app/entities/rfb-event/rfb-event.model';
import { IApplicationUser } from 'app/entities/application-user/application-user.model';

export interface IRfbEventAttendance {
  id?: number;
  attendanceDate?: dayjs.Dayjs | null;
  rfbEvent?: IRfbEvent | null;
  applicationUser?: IApplicationUser | null;
}

export class RfbEventAttendance implements IRfbEventAttendance {
  constructor(
    public id?: number,
    public attendanceDate?: dayjs.Dayjs | null,
    public rfbEvent?: IRfbEvent | null,
    public applicationUser?: IApplicationUser | null
  ) {}
}

export function getRfbEventAttendanceIdentifier(rfbEventAttendance: IRfbEventAttendance): number | undefined {
  return rfbEventAttendance.id;
}
