export interface IMyLocation {
  id?: number;
  locationName?: string;
}

export class MyLocation implements IMyLocation {
  constructor(public id?: number, public locationName?: string) {}
}

export function getLocationIdentifier(location: IMyLocation): number | undefined {
  return location.id;
}
