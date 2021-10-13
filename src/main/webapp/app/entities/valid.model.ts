export interface IValid {
  value?: boolean;
}

export class Valid implements IValid {
  constructor(public value?: boolean) {}
}
