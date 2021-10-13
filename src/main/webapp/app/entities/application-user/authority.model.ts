export interface IAuthority {
  name?: string;
}

export class Authority implements IAuthority {
  constructor(public name?: string) {}
}

export function getApplicationUserIdentifier(authority: IAuthority): string | undefined {
  return authority.name;
}
