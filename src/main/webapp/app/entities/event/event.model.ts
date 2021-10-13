import * as dayjs from 'dayjs';

export interface IAppEvent {
  id?: number;
  eventDate?: dayjs.Dayjs | null;
  startTime?: string | null;
  eventName?: string | null;
  venue?: string | null;
  projection?: number | null;
  locationId?: number | null;
}

export class AppEvent implements IAppEvent {
  constructor(
    public id?: number,
    public eventDate?: dayjs.Dayjs | null,
    public startTime?: string | null,
    public eventName?: string | null,
    public venue?: string | null,
    public projection?: number | null,
    public locationId?: number | null
  ) {}
}

export function getEventIdentifier(event: IAppEvent): number | undefined {
  return event.id;
}
