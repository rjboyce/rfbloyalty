export interface IEventAttendance {
  id?: number;
  signIn?: string;
  signOut?: string;
  userCode?: string;
  eventId?: number | null;
  volunteerId?: string | null;
}

export class EventAttendance implements IEventAttendance {
  constructor(
    public id?: number,
    public signIn?: string,
    public signOut?: string,
    public userCode?: string,
    public eventId?: number | null,
    public volunteerId?: string | null
  ) {}
}

export function getEventAttendanceIdentifier(eventAttendance: IEventAttendance): number | undefined {
  return eventAttendance.id;
}
